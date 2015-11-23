package test.websocket;

import com.uv.timer.EventTriggerTask;
import com.uv.timer.TimerUtil;
import com.uv.websocket.WSServer;
import com.uv.websocket.annotation.ReceiveMsgType;
import com.uv.websocket.annotation.WSServerEndpoint;
import com.uv.websocket.message.MessageType;
import net.sf.json.JSONObject;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by uv2sun on 15/11/22.
 */
@WSServerEndpoint("/ws/source5")
public class Source5 extends WSServer {

    private ScheduledFuture scheduledFuture;

    @ReceiveMsgType(MessageType.MESSAGE)
    public JSONObject randomAge(JSONObject data) {
        System.out.println("httpSession:" + getHttpSession());
        if (data != null) {
            return data;
        } else
            return JSONObject.fromObject("{id:123321, name:'litx222', age:" + (int) (Math.random() * 1000) + "}");
    }

    @Override
    public void onInit() {
        ScheduledFuture future = TimerUtil.interval(new EventTriggerTask(MessageType.MESSAGE), 1000, 1000);
        this.scheduledFuture = future;
    }

    @Override
    public void onDestroy() {
        System.out.println("onDestory cancel scheduledFuture");
        this.scheduledFuture.cancel(true);
    }
}
