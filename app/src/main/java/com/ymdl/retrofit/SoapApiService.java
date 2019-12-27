package com.ymdl.retrofit;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 * Created by LENOVO on 2019/11/29.
 */

public interface SoapApiService {
    /**
     * 登录
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/logIn"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getlogIn(@Body String body);

    /**
     * 修改密码
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/changePswd"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getChangePswd(@Body String body);


    /**
     * 账号注销
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/userIDelete"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getUserIDelete(@Body String body);


    /**
     * 账号注册
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/newUserSignIn"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getNewUserSignIn(@Body String body);


    /**
     * 设备易码生成
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/newEquipSignIn_MC"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getNewEquipSignInMC(@Body String body);


    /**
     * 产品易码生成
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/newProSignIn_MC"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getNewProSignInMC(@Body String body);


    /**
     * 设备易码查询
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/getEquipInfo"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getEquipInfo(@Body String body);


    /**
     * 产品易码查询
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/getProInfo"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getProInfo(@Body String body);


    /**
     * 标签绑定
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/inputBindInfo"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getInputBindInfo(@Body String body);

    /**
     * 产品历史数据查询
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/getProHistoryInfo"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getProHistoryInfo(@Body String body);

    /**
     * 产品注册绑定易码
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/newProSignIn_MCBind"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getNewProSignIn_MCBind(@Body String body);


    /**
     * 产品历史数据查询（降序）
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/getProHistoryInfo_D"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getProHistoryInfoD(@Body String body);


    /**
     * 产品易码查询是否已使用
     *
     * @param body
     * @return
     */
    @Headers({"Content-Type:text/xml; charset=utf-8",
            "SOAPAction:http://47.96.187.127:8080/YM_WebService/checkProMCIsUsed"})
    @POST("/ym_webservice.asmx")
    Observable<ResponseBody> getCheckProMasterCode(@Body String body);

}
