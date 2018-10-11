package com.uv.websocket;

import javax.websocket.Session;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TransferQueue;

/**
 * Created by uv2sun on 15/11/19.
 */
public class WSSenderFactory {

    /**
     * 初始化websocket发送缓存通道,并启动发送线程,持续从缓存提取数据发送
     *
     * @param session
     * @return
     */
    public static WSSender initWebSocketSender(Session session) {
        return new WSSender(newWSCache(), session);
    }


    /**
     * 获得websocket发送缓存队列
     *
     * @return
     */
    private static WSCache<String> newWSCache() {

        return new WSCache<String>() {
            /**
             * WSCache缓存实现队列
             */
            private TransferQueue<String> queue = new LinkedTransferQueue<>();
            /**
             * 缓存是否打开可用状态标志位
             */
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
