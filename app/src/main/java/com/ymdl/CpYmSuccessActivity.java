package com.ymdl;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.ymdl.adapter.YMDlAdapter;
import com.ymdl.base.BaseActivity;
import com.ymdl.bean.TransferBean;
import com.ymdl.retrofit.RetrofitSoapClient;
import com.ymdl.retrofit.RxSchedulers;
import com.ymdl.utils.ApiNode;
import com.ymdl.utils.ToastUtils;
import com.ymdl.utils.XMLUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

public class CpYmSuccessActivity extends BaseActivity {
    @BindView(R.id.right_icon)
    ImageView rightImage;
    @BindView(R.id.bitmap)
    RelativeLayout bitmap;
    @BindView(R.id.factory_name)
    TextView factoryName;
    @BindView(R.id.pro_name)
    TextView proName;
    @BindView(R.id.pro_no)
    TextView proNo;
    @BindView(R.id.pro_num)
    TextView proNum;
    @BindView(R.id.pro_unit)
    TextView proUnit;
    @BindView(R.id.pro_master_code)
    TextView proMasterCode;
    @BindView(R.id.qr_code)
    ImageView qrCode;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.history_recycler)
    RecyclerView historyRecycler;
    @BindView(R.id.printing_layout)
    LinearLayout printingLayout;



    private TransferBean transferBean;
    //适配器
    private YMDlAdapter adapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_cpym_success;
    }

    @Override
    public void initView() {
        rightImage.setBackground(ContextCompat.getDrawable(this, R.mipmap.dyj));
        transferBean = getIntentObject(TransferBean.class);
        factoryName.setText(String.format("工厂名称：%s", transferBean.getFactoryName()));
        proName.setText(String.format("产品名称：%s", transferBean.getProName()));
        proNo.setText(String.format("产品编号：%s", transferBean.getProNo()));
        proNum.setText(String.format("产品数量：%s", transferBean.getProNum()));
        proUnit.setText(String.format("产品单位：%s", transferBean.getProUnit()));
        proMasterCode.setText(String.format("易码：%s", transferBean.getProMasterCode()));
        //生成二维码
        qrCode.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(transferBean.getProMasterCode(), 100));
        if ("0".equals(transferBean.getJumpType())) {
            //产品输入产生易码

        } else if ("1".equals(transferBean.getJumpType())) {
            //补签
            topTitle.setText("产品易码标签补制");
        } else if ("2".equals(transferBean.getJumpType())) {
            //查询
            topTitle.setText("查询到当前产品易码");
            rightImage.setVisibility(View.GONE);
            printingLayout.setVisibility(View.GONE);
            historyRecycler.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            historyRecycler.setLayoutManager(linearLayoutManager);
            historyRecycler.setAdapter(adapter = new YMDlAdapter(null));
            Map<String, String> map = new HashMap<>();
            map.put("proMasterCode", transferBean.getProMasterCode());
            String body = ApiNode.getRequestBody("getProHistoryInfo", map);
            addRxDestroy(RetrofitSoapClient.getInstance().service
                    .getProHistoryInfo(body)
                    .compose(RxSchedulers.io_main())
                    .subscribeWith(new DisposableObserver<ResponseBody>() {
                        @RequiresApi(api = Build.VERSION_CODES.O)
                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                List<String> newEquipSignInMC = XMLUtil.XmlToData(responseBody.string(), "getProHistoryInfoResult");
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
    }

    @OnClick({R.id.back_btn, R.id.right_layout, R.id.commit_btn})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back_btn:
                finish();
                break;
            case R.id.right_layout:
                //判断蓝牙是否开启
                App app = App.getInstance();
                if (app.mBluetoothAdapter.isEnabled()) {
                    //判断是否连接到打印机
//                    if (app.mConnectPrinterType == app.DISCONNECTED_TYPE) {
//                        ToastUtils.showToast(this, "请先用蓝牙匹配打印机~");
//                        return;
//                    }
                    ToastUtils.showToast(this, "打印机连接中...");
                    //判断搜索权限是否开启
                    AndPermission.with(this)
                            .runtime()
                            .permission(Permission.Group.LOCATION)
                            .onGranted(data -> App.getInstance().mBluetoothAdapter.startDiscovery())
                            .onDenied(data -> ToastUtils.showToast(CpYmSuccessActivity.this, "请开启位置权限,用于搜索蓝牙设备"))
                            .start();
                } else {
                    ToastUtils.showToast(this, "请在设置开启蓝牙");
                }
                break;
            case R.id.commit_btn:
                //立即打印
                getPrintLabel();
                break;
        }
    }

    /**
     * 打印标签
     */
    private void getPrintLabel() {
        App app = App.getInstance();
        if (app.mConnectPrinterType == app.B3S_CONNECTED_TYPE) {
            app.mJCAPI.startJob(80, 60, 90, 1);
            app.mJCAPI.startPage();
            app.mJCAPI.drawBitmap(app.loadBitmapFromView(bitmap), 0, 0, 80, 60, 0);
            app.mJCAPI.endPage();
            boolean isPrintSuccess = App.getInstance().mJCAPI.commitJob(1);
            if (isPrintSuccess) {
                ToastUtils.showToast(CpYmSuccessActivity.this, "打印成功");
            }
            app.mJCAPI.endJob();
        } else {
            ToastUtils.showToast(CpYmSuccessActivity.this, "请先连接打印机");
        }
    }
}