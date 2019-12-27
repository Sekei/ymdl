package com.ymdl;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

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

public class MainActivity extends BaseActivity {
    @BindView(R.id.user_pwd)
    EditText userPwd;
    @BindView(R.id.user_name)
    EditText userName;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        if (!TextUtils.isEmpty(SpUtil.getString(this, "userID"))) {
            startActivity(HomeActivity.class);
            finish();
        }
    }

    @OnClick({R.id.login_btn, R.id.change_password, R.id.register})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.login_btn:
                String userID = userName.getText().toString().trim();
                String passWord = userPwd.getText().toString().trim();
                if (TextUtils.isEmpty(userID)) {
                    ToastUtils.showToast(this, "账号不能为空~");
                    return;
                }
                if (TextUtils.isEmpty(passWord)) {
                    ToastUtils.showToast(this, "密码不能为空~");
                    return;
                }
                Map<String, String> map = new HashMap<>();
                map.put("userID", userID);
                map.put("passWord", passWord);
                String body = ApiNode.getRequestBody("logIn", map);
                addRxDestroy(RetrofitSoapClient.getInstance().service
                        .getlogIn(body)
                        .compose(RxSchedulers.io_main())
                        .subscribeWith(new DisposableObserver<ResponseBody>() {
                            @Override
                            public void onNext(ResponseBody responseBody) {
                                try {
                                    List<String> loginBean = XMLUtil.strXmlToData(responseBody.string(), "logInResult");
                                    System.out.println("API数据：=====>>>" + loginBean.toString());
                                    if (loginBean.contains("OK")) {
                                        ToastUtils.showToast(MainActivity.this, "登录成功~");
                                        SpUtil.putString(MainActivity.this, "userID", userID);
                                        SpUtil.putString(MainActivity.this, "userName", loginBean.get(0));
                                        SpUtil.putString(MainActivity.this, "factoryName", loginBean.get(1));
                                        startActivity(HomeActivity.class);
                                        finish();
                                    } else {
                                        ToastUtils.showToast(MainActivity.this, "登录失败，账号或密码错误~");
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
            case R.id.change_password:
                //更改密码
                startActivity(ChangePwdActivity.class);
                break;
            case R.id.register:
                //注册
                startActivity(RegisterActivity.class);
                break;
        }
    }

}
