package com.ymdl;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
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
 * 易码当联产品扫一扫
 */
public class YM_CP_SyS_Activity extends BaseActivity implements QRCodeView.Delegate {
    @BindView(R.id.sqc_zxing_view)
    ZXingView mZXingView;
    @BindView(R.id.user_name)
    TextView userName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ym_cp_sys;
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
        if (result.contains("p")|| result.contains("P")) {
            //产品查询
            Map<String, String> map = new HashMap<>();
            map.put("proMasterCode", result);
            String body = ApiNode.getRequestBody("getProInfo", map);
            addRxDestroy(RetrofitSoapClient.getInstance().service
                    .getProInfo(body)
                    .compose(RxSchedulers.io_main())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                        @TargetApi(Build.VERSION_CODES.O)
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                List<String> newEquipSignInMC = XMLUtil.strXmlToData(responseBody.string(), "getProInfoResult");
                                System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                                if (newEquipSignInMC.contains("OK")) {
                                    String equipSign = String.join(",", newEquipSignInMC);
                                    startActivity(YM_SB_SyS_Activity.class, equipSign, result);
                                    finish();
                                } else {
                                    ToastUtils.showToast(YM_CP_SyS_Activity.this, "产品易码查询失败~");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showToast(YM_CP_SyS_Activity.this, "产品易码查询失败~");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    }));
        } else {
            ToastUtils.showToast(this, "请扫描正确的产品易码~");
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
