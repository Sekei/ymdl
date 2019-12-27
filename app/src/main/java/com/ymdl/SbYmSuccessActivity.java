package com.ymdl;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.ymdl.base.BaseActivity;
import com.ymdl.bean.TransferBean;
import com.ymdl.utils.ToastUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder;

/**
 * 易码打印界面
 */
public class SbYmSuccessActivity extends BaseActivity {
    @BindView(R.id.right_icon)
    ImageView rightImage;
    @BindView(R.id.factory_name)
    TextView factoryName;
    @BindView(R.id.equ_name)
    TextView equName;
    @BindView(R.id.equ_no)
    TextView equNo;
    @BindView(R.id.equip_master_code)
    TextView equipMasterCode;
    @BindView(R.id.qr_code)
    ImageView qrCode;
    @BindView(R.id.bitmap)
    RelativeLayout bitmap;
    @BindView(R.id.top_title)
    TextView topTitle;
    @BindView(R.id.button_tips)
    TextView buttonTips;


    //传递数据接收
    private TransferBean transferBean;

    @Override
    public int getLayoutId() {
        return R.layout.activity_sbym_success;
    }

    @Override
    public void initView() {
        rightImage.setBackground(ContextCompat.getDrawable(this, R.mipmap.dyj));
        transferBean = getIntentObject(TransferBean.class);
        factoryName.setText(String.format("工厂名称：%s", transferBean.getFactoryName()));
        equName.setText(String.format("设备名称：%s", transferBean.getEquName()));
        equNo.setText(String.format("设备编号：%s", transferBean.getEquNo()));
        equipMasterCode.setText(String.format("易码：%s", transferBean.getEquipMasterCode()));
        //生成二维码
        qrCode.setImageBitmap(QRCodeEncoder.syncEncodeQRCode(transferBean.getEquipMasterCode(), 100));
        if ("0".equals(transferBean.getJumpType())) {
            //输入产生易码
        } else if ("1".equals(transferBean.getJumpType())) {
            //补签
            topTitle.setText("设备易码标签补制");
        } else if ("2".equals(transferBean.getJumpType())) {
            //查询
            topTitle.setText("查询到当前设备易码");
            buttonTips.setVisibility(View.VISIBLE);
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
                            .onDenied(data -> ToastUtils.showToast(SbYmSuccessActivity.this, "请开启位置权限,用于搜索蓝牙设备"))
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
                ToastUtils.showToast(SbYmSuccessActivity.this, "打印成功");
            }
            app.mJCAPI.endJob();
        } else {
            ToastUtils.showToast(SbYmSuccessActivity.this, "请先连接打印机");
        }
    }

}
