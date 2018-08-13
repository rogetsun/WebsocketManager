package com.uv.websocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.websocket.Session;

/**
 * Created by uv2sun on 15/11/21.
 * websocket 处理器
 */
public class WSSender implements Runnable {

    private static final Log log = LogFactory.getLog(WSSender.class);

    private WSCache<String> wsCache;
    private Session session;

    public WSSender(WSCache<String> wsCache, Session session) {
        this.wsCache = wsCache;
        this.session = session;
    }

    @Override
    public void run() {
        while (session.isOpen()) {
            try {
//                session.getAsyncRemote().sendText(wsCache.pop());
                session.getBasicRemote().sendText(wsCache.pop());
            } catch (InterruptedException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wsCache.close();
    }

    public WSCache<String> getWsCache() {
        return wsCache;
    }

}
