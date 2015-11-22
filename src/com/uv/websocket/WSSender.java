package com.uv.websocket;

import javax.websocket.Session;

/**
 * Created by uv2sun on 15/11/21.
 * websocket 处理器
 */
public class WSSender implements Runnable {
    private WSCache<String> wsCache;
    private Session session;

    @Override
    public void run() {
        while (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(wsCache.pop());
            } catch (InterruptedException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        wsCache.close();
    }

    public WSSender(WSCache<String> wsCache, Session session) {
        this.wsCache = wsCache;
        this.session = session;
    }

    public WSCache<String> getWsCache() {
        return wsCache;
    }

}
