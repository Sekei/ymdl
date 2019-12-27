package com.ymdl.retrofit;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by LENOVO on 2019/11/29.
 */

public class RetrofitSoapClient {
    //public ZYDApiService service;
    public SoapApiService service;

    private RetrofitSoapClient() {
        //okhttp log : 包含header、body数据
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                System.out.println("API数据：" + message);
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        //okhttp client
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(loggingInterceptor)
                .build();

        //retrofit client
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .addConverterFactory(ScalarsConverterFactory.create())      //添加 String类型[ Scalars (primitives, boxed, and String)] 转换器
                .addConverterFactory(SimpleXmlConverterFactory.create())    //添加 xml数据类型 bean-<xml
                .addConverterFactory(GsonConverterFactory.create())         //添加 json数据类型 bean->json
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("http://47.96.187.127:8080")        //test baseurl ；http://www.webxml.com.cn/WebServices/
                .build();
        service = retrofit.create(SoapApiService.class);
    }

    private static RetrofitSoapClient INSTANCE = null;

    //获取单例
    public static RetrofitSoapClient getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RetrofitSoapClient();
        }
        return INSTANCE;
    }

}
