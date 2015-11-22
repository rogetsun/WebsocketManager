package com.uv.websocket;

import com.uv.event.EventHandler;
import com.uv.event.EventUtil;
import com.uv.event.impl.EventHandlerS;
import com.uv.websocket.annotation.ReceiveDataType;
import net.sf.json.JSONObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpSession;
import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by uv2sun on 15/11/22.
 */
public abstract class WSServer extends Endpoint {
    private static Log log = LogFactory.getLog(WSServer.class);
    private Map<String, EventHandler> eventHandlerMap = new HashMap<>();
    private HttpSession httpSession;

    /**
     * websocket关闭时调用
     */
    public void onDestroy() {
    }

    /**
     * websocket打开时调用
     */
    public void onInit() {
    }

    @Override
    public void onOpen(final Session session, EndpointConfig endpointConfig) {
        WSServer wsServer = this;
        this.httpSession = (HttpSession) endpointConfig.getUserProperties().get(HttpSession.class.getName());
        /**
         * 初始化websocket缓存
         */
        WSCache<String> cache = WSCacheFactory.initWebSocketCache(session);

        /**
         * 添加事件处理器,由ReceiveDataType指定事件名称,注解的方法处理事件
         */
        addRecevieDataTypeMethod2Event(wsServer, cache);

        this.onInit();
    }

    /**
     * 将ReceiveDataType注解的方法注册到事件
     * desc:
     * 如果用户实现的本类的子类中有ReceiveDataType注解的方法
     * 则实例化一个EventHandler,并在EventHandler的实现方法deal中调用此注解方法,
     * 然后将EventHandler实例EventUtil.on()到事件容器中,on的事件名称即为ReceiveDataType的value
     *
     * @param wsServer
     * @param cache
     */
    private void addRecevieDataTypeMethod2Event(final WSServer wsServer, final WSCache<String> cache) {
        /**
         * 获取当前websocket的Endpoint实现类中,用户继承本类定义的接受数据类型方法
         */
        for (final Method method : this.getClass().getDeclaredMethods()) {
            //方法是否有receiveDataType注解
            ReceiveDataType receiveDataType = method.getAnnotation(ReceiveDataType.class);

            if (receiveDataType != null) {//有receiveDataType注解

                final String dataType = receiveDataType.value();

                if (dataType != null && dataType.length() > 0) {//判断是否注解的value是合法存在的(填写了value，并且非"")
                    long id = (new Date().getTime() * 100) + ((int) (Math.random() * 100));
                    /**
                     * 增加ReceiveDataType.value()为名称的事件,事件deal里执行注解的方法
                     */
                    EventHandler eventHandler = new EventHandlerS(id) {
                        @Override
                        public void deal(String s, JSONObject jsonObject) {
                            try {
                                if (cache.isOpen()) {
                                    Object msg = method.invoke(wsServer, jsonObject);
                                    cache.push(msg.toString());
                                } else {
                                    getEventEmitter().remove(dataType, this);
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


    @Override
    public void onClose(Session session, CloseReason closeReason) {
        for (Map.Entry<String, EventHandler> ehEntry : eventHandlerMap.entrySet()) {
            EventUtil.remove(ehEntry.getKey(), ehEntry.getValue());
        }
        eventHandlerMap.clear();
        this.onDestroy();
    }

    public HttpSession getHttpSession() {
        return this.httpSession;
    }
}
