package com.uv.websocket;

/**
 * Created by uv2sun on 15/11/22.
 */
public interface WSCache<E> {
    /**
     * 从队列头取出一个元素,如果没有元素可取,wait,直到有元素可取.
     *
     * @return
     */
    E pop() throws Exception;

    /**
     * @return 队列长度
     */
    int size();

    /**
     * 添加一个元素到队尾
     *
     * @param e
     */
    void push(E e) throws Exception;

    /**
     * 缓存是否打开可用
     *
     * @return
     */
    boolean isOpen();

    /**
     * 关闭缓存
     */
    void close();
}
