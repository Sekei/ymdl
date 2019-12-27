package com.ymdl.utils;


import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by LENOVO on 2019/11/29.
 * 解析返回soap报文，获取目标节点值
 */
public class XMLUtil {
    /**
     * xml转String
     *
     * @param parseStrXml
     * @param finalNodeName
     * @return
     */
    public static List<String> strXmlToData(String parseStrXml, String finalNodeName) {
        String dataStr = null;
        Matcher matcher = Pattern.compile("<" + finalNodeName + ">(.*)</" + finalNodeName + ">").matcher(parseStrXml);
        while (matcher.find()) {
            dataStr = (((matcher.group(1)).replace("<string>", "")).replace("</string>", ",")).replace("<string />", "");
        }
        dataStr = dataStr.substring(0, dataStr.length() - 1);
        if ("O".equals(dataStr)) {
            dataStr = "OK";
        }
        return Arrays.asList(dataStr.split(","));
    }

    /**
     * list截取
     *
     * @param parseStrXml
     * @param finalNodeName
     * @return
     */
    public static List<String> XmlToData(String parseStrXml, String finalNodeName) {
        String dataStr = null;
        Matcher matcher = Pattern.compile("<" + finalNodeName + ">(.*)</" + finalNodeName + ">").matcher(parseStrXml);
        while (matcher.find()) {
            dataStr = (((matcher.group(1)).replace("<string>", "")).replace("</string>", ",")).replace("<string />", "");
        }
        if ("O".equals(dataStr)) {
            dataStr = "OK";
        }
        return Arrays.asList(dataStr.split(","));
    }
}
