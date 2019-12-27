package com.ymdl.adapter;


import android.annotation.SuppressLint;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ymdl.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by LENOVO on 2019/12/9.
 */

public class YMDlAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public YMDlAdapter(@Nullable List<String> data) {
        super(R.layout.item_ymdl, data);
    }

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void convert(BaseViewHolder helper, String item) {
        //time
        String adapterData = item + ",";
        System.out.println("==============" + adapterData);
        List<String> data = Arrays.asList(adapterData.split(","));
        System.out.println("==============" + data.toString());
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = simpleDateFormat.parse(data.get(0));
            SimpleDateFormat format = new SimpleDateFormat("MM.dd");
            String time1 = format.format(date);
            SimpleDateFormat format1 = new SimpleDateFormat("HH:mm");
            String time2 = format1.format(date);
            helper.setText(R.id.time, String.format("%s\n%s", time1, time2));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        helper.setText(R.id.factory_name, String.format("工厂：%s", data.get(7)));
        helper.setText(R.id.equ_name, String.format("设备：%s\t\t编号：%s", data.get(2), data.get(1)));
        System.out.println("=========================" + data.size());
        if (data.size() > 8) {
            helper.setText(R.id.operator, String.format("操作员：%s", data.get(8)));
        }
    }
}
