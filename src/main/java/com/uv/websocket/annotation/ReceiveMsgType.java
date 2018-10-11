package com.uv.websocket.annotation;

import java.lang.annotation.*;

/**
 * Created by uv2sun on 15/11/21.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ReceiveMsgType {
    String value();
}
