package com.ymdl.utils;

import java.util.Map;

/**
 * Created by LENOVO on 2019/11/29.
 */

public class ApiNode {
    // 正常字符-> <xxx>
    public static String toStart(String name) {
        return "<" + name + ">";
    }
    // 正常字符-> </xxx>
    public static String toEnd(String name) {
        return "</" + name + ">";
    }

    public static String getRequestBody(String method, Map<String, String> map) {
        StringBuffer sbf = new StringBuffer();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            sbf.append(ApiNode.toStart(entry.getKey()));
            sbf.append(entry.getValue());
            sbf.append(ApiNode.toEnd(entry.getKey()));
        }
        String str = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">" +
                "  <soap:Body>" +
                "    <" + method + " xmlns=\"http://47.96.187.127:8080/YM_WebService\">" + sbf.toString() +
                "    </" + method + ">" +
                "  </soap:Body>" +
                "</soap:Envelope>";
        System.out.println("Soap1.1 请求入参:" + str);
        return str;
    }
}
