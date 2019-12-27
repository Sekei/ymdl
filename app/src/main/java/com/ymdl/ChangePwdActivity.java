package com.ymdl;

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

public class ChangePwdActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView topTitle;
    @BindView(R.id.user_id)
    EditText userId;
    @BindView(R.id.user_pwd)
    EditText userPwd;
    @BindView(R.id.user_new_pwd)
    EditText userNewPwd;
    @BindView(R.id.new_pswd)
    EditText newPswd;


    @Override
    public int getLayoutId() {
        return R.layout.activity_change_password;
    }

    @Override
    public void initView() {
        topTitle.setText("更改密码");
    }

    @OnClick({R.id.back_btn, R.id.change_pswd, R.id.user_delete})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.change_pswd: {
                //修改
                String userID = userId.getText().toString().trim();
                String oldPswd = userPwd.getText().toString().trim();
                String userNewP = userNewPwd.getText().toString().trim();
                String newPD = newPswd.getText().toString().trim();
                if (TextUtils.isEmpty(userID)) {
                    ToastUtils.showToast(this, "当前账号不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(oldPswd)) {
                    ToastUtils.showToast(this, "当前不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(userNewP)) {
                    ToastUtils.showToast(this, "新密码不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(newPD)) {
                    ToastUtils.showToast(this, "第二次密码不能为空~");
                    return;
                }
                if (!userNewP.equals(newPD)) {
                    ToastUtils.showToast(this, "二次密码不一致，请重新输入~");
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("userID", userID);
                map.put("oldPswd", oldPswd);
                map.put("newPswd", newPD);
                String body = ApiNode.getRequestBody("changePswd", map);
                addRxDestroy(RetrofitSoapClient.getInstance().service
                        .getChangePswd(body)
                        .compose(RxSchedulers.io_main())
                        .subscribeWith(new DisposableObserver<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    List<String> changePswd = XMLUtil.strXmlToData(responseBody.string(), "changePswdResult");
                                    System.out.println("API数据：=====>>>" + changePswd.toString());
                                    if (changePswd.contains("OK")) {
                                        ToastUtils.showToast(ChangePwdActivity.this, "修改成功，请重新登录~");
                                        finish();
                                    } else {
                                        ToastUtils.showToast(ChangePwdActivity.this, "修改失败，请核对账号或密码~");
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
            case R.id.user_delete: {
                //注销
                String userID = userId.getText().toString().trim();
                String oldPswd = userPwd.getText().toString().trim();
                String userNewP = userNewPwd.getText().toString().trim();
                String newPD = newPswd.getText().toString().trim();
                if (TextUtils.isEmpty(userID)) {
                    ToastUtils.showToast(this, "当前账号不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(oldPswd)) {
                    ToastUtils.showToast(this, "当前不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(userNewP)) {
                    ToastUtils.showToast(this, "新密码不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(newPD)) {
                    ToastUtils.showToast(this, "第二次密码不能为空~");
                    return;
                }
                if (!userNewP.equals(newPD)) {
                    ToastUtils.showToast(this, "二次密码不一致，请重新输入~");
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("userID", userID);
                map.put("userPswd", oldPswd);
                String body = ApiNode.getRequestBody("userIDelete", map);
                addRxDestroy(RetrofitSoapClient.getInstance().service
                        .getUserIDelete(body)
                        .compose(RxSchedulers.io_main())
                        .subscribeWith(new DisposableObserver<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    List<String> changePswd = XMLUtil.strXmlToData(responseBody.string(), "userIDeleteResult");
                                    System.out.println("API数据：=====>>>" + changePswd.toString());
                                    if (changePswd.contains("OK")) {
                                        ToastUtils.showToast(ChangePwdActivity.this, "账号注销成功~");
                                        finish();
                                    } else {
                                        ToastUtils.showToast(ChangePwdActivity.this, "账号或密码错误，请重新输入~");
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

}
