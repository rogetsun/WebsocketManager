package com.uv.websocket;

import com.uv.event.EventHandler;
import com.uv.event.EventUtil;
import com.uv.event.impl.EventHandlerS;
import com.uv.websocket.annotation.ReceiveMsgType;
import net.sf.json.JSONObject;

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
    /**
     * 从当前类的基层类启动的事件处理器
     * key:事件名称,value：EventHandler
     */
    private Map<String, EventHandler> eventHandlerMap = new HashMap<>();
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
        this.wsSenderThread.start();

        /**
         * 启动事件监听处理器,处理结果发送到当前websocket数据发送器的缓存
         *
         * 由ReceiveDataType注解的value()指定事件名称,注解的方法处理事件
         */
        this.addRecevieDataTypeMethod2Event(wsServer, cache);
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
            ReceiveMsgType receiveMsgType = method.getAnnotation(ReceiveMsgType.class);

            if (receiveMsgType != null) {//有receiveDataType注解

                final String dataType = receiveMsgType.value();

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
                                    /**
                                     * 执行用户定义方法,并获得返回值msg
                                     */
                                    Object msg = method.invoke(wsServer, jsonObject);
                                    /**
                                     * 将msg.toString()放进websocket的发送缓存
                                     */
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


    public HttpSession getHttpSession() {
        return this.httpSession;
    }
}
