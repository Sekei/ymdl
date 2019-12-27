package com.ymdl;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.ymdl.base.BaseActivity;
import com.ymdl.utils.SpUtil;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class HomeActivity extends BaseActivity implements Consumer<Boolean> {
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.user_id)
    TextView userID;

    private long mExitTime;
    //0表示扫一扫、1表示标签绑定、2表示易码当联
    private String functionalTypes = "0";

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public void initView() {
        userID.setText(String.format("当前账号：%s", SpUtil.getString(this, "userID")));
        userName.setText(String.format("欢迎%s，使用易码当联", SpUtil.getString(this, "userName")));
    }


    @OnClick({R.id.sbym_layout, R.id.cpym_layout, R.id.smcx_layout, R.id.ymcx_layout,
            R.id.ymdl_layout, R.id.bqbd_layout, R.id.exit_btn})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.sbym_layout:
                //设备易码
                startActivity(SbYmActivity.class);
                break;
            case R.id.cpym_layout:
                //产品易码
                startActivity(CpYmActivity.class);
                break;
            case R.id.smcx_layout: {
                //扫码查询
                functionalTypes = "0";
                RxPermissions rxPermission = new RxPermissions(this);
                rxPermission.request(Manifest.permission.CAMERA).subscribe(this);
                break;
            }
            case R.id.ymcx_layout:
                //易码查询
                startActivity(YmQueryActivity.class, "1");
                break;
            case R.id.ymdl_layout: {
                //易码当联
                functionalTypes = "2";
                RxPermissions rxPermission = new RxPermissions(this);
                rxPermission.request(Manifest.permission.CAMERA).subscribe(this);
                break;
            }
            case R.id.bqbd_layout: {
                //标签绑定
                functionalTypes = "1";
                RxPermissions rxPermission = new RxPermissions(this);
                rxPermission.request(Manifest.permission.CAMERA).subscribe(this);
                break;
            }
            case R.id.exit_btn:
                //退出登录
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("账号退出");
                builder.setMessage("确定退出当前账号？").setPositiveButton("确定", (dialogInterface, i) -> {
                    dialogInterface.dismiss();
                    SpUtil.putString(HomeActivity.this, "userID", "");
                    startActivity(MainActivity.class);
                    finish();
                });
                builder.setNegativeButton("取消", (dialogInterface, i) -> dialogInterface.dismiss());
                builder.create().show();
                break;
        }
    }

    /**
     * 相机权限判断
     *
     * @param aBoolean
     */
    @Override
    public void accept(Boolean aBoolean) {
        if (aBoolean) {
            //0表示扫一扫、1表示标签绑定、2表示易码当联
            if ("0".equals(functionalTypes)) {
                //扫码查询
                startActivity(ScanQueryActivity.class);
            } else if ("1".equals(functionalTypes)) {
                //标签绑定
                startActivity(BqbdSySActivity.class);
            } else if ("2".equals(functionalTypes)) {
                //易码当联
                startActivity(YM_CP_SyS_Activity.class);
            }
        } else {
            Toast.makeText(this, "请先打开相机权限", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 退出App
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_LONG).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
