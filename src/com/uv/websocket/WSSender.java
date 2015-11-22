package com.uv.websocket;

import javax.websocket.Session;

/**
 * Created by uv2sun on 15/11/21.
 * websocket 处理器
 */
public class WSSender implements Runnable {
    private WSCache<String> cache;
    private Session session;

    @Override
    public void run() {
        while (session.isOpen()) {
            try {
                session.getBasicRemote().sendText(cache.pop());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("WSSender.closed");
        cache.close();
    }

    public WSSender(WSCache<String> cache, Session session) {
        this.cache = cache;
        this.session = session;
    }
}
