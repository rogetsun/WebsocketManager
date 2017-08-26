package com.uv.websocket;

import com.uv.event.EventHandler;
import com.uv.event.EventUtil;
import com.uv.event.impl.EventHandlerS;
import com.uv.websocket.annotation.ReceiveMsgType;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by uv2sun on 15/11/22.
 */
public abstract class WSServer extends Endpoint {
    private static final Log log = LogFactory.getLog(WSServer.class);
    
    private static final Pattern URL_PARAM_KEY_PATTERN = Pattern.compile("(?<=\\{)([^\\}]+)(?=\\})");
    
    
    /**
     * 从当前类的基层类启动的事件处理器
     * key:事件名称,value：EventHandler
     */
    private Map<String, EventHandler> eventHandlerMap = new HashMap<>();
    
    /**
     * 当前websocket被请求时，给的url参数
     */
    private Map<String, List<String>> requestParam;
    
    /**
     * 当前web容器的session
     */
    private HttpSession httpSession;
    /**
     * websocket数据发送器启动后的线程
     */
    private Thread wsSenderThread;
    
    /**
     * 自定义websocket关闭时调用
     */
    public void onDestroy() {
    }
    
    /**
     * 自定义websocket打开时调用
     */
    public void onInit() {
    }
    
    @Override
    public void onOpen(final Session session, EndpointConfig endpointConfig) {
        this.setRequestParam(session.getRequestParameterMap());
        WSServer wsServer = this;
        this.httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
        /**
         * 初始化websocket发送器
         */
        WSSender wsSender = WSSenderFactory.initWebSocketSender(session);
        /**
         * 取得发送器的发送缓存
         */
        WSCache<String> cache = wsSender.getWsCache();
        /**
         * 启动发送器
         */
        this.wsSenderThread = new Thread(wsSender);
        this.wsSenderThread.setDaemon(true);
        this.wsSenderThread.start();
        
        /**
         * 启动事件监听处理器,处理结果发送到当前websocket数据发送器的缓存
         *
         * 由ReceiveDataType注解的value()指定事件名称,注解的方法处理事件
         */
        this.addRecevieDataTypeMethod2Event(wsServer, cache);
        
        /**
         * 如果当前web应用环境有spring
         * 检查当前类属性有Resource或Autowired注解的，从spring上下文注入管理的bean
         */
        this.injectPropertyBySpring();
        /**
         * 自定义websocket启动时操作
         */
        this.onInit();
    }
    
    
    @Override
    public void onClose(Session session, CloseReason closeReason) {
        for (Map.Entry<String, EventHandler> ehEntry : eventHandlerMap.entrySet()) {
            EventUtil.remove(ehEntry.getKey(), ehEntry.getValue());
        }
        eventHandlerMap.clear();
        wsSenderThread.interrupt();
        this.onDestroy();
    }
    
    
    /**
     * 将ReceiveMsgType注解的方法注册到事件
     * desc:
     * 如果用户实现的本类的子类中有ReceiveMsgType注解的方法
     * 则实例化一个EventHandler,并在EventHandler的实现方法deal中调用此注解方法,
     * 然后将EventHandler实例EventUtil.on()到事件容器中,on的事件名称即为ReceiveDataType的value
     * <p>
     * 20170818增加ReceiveMsgType的值可以包含{key}。key即为请求中的参数的key。并且会替换{key}
     *
     * @param wsServer
     * @param cache
     */
    private void addRecevieDataTypeMethod2Event(final WSServer wsServer, final WSCache<String> cache) {
        /**
         * 获取当前websocket的Endpoint实现类中,用户继承本类定义的接受数据类型方法
         */
        for (final Method method : this.getClass().getMethods()) {
            //方法是否有receiveDataType注解
            ReceiveMsgType receiveMsgType = method.getAnnotation(ReceiveMsgType.class);
            
            if (receiveMsgType != null) {//有receiveDataType注解
                
                String dataType = receiveMsgType.value();
                
                if (dataType.length() > 0) {//判断是否注解的value是合法存在的(填写了value，并且非"")
                    
                    //替换dataType中的{key}为请求参数中的参数
                    dataType = this.urlParamReplace(dataType);
                    
                    long id = (new Date().getTime() * 100) + ((int) (Math.random() * 100));
                    /**
                     * 增加ReceiveMsgType.value()为名称的事件,事件deal里执行注解的方法
                     */
                    String finalDataType = dataType;
                    EventHandler eventHandler = new EventHandlerS(id) {
                        @Override
                        public void deal(String s, JSONObject jsonObject) {
                            try {
                                if (cache.isOpen()) {
                                    /**
                                     * 执行用户定义方法,并获得返回值msg
                                     */
                                    Object msg = method.invoke(wsServer, jsonObject);
                                    /**
                                     * 将msg.toString()放进websocket的发送缓存
                                     */
                                    if (null != msg)
                                        cache.push(msg.toString());
                                } else {
                                    getEventEmitter().remove(finalDataType, this);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    this.eventHandlerMap.put(dataType, eventHandler);
                    EventUtil.on(dataType, eventHandler);
                }
            }
        }
    }
    
    /**
     * 将url中的{key}替换为request.getParam(key)
     * 当前使用用来将ReceiveMsgType中给定的事件名称中，如果有要替换的内容，则从当前websocket被请求的请求中寻找参数key，并替换
     * 如果请求参数中没有key，则替换为空字符串
     *
     * @param url
     * @return
     */
    private String urlParamReplace(String url) {
        Matcher matcher = URL_PARAM_KEY_PATTERN.matcher(url);
        while (matcher.find()) {
            String paramKey = matcher.group();
            List<String> paramValues = this.getRequestParam().get(paramKey);
            if (paramValues != null && paramValues.size() > 0) {
                url = url.replaceAll("\\{" + paramKey + "\\}", paramValues.get(0));
            } else {
                url = url.replaceAll("\\{" + paramKey + "\\}", "");
            }
        }
        if (url.endsWith(EventUtil.EVENT_PROPAGATION_SEQ) || url.endsWith(EventUtil.EVENT_NO_PROPAGATION_SEQ)) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
    
    /**
     * 注入spring容器管理的bean
     * 遍历所有继承当前类的子类的属性，如果有Resource或Autowired的注解，将从spring上下文中获取并注入
     */
    private void injectPropertyBySpring() {
        try {
//            Class.forName("org.springframework.web.context.support.WebApplicationContextUtils");
            WebApplicationContext ac = WebApplicationContextUtils.getRequiredWebApplicationContext(
                    this.getHttpSession().getServletContext());
            
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getAnnotationsByType(Resource.class).length > 0 || field.getAnnotationsByType(Autowired.class).length > 0) {
                    field.setAccessible(true);
                    field.set(this, ac.getBean(field.getType()));
                }
            }
        } catch (Exception e) {
        }
    }
    
    public HttpSession getHttpSession() {
        return this.httpSession;
    }
    
    public Map<String, List<String>> getRequestParam() {
        return requestParam;
    }
    
    public void setRequestParam(Map<String, List<String>> requestParam) {
        this.requestParam = requestParam;
    }
}
