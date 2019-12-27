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

public class RegisterActivity extends BaseActivity {
    @BindView(R.id.title)
    TextView topTitle;
    @BindView(R.id.user_id)
    EditText userId;
    @BindView(R.id.user_pwd)
    EditText userPwd;
    @BindView(R.id.pass_word)
    EditText passWord;
    @BindView(R.id.user_name)
    EditText userName;
    @BindView(R.id.factory_name)
    EditText factoryName;


    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        topTitle.setText("注  册");
    }

    @OnClick({R.id.back_btn, R.id.sign_in})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.sign_in:
                //注册
                String userID = userId.getText().toString().trim();
                String pwd = userPwd.getText().toString().trim();
                String passW = passWord.getText().toString().trim();
                String userN = userName.getText().toString().trim();
                String factoryN = factoryName.getText().toString().trim();
                if (TextUtils.isEmpty(userID)) {
                    ToastUtils.showToast(this, "手机号码不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtils.showToast(this, "密码不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(passW)) {
                    ToastUtils.showToast(this, "确认密码不能为空~");
                    return;
                }
                if (!pwd.equals(passW)) {
                    ToastUtils.showToast(this, "二次密码不一致~");
                    return;
                }
                if (TextUtils.isEmpty(userN)) {
                    ToastUtils.showToast(this, "姓名不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(factoryN)) {
                    ToastUtils.showToast(this, "工厂名称不能为空~");
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("userID", userID);
                map.put("userName", userN);
                map.put("passWord", passW);
                map.put("factoryName", factoryN);
                String body = ApiNode.getRequestBody("newUserSignIn", map);
                addRxDestroy(RetrofitSoapClient.getInstance().service
                        .getNewUserSignIn(body)
                        .compose(RxSchedulers.io_main())
                        .subscribeWith(new DisposableObserver<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    List<String> newUserSignIn = XMLUtil.strXmlToData(responseBody.string(), "newUserSignInResult");
                                    System.out.println("API数据：=====>>>" + newUserSignIn.toString());
                                    if (newUserSignIn.contains("OK")) {
                                        ToastUtils.showToast(RegisterActivity.this, "注册成功~");
                                        finish();
                                    } else {
                                        ToastUtils.showToast(RegisterActivity.this, "该账号已注册，请选择新账号~");
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
