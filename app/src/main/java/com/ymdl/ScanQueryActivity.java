package com.ymdl;

import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.ymdl.base.BaseActivity;
import com.ymdl.bean.TransferBean;
import com.ymdl.retrofit.RetrofitSoapClient;
import com.ymdl.retrofit.RxSchedulers;
import com.ymdl.utils.ApiNode;
import com.ymdl.utils.ToastUtils;
import com.ymdl.utils.XMLUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * 扫描查询
 */
public class ScanQueryActivity extends BaseActivity implements QRCodeView.Delegate {
    @BindView(R.id.sqc_zxing_view)
    ZXingView mZXingView;


    private TransferBean transferBean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_scan_query;
    }


    @Override
    public void onStop() {
        mZXingView.stopCamera();
        super.onStop();
    }


    @Override
    public void onDestroy() {
        mZXingView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        // 设置扫描二维码的代理，即扫码成功后的结果回调到哪里
        mZXingView.setDelegate(this);
        // 显示扫描框，并开始识别
        mZXingView.startSpotAndShowRect();
        transferBean = new TransferBean();
        transferBean.setJumpType("2");
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        transferBean.setEquipMasterCode(result);
        transferBean.setProMasterCode(result);
        //pro为产品，equ为设备
        if (result.contains("p")|| result.contains("P")) {
            //产品查询
            Map<String, String> map = new HashMap<>();
            map.put("proMasterCode", result);
            String body = ApiNode.getRequestBody("getProInfo", map);
            addRxDestroy(RetrofitSoapClient.getInstance().service
                    .getProInfo(body)
                    .compose(RxSchedulers.io_main())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                List<String> newEquipSignInMC = XMLUtil.strXmlToData(responseBody.string(), "getProInfoResult");
                                System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                                if (newEquipSignInMC.contains("OK")) {
                                    ToastUtils.showToast(ScanQueryActivity.this, "产品易码查询成功~");
                                    transferBean.setFactoryName(newEquipSignInMC.get(4));
                                    transferBean.setProName(newEquipSignInMC.get(1));
                                    transferBean.setProNo(newEquipSignInMC.get(0));
                                    transferBean.setProNum(newEquipSignInMC.get(2));
                                    transferBean.setProUnit(newEquipSignInMC.get(3));
                                    startActivity(CpYmSuccessActivity.class, transferBean);
                                    finish();
                                } else {
                                    mZXingView.startSpot();
                                    ToastUtils.showToast(ScanQueryActivity.this, "查询失败，请检查产品易码是否正确~");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            mZXingView.startSpot();
                            ToastUtils.showToast(ScanQueryActivity.this, "易码查询失败~");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    }));
        } else if (result.contains("e")|| result.contains("E")) {
            //设备查询
            Map<String, String> map = new HashMap<>();
            map.put("equipMasterCode", result);
            String body = ApiNode.getRequestBody("getEquipInfo", map);
            addRxDestroy(RetrofitSoapClient.getInstance().service
                    .getEquipInfo(body)
                    .compose(RxSchedulers.io_main())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                List<String> newEquipSignInMC = XMLUtil.strXmlToData(responseBody.string(), "getEquipInfoResult");
                                System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                                if (newEquipSignInMC.contains("OK")) {
                                    ToastUtils.showToast(ScanQueryActivity.this, "设备易码查询成功~");
                                    transferBean.setEquNo(newEquipSignInMC.get(0));
                                    transferBean.setEquName(newEquipSignInMC.get(1));
                                    transferBean.setFactoryName(newEquipSignInMC.get(2));
                                    startActivity(SbYmSuccessActivity.class, transferBean);
                                    finish();
                                } else {
                                    ToastUtils.showToast(ScanQueryActivity.this, "查询失败，请检查设备易码是否正确~");
                                    mZXingView.startSpot(); }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showToast(ScanQueryActivity.this, "易码查询失败~");
                            mZXingView.startSpot();
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    }));
        } else {
            ToastUtils.showToast(this, "易码格式不正确~");
            mZXingView.startSpot();
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }


    @OnClick({R.id.back_btn})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
        }
    }


}
