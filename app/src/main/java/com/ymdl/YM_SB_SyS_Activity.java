package com.ymdl;

import android.view.View;
import android.widget.TextView;

import com.ymdl.base.BaseActivity;
import com.ymdl.bean.TransferBean;
import com.ymdl.retrofit.RetrofitSoapClient;
import com.ymdl.retrofit.RxSchedulers;
import com.ymdl.utils.ApiNode;
import com.ymdl.utils.SpUtil;
import com.ymdl.utils.ToastUtils;
import com.ymdl.utils.XMLUtil;

import java.io.IOException;
import java.util.Arrays;
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
 * 易码当联设备扫一扫
 */
public class YM_SB_SyS_Activity extends BaseActivity implements QRCodeView.Delegate {
    @BindView(R.id.sqc_zxing_view)
    ZXingView mZXingView;
    @BindView(R.id.pro_name)
    TextView proName;
    @BindView(R.id.pro_no)
    TextView proNo;
    @BindView(R.id.pro_num)
    TextView proNum;
    @BindView(R.id.pro_unit)
    TextView proUnit;


    //产品二维码数据
    private List<String> newEquipSignInMC;

    private TransferBean transferBean;
    private String proMasterCode;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ym_sb_sys;
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
        String equipSign = getIntentObject(String.class);
        newEquipSignInMC = Arrays.asList(equipSign.split(","));
        proName.setText(String.format("产品名称：%s", newEquipSignInMC.get(1)));
        proNum.setText(String.format("产品数量：%s", newEquipSignInMC.get(2)));
        proNo.setText(String.format("产品编号：%s", newEquipSignInMC.get(0)));
        proUnit.setText(String.format("产品单位：%s", newEquipSignInMC.get(3)));
        transferBean = new TransferBean();
        transferBean.setProNo(newEquipSignInMC.get(0));
        transferBean.setFactoryName(newEquipSignInMC.get(4));
        transferBean.setProNum(newEquipSignInMC.get(2));
        transferBean.setProUnit(newEquipSignInMC.get(3));
        proMasterCode = getIntentObject(String.class, 1);
        transferBean.setProMasterCode(proMasterCode);
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        //pro为产品，equ为设备
        if (result.contains("e")|| result.contains("E")) {
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
                                    //ToastUtils.showToast(YM_SB_SyS_Activity.this, "设备易码查询成功~");
                                    transferBean.setEquNo(newEquipSignInMC.get(0));
                                    //重新绑定（产品+设备编码绑定）
                                    setInputBindInfo(result);
                                } else {
                                    ToastUtils.showToast(YM_SB_SyS_Activity.this, "设备易码查询失败~");
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showToast(YM_SB_SyS_Activity.this, "易码查询失败~");
                            e.printStackTrace();
                        }

                        @Override
                        public void onComplete() {
                        }
                    }));
        } else {
            ToastUtils.showToast(this, "请扫描正确的设备易码~");
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

    /**
     * 设备+输入产品标签绑定
     *
     * @param equipMasterCode
     */
    private void setInputBindInfo(String equipMasterCode) {
        String userID = SpUtil.getString(this, "userID");
        Map<String, String> map = new HashMap<>();
        map.put("proMasterCode", proMasterCode);
        map.put("equipMasterCode", equipMasterCode);
        map.put("userID", userID);
        String body = ApiNode.getRequestBody("inputBindInfo", map);
        addRxDestroy(RetrofitSoapClient.getInstance().service
                .getInputBindInfo(body)
                .compose(RxSchedulers.io_main())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            List<String> newEquipSignInMC = XMLUtil.strXmlToData(responseBody.string(), "inputBindInfoResult");
                            System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                            if (newEquipSignInMC.contains("OK")) {
                                startActivity(YmdlOkActivity.class, transferBean);
                                finish();
                            } else {
                                ToastUtils.showToast(YM_SB_SyS_Activity.this, "绑定失败~");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showToast(YM_SB_SyS_Activity.this, "绑定失败~");
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {
                    }
                }));
    }

}
