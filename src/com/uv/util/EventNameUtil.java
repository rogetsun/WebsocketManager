package com.uv.util;

import com.uv.event.EventUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EventNameUtil {
    
    //匹配{key}的正则表达式
    public static final Pattern EVENT_NAME_PARAM_KEY_PATTERN = Pattern.compile("(?<=\\{)([^\\}]+)(?=\\})");
    
    /**
     * 将EventName中的{key}替换为request.getParam(key)
     * 当前使用用来将ReceiveMsgType中给定的事件名称中，如果有要替换的内容，则从当前webSocket被请求的请求中寻找参数key，并替换
     * 如果请求参数中没有key，则替换为空字符串
     *
     * @param eventName 带占位符 {key} 的时间名称
     * @param paramsMap 参数map，每个key可以有多个值
     * @return 不带占位符的时间名称
     */
    public static List<String> eventNameParamReplace(String eventName, Map<String, List<String>> paramsMap) {
        List<String> l = new ArrayList<>();
        
        //参数Map
        Map<String, List<String>> paramMap = new HashMap<>();
        
        for (String paramKey : getPlaceholderParamKey(eventName)) {
            List<String> paramValues = paramsMap.get(paramKey);
            if (paramValues != null && paramValues.size() > 0) {
                paramMap.put(paramKey, paramValues);
//                url = url.replaceAll("\\{" + paramKey + "\\}", paramValues.get(0));
            } else {
                //对于参数值未给的情况，直接去掉{key}
                eventName = eventName.replaceAll("\\{" + paramKey + "\\}", "");
            }
        }
        
        List<String> tmpList = new ArrayList<>();//带替换{key}的url集合
        tmpList.add(eventName);//初始就一个url
        
        for (Map.Entry<String, List<String>> entry : paramMap.entrySet()) {
            List<String> stringList = new ArrayList<>();
            for (String param : entry.getValue()) {
                tmpList.forEach(s -> stringList.add(s.replaceAll("\\{" + entry.getKey() + "\\}", param)));
            }
            tmpList = stringList;
        }
        
        tmpList.forEach(en -> {
            if (en.endsWith(EventUtil.EVENT_PROPAGATION_SEQ) || en.endsWith(EventUtil.EVENT_NO_PROPAGATION_SEQ)) {
                en = en.substring(0, en.length() - 1);
            }
            l.add(en);
        });
        
        return l;
    }
    
    /**
     * 获取时间名称中的占位参数key
     *
     * @param eventName
     * @return
     */
    public static List<String> getPlaceholderParamKey(String eventName) {
        List<String> ph = new ArrayList<>();
        
        Matcher m = EVENT_NAME_PARAM_KEY_PATTERN.matcher(eventName);
        while (m.find()) {
            ph.add(m.group());
        }
        
        return ph;
    }
    
}
