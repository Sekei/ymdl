package com.ymdl;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

public class YmQueryActivity extends BaseActivity {
    @BindView(R.id.top_image)
    ImageView topImage;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.master_code)
    EditText masterCode;

    private TransferBean transferBean;
    private String masterCodeStr;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ymquery;
    }

    @Override
    public void initView() {
        //0表示补签，1表示正常的查询
        topImage.setBackground(ContextCompat.getDrawable(this, "0".equals(getIntentObject(String.class)) ? R.mipmap.bai_bqbz : R.mipmap.bai_ymcx));
        topTitle.setText("0".equals(getIntentObject(String.class)) ? "标签补制" : "易码查询");
        transferBean = new TransferBean();
        transferBean.setJumpType("0".equals(getIntentObject(String.class)) ? "1" : "2");
    }

    @OnClick({R.id.back_btn, R.id.commit_btn})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.commit_btn:
                //易码生成
                masterCodeStr = masterCode.getText().toString().trim();
                transferBean.setEquipMasterCode(masterCodeStr);
                transferBean.setProMasterCode(masterCodeStr);
                if (TextUtils.isEmpty(masterCodeStr)) {
                    ToastUtils.showToast(this, "易码不能为空~");
                    return;
                }
                if (masterCodeStr.length() < 3) {
                    ToastUtils.showToast(this, "易码位数不正确~");
                    return;
                }
                //pro为产品，equ为设备
                if (masterCodeStr.contains("p") || masterCodeStr.contains("P")) {
                    //产品查询
                    Map<String, String> map = new HashMap<>();
                    map.put("proMasterCode", masterCodeStr);
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
                                            ToastUtils.showToast(YmQueryActivity.this, "产品易码查询成功~");
                                            transferBean.setFactoryName(newEquipSignInMC.get(4));
                                            transferBean.setProName(newEquipSignInMC.get(1));
                                            transferBean.setProNo(newEquipSignInMC.get(0));
                                            transferBean.setProNum(newEquipSignInMC.get(2));
                                            transferBean.setProUnit(newEquipSignInMC.get(3));
                                            startActivity(CpYmSuccessActivity.class, transferBean);
                                            finish();
                                        } else {
                                            ToastUtils.showToast(YmQueryActivity.this, "产品易码查询失败~");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    ToastUtils.showToast(YmQueryActivity.this, "易码查询失败~");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onComplete() {
                                }
                            }));
                } else if (masterCodeStr.contains("e") || masterCodeStr.contains("E")) {
                    //设备查询
                    Map<String, String> map = new HashMap<>();
                    map.put("equipMasterCode", masterCodeStr);
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
                                            ToastUtils.showToast(YmQueryActivity.this, "设备易码查询成功~");
                                            transferBean.setEquNo(newEquipSignInMC.get(0));
                                            transferBean.setEquName(newEquipSignInMC.get(1));
                                            transferBean.setFactoryName(newEquipSignInMC.get(2));
                                            startActivity(SbYmSuccessActivity.class, transferBean);
                                            finish();
                                        } else {
                                            ToastUtils.showToast(YmQueryActivity.this, "设备易码查询失败~");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    ToastUtils.showToast(YmQueryActivity.this, "易码查询失败~");
                                    e.printStackTrace();
                                }

                                @Override
                                public void onComplete() {
                                }
                            }));
                } else {
                    ToastUtils.showToast(this, "易码格式不正确~");
                }
                break;
        }
    }

}
