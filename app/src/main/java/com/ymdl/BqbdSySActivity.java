package com.ymdl;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.View;
import android.widget.TextView;

import com.ymdl.base.BaseActivity;
import com.ymdl.retrofit.RetrofitSoapClient;
import com.ymdl.retrofit.RxSchedulers;
import com.ymdl.utils.ApiNode;
import com.ymdl.utils.SpUtil;
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
 * 标签绑定扫一扫
 */
public class BqbdSySActivity extends BaseActivity implements QRCodeView.Delegate {
    @BindView(R.id.sqc_zxing_view)
    ZXingView mZXingView;
    @BindView(R.id.user_name)
    TextView userName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bq_scan;
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
        // 设置扫描二维码的代理，即扫码成功后的结果回调到哪里
        mZXingView.setDelegate(this);
        // 显示扫描框，并开始识别
        mZXingView.startSpotAndShowRect();
        userName.setText(String.format("欢迎%s进入易联标签绑定，", SpUtil.getString(this, "userName")));
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        //pro为产品，equ为设备
        if (result.contains("p") || result.contains("P")) {
            //产品查询
            Map<String, String> map = new HashMap<>();
            map.put("proMasterCode", result);
            String body = ApiNode.getRequestBody("checkProMCIsUsed", map);
            addRxDestroy(RetrofitSoapClient.getInstance().service
                    .getCheckProMasterCode(body)
                    .compose(RxSchedulers.io_main())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                        @TargetApi(Build.VERSION_CODES.O)
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                List<String> newEquipSignInMC = XMLUtil.strXmlToData(responseBody.string(), "checkProMCIsUsedResult");
                                System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                                if (newEquipSignInMC.contains("OK")) {
                                    startActivity(BqBdActivity.class, result);
                                    finish();
                                } else {
                                    String msg = newEquipSignInMC.get(0).replace("fai", "");
                                    ToastUtils.showToast(BqbdSySActivity.this, msg);
                                    mZXingView.startSpot();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showToast(BqbdSySActivity.this, "易联标签查询失败~");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    }));
        } else {
            ToastUtils.showToast(this, "请扫描易联标签~");
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
