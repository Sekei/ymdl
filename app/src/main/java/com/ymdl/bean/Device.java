package com.ymdl.bean;/** * 作用- * * @author zhangbin * @date 2019-07-02 */public class Device {	private String mDeviceName;	private String mDeviceAddress;	private int mDeviceStatus;	public Device(String deviceName, String deviceAddress, int deviceStatus) {		mDeviceName = deviceName;		mDeviceAddress = deviceAddress;		mDeviceStatus = deviceStatus;	}	public String getDeviceName() {		return mDeviceName;	}	public void setDeviceName(String deviceName) {		mDeviceName = deviceName;	}	public String getDeviceAddress() {		return mDeviceAddress;	}	public void setDeviceAddress(String deviceAddress) {		mDeviceAddress = deviceAddress;	}	public int getDeviceStatus() {		return mDeviceStatus;	}	public void setDeviceConnectStatus(int deviceStatus) {		mDeviceStatus = deviceStatus;	}}