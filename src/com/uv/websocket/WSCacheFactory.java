package com.uv.websocket;

import javax.websocket.Session;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * Created by uv2sun on 15/11/19.
 */
public class WSCacheFactory {

    /**
     * 初始化websocket发送缓存通道,并启动发送线程,持续从缓存提取数据发送
     *
     * @param session
     * @return
     */
    public static WSCache<String> initWebSocketCache(Session session) {
        WSCache<String> cache = newTransferQueue();
        new Thread(new WSSender(cache, session)).start();
        return cache;
    }


    /**
     * 获得websocket发送缓存队列
     *
     * @return
     */
    private static WSCache<String> newTransferQueue() {

        return new WSCache<String>() {
            private TransferQueue<String> queue = new LinkedTransferQueue<>();
            private boolean openFlag = true;

            @Override
            public String pop() throws InterruptedException {
                return queue.take();
            }

            @Override
            public int size() {
                return queue.size();
            }

            @Override
            public void push(String s) {
                queue.offer(s);
            }

            @Override
            public boolean isOpen() {
                return openFlag;
            }

            @Override
            public void close() {
                openFlag = false;
                queue.clear();
                queue = null;
            }
        };
    }

}
