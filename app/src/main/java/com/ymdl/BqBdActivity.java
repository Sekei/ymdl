package com.ymdl;

import android.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
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
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * 标签绑定
 */
public class BqBdActivity extends BaseActivity {
    @BindView(R.id.ym_no)
    TextView ymNo;
    @BindView(R.id.factory_name)
    EditText factoryName;
    @BindView(R.id.pro_name)
    EditText proName;
    @BindView(R.id.pro_no)
    EditText proNo;
    @BindView(R.id.pro_num)
    EditText proNum;
    @BindView(R.id.pro_unit)
    TextView proUnit;

    private String proUnitStr;
    private String equipmentID;
    //单选框
    private AlertDialog alertDialog;
    private String[] items = {"个", "米", "码", "公斤", "箱", "件"};

    @Override
    public int getLayoutId() {
        return R.layout.activity_bqbd;
    }

    @Override
    public void initView() {
        equipmentID = getIntentObject(String.class);
        ymNo.setText(String.format("(易码：%s)", equipmentID));
    }


    @OnClick({R.id.back_btn, R.id.commit_btn, R.id.pro_unit})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.pro_unit:
                //单位选择
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle("产品单位");
                alertBuilder.setSingleChoiceItems(items, -1, (dialogInterface, i) -> {
                    proUnitStr = items[i];
                    proUnit.setText(proUnitStr);
                });
                alertBuilder.setPositiveButton("确定", (dialogInterface, i) -> alertDialog.dismiss());
                alertBuilder.setNegativeButton("取消", (dialogInterface, i) -> alertDialog.dismiss());
                alertDialog = alertBuilder.create();
                alertDialog.show();
                break;
            case R.id.commit_btn:
                //易码生成
                String factoryNameStr = factoryName.getText().toString().trim();
                String proNameStr = proName.getText().toString().trim();
                String proNoStr = proNo.getText().toString().trim();
                String proNumStr = proNum.getText().toString().trim();
                if (TextUtils.isEmpty(factoryNameStr)) {
                    ToastUtils.showToast(this, "工厂名称不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(proNameStr)) {
                    ToastUtils.showToast(this, "产品名称不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(proNoStr)) {
                    ToastUtils.showToast(this, "产品编号不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(proNumStr)) {
                    ToastUtils.showToast(this, "产品数量不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(proUnitStr)) {
                    ToastUtils.showToast(this, "产品单位不能为空~");
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("factoryName", factoryNameStr);
                map.put("proNo", proNoStr);
                map.put("proName", proNameStr);
                map.put("proNum", proNumStr);
                map.put("proUnit", proUnitStr);
                map.put("proMasterCode", equipmentID);
                String body = ApiNode.getRequestBody("newProSignIn_MCBind", map);
                addRxDestroy(RetrofitSoapClient.getInstance().service
                        .getNewProSignIn_MCBind(body)
                        .compose(RxSchedulers.io_main())
                        .subscribeWith(new DisposableObserver<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    List<String> newEquipSignInMC = XMLUtil.strXmlToData(responseBody.string(), "newProSignIn_MCBindResult");
                                    System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                                    if (newEquipSignInMC.contains("OK")) {
                                        startActivity(BqBdSuccessActivity.class);
                                        finish();
                                    } else {
                                        String msg = newEquipSignInMC.get(0).replace("fai", "");
                                        ToastUtils.showToast(BqBdActivity.this, msg);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtils.showToast(BqBdActivity.this, "标签绑定生成失败~");
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
