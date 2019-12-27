package com.ymdl;

import android.view.View;

import com.ymdl.base.BaseActivity;

import butterknife.OnClick;

public class BqBdSuccessActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.activity_bqbd_success;
    }

    @Override
    public void initView() {

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
