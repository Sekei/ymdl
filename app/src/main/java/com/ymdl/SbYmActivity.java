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
import com.ymdl.utils.SpUtil;
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

public class SbYmActivity extends BaseActivity {
    @BindView(R.id.right_icon)
    ImageView rightImage;
    @BindView(R.id.equip_no)
    EditText equipNo;
    @BindView(R.id.equip_name)
    EditText equipName;
    @BindView(R.id.factory_name)
    TextView factoryName;

    private TransferBean transferBean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sbym;
    }

    @Override
    public void initView() {
        rightImage.setBackground(ContextCompat.getDrawable(this, R.mipmap.bqbz_01));
        transferBean = new TransferBean();
        transferBean.setJumpType("0");
        //赋值工厂名称（注册人相关的且不可修改）
        System.out.println("================》》" + SpUtil.getString(this, "factoryName"));
        factoryName.setText(SpUtil.getString(this, "factoryName"));
    }

    @OnClick({R.id.back_btn, R.id.right_layout, R.id.commit_btn})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.right_layout:
                startActivity(YmQueryActivity.class, "0");
                break;
            case R.id.commit_btn:
                //易码生成
                String equipNoStr = equipNo.getText().toString().trim();
                String equipNameStr = equipName.getText().toString().trim();
                String factoryNameStr = factoryName.getText().toString().trim();
                transferBean.setEquNo(equipNoStr);
                transferBean.setEquName(equipNameStr);
                transferBean.setFactoryName(factoryNameStr);
                if (TextUtils.isEmpty(factoryNameStr)) {
                    ToastUtils.showToast(this, "工厂名称不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(equipNameStr)) {
                    ToastUtils.showToast(this, "设备名称不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(equipNoStr)) {
                    ToastUtils.showToast(this, "设备编号不能为空~");
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("equipNo", equipNoStr);
                map.put("equipName", equipNameStr);
                map.put("factoryName", factoryNameStr);
                String body = ApiNode.getRequestBody("newEquipSignIn_MC", map);
                addRxDestroy(RetrofitSoapClient.getInstance().service
                        .getNewEquipSignInMC(body)
                        .compose(RxSchedulers.io_main())
                        .subscribeWith(new DisposableObserver<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    List<String> newEquipSignInMC = XMLUtil.strXmlToData(responseBody.string(), "newEquipSignIn_MCResult");
                                    System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                                    if (newEquipSignInMC.contains("OK")) {
                                        ToastUtils.showToast(SbYmActivity.this, "设备易码生成成功~");
                                        transferBean.setEquipMasterCode(newEquipSignInMC.get(0));
                                        startActivity(SbYmSuccessActivity.class, transferBean);
                                        finish();
                                    } else {
                                        ToastUtils.showToast(SbYmActivity.this, newEquipSignInMC.get(0));
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                            }

                            @Override
                            public void onComplete() {
                            }
                        }));
                break;
        }
    }

}
