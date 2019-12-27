package com.ymdl;

import android.annotation.SuppressLint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ymdl.adapter.YMDlAdapter;
import com.ymdl.base.BaseActivity;
import com.ymdl.bean.TransferBean;
import com.ymdl.retrofit.RetrofitSoapClient;
import com.ymdl.retrofit.RxSchedulers;
import com.ymdl.utils.ApiNode;
import com.ymdl.utils.SpUtil;
import com.ymdl.utils.XMLUtil;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * 易码当联成功
 */
public class YmdlOkActivity extends BaseActivity {
    @BindView(R.id.factory_name)
    TextView factoryName;
    @BindView(R.id.pro_no)
    TextView proNo;
    @BindView(R.id.equ_no)
    TextView equNo;
    @BindView(R.id.user_name)
    TextView userName;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.pro_num)
    TextView proNum;
    @BindView(R.id.history_recycler)
    RecyclerView historyRecycler;

    private TransferBean transferBean;
    //适配器
    private YMDlAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_ymdl_ok;
    }

    @Override
    public void initView() {
        //初始化
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        historyRecycler.setLayoutManager(linearLayoutManager);
        historyRecycler.setAdapter(adapter = new YMDlAdapter(null));
        //赋值
        transferBean = getIntentObject(TransferBean.class);
        factoryName.setText(String.format("工厂：%s", transferBean.getFactoryName()));
        proNo.setText(String.format("产品编号：%s", transferBean.getProNo()));
        equNo.setText(String.format("设备编号：%s", transferBean.getEquNo()));
        userName.setText(String.format("操作员：%s", SpUtil.getString(this, "userName")));
        Date currentTime = new Date();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        String dateString = formatter.format(currentTime);
        time.setText(String.format("（%s）", dateString));
        proNum.setText(String.format("%s%s", transferBean.getProNum(), transferBean.getProUnit()));


        Map<String, String> map = new HashMap<>();
        map.put("proMasterCode", transferBean.getProMasterCode());
        String body = ApiNode.getRequestBody("getProHistoryInfo_D", map);
        addRxDestroy(RetrofitSoapClient.getInstance().service
                .getProHistoryInfoD(body)
                .compose(RxSchedulers.io_main())
                .subscribeWith(new DisposableObserver<ResponseBody>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            List<String> newEquipSignInMC = XMLUtil.XmlToData(responseBody.string(), "getProHistoryInfo_DResult");
                            System.out.println("API数据：=====>>>" + newEquipSignInMC.toString());
                            if (newEquipSignInMC.contains("OK")) {
                                //循环截取
                                String stringData = String.join(",", newEquipSignInMC) + ",";
                                System.out.println("API数据：拼接=====>>>" + stringData);
                                adapter.setNewData(Arrays.asList(stringData.split(",OK,")));
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
