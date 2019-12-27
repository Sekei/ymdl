package com.ymdl;


import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.view.View;

import com.dothantech.lpapi.LPAPI;
import com.dothantech.printer.IDzPrinter;
import com.gengcon.www.jcapi.api.JCAPI;
import com.ymdl.bean.Device;
import com.ymdl.utils.ClsUtils;
import com.ymdl.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 易码当联
 *
 * @author Created by stone
 * @since 2019/8/12
 */
public class App extends MultiDexApplication implements LPAPI.Callback, JCAPI.CallBack {
    private static App instance;

    public static App getInstance() {
        return instance;
    }

    //B11系列打印机打印接口
    public LPAPI mLPAPI = null;
    //B3S系列打印机打印接口
    public JCAPI mJCAPI = null;
    // 蓝牙适配器
    public BluetoothAdapter mBluetoothAdapter;
    //广播过滤
    public IntentFilter mFilter;
    // 设备mac地址
    public List<String> mDeviceAddressList = new ArrayList<>();
    //设备列表数据
    public List<Device> mDeviceList = new ArrayList<>();
    //连接设备类型
    public final int DISCONNECTED_TYPE = -1;
    public final int B11_CONNECTED_TYPE = 1;
    public final int B3S_CONNECTED_TYPE = 2;
    //-1 未连接打印机
    public int mConnectPrinterType = DISCONNECTED_TYPE;
    //B3S系列
    private final String B3S = "B3S";
    // B11系列
    private final String B11 = "B11";

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        MultiDex.install(this);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //打印相关准备
        mLPAPI = LPAPI.Factory.createInstance(this);
        mJCAPI = JCAPI.getInstance(this, this);
        //蓝牙广播意图过滤
        mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        mFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        //注册广播
        registerReceiver(mReceiver, mFilter);
    }

    /**
     * View转bitmap
     *
     * @param v
     * @return
     */
    public Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        c.drawColor(Color.WHITE);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    /**
     * 广播
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //获取意图
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_FOUND)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceAddress = bluetoothDevice.getAddress();
                String name = bluetoothDevice.getName();
                boolean isPrinterType = bluetoothDevice.getBluetoothClass().getDeviceClass() == 1664;
                if (!mDeviceAddressList.contains(deviceAddress) && name != null && isPrinterType) {
                    mDeviceAddressList.add(deviceAddress);
                    Device device = null;
                    //显示已配对设备
                    if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                        device = new Device(bluetoothDevice.getName(), deviceAddress, 12);
                    } else if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                        device = new Device(bluetoothDevice.getName(), deviceAddress, 10);
                    }
                    mDeviceList.add(device);
                    //自动连接默认第一个配对的蓝牙设备
                    getConnectingDevice();
                }
            } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceAddress = bluetoothDevice.getAddress();
                if (mDeviceAddressList.contains(deviceAddress)) {
                    for (Device device : mDeviceList) {
                        if (device.getDeviceAddress().equals(deviceAddress)) {
                            if (device.getDeviceStatus() == 14) {
                                return;
                            } else {
                                device.setDeviceConnectStatus(14);
                                return;

                            }
                        }
                    }
                }
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceAddress = bluetoothDevice.getAddress();
                if (mDeviceAddressList.contains(deviceAddress)) {
                    for (Device device : mDeviceList) {
                        if (device.getDeviceAddress().equals(deviceAddress)) {
                            if (device.getDeviceStatus() == 12 || device.getDeviceStatus() == 10) {
                                return;
                            } else {
                                if (bluetoothDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                                    device.setDeviceConnectStatus(12);
                                } else if (bluetoothDevice.getBondState() != BluetoothDevice.BOND_BONDED) {
                                    device.setDeviceConnectStatus(10);
                                }
                                return;
                            }
                        }
                    }
                }
            }
        }
    };

    /**
     * 连接设备
     */
    private void getConnectingDevice() {
        System.out.println("=============>>开始扫描设备");
        //自动连接默认第一个配对的蓝牙设备
        Device device = mDeviceList.get(0);
        String deviceName = device.getDeviceName();
        System.out.println("=============>>开始扫描设备" + deviceName);
        String deviceAddress = device.getDeviceAddress();
        BluetoothDevice bluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
        int deviceStatus = device.getDeviceStatus();
        //未配对 进行配对
        if (deviceStatus == BluetoothDevice.BOND_NONE) {
            try {
                // 与设备配对
                if (ClsUtils.createBond(bluetoothDevice.getClass(), bluetoothDevice)) {
                    System.out.println("=================>配对成功");
                    //ToastUtils.showToast(getApplicationContext(), "配对成功");
                    boolean isOpenSuccess = mJCAPI.openPrinterByAddress(deviceAddress);
                    if (isOpenSuccess) {
                        device.setDeviceConnectStatus(14);
                        mConnectPrinterType = B3S_CONNECTED_TYPE;
                        ToastUtils.showToast(getApplicationContext(), "连接成功~");
                    }
                } else {
                    ToastUtils.showToast(getApplicationContext(), "配对失败,请重新连接蓝牙");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //已配对进行连接
        if (deviceStatus == BluetoothDevice.BOND_BONDED) {
            if (deviceName.startsWith(B3S)) {
                boolean isOpenSuccess = mJCAPI.openPrinterByAddress(deviceAddress);
                if (isOpenSuccess) {
                    System.out.println("=================>连接成功");
                    device.setDeviceConnectStatus(14);
                    mConnectPrinterType = B3S_CONNECTED_TYPE;
                    ToastUtils.showToast(getApplicationContext(), "连接成功~");
                }
            }
            if (deviceName.startsWith(B11)) {
                boolean isOpenSuccess = mLPAPI.openPrinterSync(deviceName);
                if (isOpenSuccess) {
                    device.setDeviceConnectStatus(14);
                    mConnectPrinterType = B11_CONNECTED_TYPE;
                    ToastUtils.showToast(getApplicationContext(), "连接成功~");
                }
            }
        }
    }


    /**
     * B11系列打印机回调接口
     *
     * @param progressInfo
     * @param o
     */
    @Override
    public void onProgressInfo(IDzPrinter.ProgressInfo progressInfo, Object o) {

    }

    @Override
    public void onStateChange(IDzPrinter.PrinterAddress printerAddress, IDzPrinter.PrinterState printerState) {

    }

    @Override
    public void onPrintProgress(IDzPrinter.PrinterAddress printerAddress, Object o, IDzPrinter.PrintProgress printProgress, Object o1) {

    }

    @Override
    public void onPrinterDiscovery(IDzPrinter.PrinterAddress printerAddress, IDzPrinter.PrinterInfo printerInfo) {

    }

    /**
     * B3S系列打印机回调接口
     */
    @Override
    public void onConnectSuccess() {
        System.out.println("=================>打印机连接成功");
        ToastUtils.showToast(getApplicationContext(), "打印机连接成功~");
    }

    @Override
    public void onConnectFail() {
        System.out.println("=================>打印机连接失败");
        ToastUtils.showToast(getApplicationContext(), "打印机连接失败~");
    }

    @Override
    public void disConnect() {
        System.out.println("=================>打印机连接已断开");
        ToastUtils.showToast(getApplicationContext(), "打印机连接已断开~");
    }

    @Override
    public void onAbnormalResponse(int i) {
        System.out.println("=================>打印机异常");
        ToastUtils.showToast(getApplicationContext(), "打印机异常，请查看~");
    }

    @Override
    public void electricityChange(int i) {
        System.out.println("=================>打印机异常");
    }
}