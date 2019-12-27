package com.ymdl.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.alibaba.fastjson.JSON;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by LENOVO on 2019/11/27.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private CompositeDisposable disposables2Stop;// 管理Stop取消订阅者者
    private CompositeDisposable disposables2Destroy;// 管理Destroy取消订阅者者
    private Unbinder unbinder;
    //Oss文件命名
    public String pathName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        unbinder = ButterKnife.bind(this);
        if (disposables2Destroy != null) {
            throw new IllegalStateException("onCreate called multiple times");
        }
        disposables2Destroy = new CompositeDisposable();
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        initView();
    }

    public abstract int getLayoutId();

    public abstract void initView();



    public boolean addRxDestroy(Disposable disposable) {
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "addUtilDestroy should be called between onCreate and onDestroy");
        }
        disposables2Destroy.add(disposable);
        return true;
    }

    public void onStart() {
        super.onStart();
        if (disposables2Stop != null) {
            throw new IllegalStateException("onStart called multiple times");
        }
        disposables2Stop = new CompositeDisposable();
    }

    @Override
    public  void onStop() {
        super.onStop();
        if (disposables2Stop == null) {
            throw new IllegalStateException("onStop called multiple times or onStart not called");
        }
        disposables2Stop.dispose();
        disposables2Stop = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) unbinder.unbind();
        if (disposables2Destroy == null) {
            throw new IllegalStateException(
                    "onDestroy called multiple times or onCreate not called");
        }
        disposables2Destroy.dispose();
        disposables2Destroy = null;
    }


    /**
     * 无参activity跳转
     */
    public void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * 带参执行activity之间跳转
     * 参数分别以逗号分隔
     * arg0,arg1,arg2
     */
    public void startActivity(Class<?> cls, Object... objects) {
        Intent intent = new Intent(this, cls);
        for (int i = 0; i < objects.length; i++) {
            intent.putExtra("data_" + i, JSON.toJSONString(objects[i]));
        }
        startActivity(intent);
    }

    /**
     * 获取对应上一个activity传来对象值
     *
     * @param cls 传入对应的类型，int、String
     */
    public Object[] getIntentObjects(Class<?>... cls) {
        Object[] objs = new Object[cls.length];
        Intent intent = getIntent();
        for (int i = 0; i < objs.length; i++) {
            objs[i] = JSON.parseObject(intent.getStringExtra("data_" + i), cls[i]);
        }
        return objs;
    }

    /**
     * 默认获取传入的第一个参数
     */
    public <T> T getIntentObject(Class<T> cls) {
        return getIntentObject(cls, 0);
    }

    /**
     * 获取对应上一个activity传来的值,分别出入下标0,1,2
     */
    public <T> T getIntentObject(Class<T> cls, int positon) {
        String st = getIntent().getStringExtra("data_" + positon);
        if (st == null) {
            return null;
        }
        return JSON.parseObject(st, cls);
    }

}
