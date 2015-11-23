package com.uv.websocket.message;

/**
 * Created by uv2sun on 15/11/23.
 */
public abstract class MessageType {
    /**
     * {sensor_id:{type:long, require:1}, monitor_value:{type:String, require:1}, monitor_time:{type:long, require:1}}
     */
    public final static String REALTIME_SENSOR_DATA = "realtime_sensor_data";


    /**
     * {type:{type:String, require:0}, login_no:{type:String, require:0}, context:{type:String, require:1}}
     */
    public final static String MESSAGE = "message";

}

