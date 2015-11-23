package test.websocket;

import com.uv.websocket.WSServer;
import com.uv.websocket.annotation.ReceiveMsgType;
import com.uv.websocket.annotation.WSServerPoint;
import net.sf.json.JSONObject;

/**
 * Created by uv2sun on 15/11/22.
 */

@WSServerPoint("/ws/source4")
public class Source4 extends WSServer {

    @ReceiveMsgType("realtime.sensor")
    public String realtimeSensorData(JSONObject jsonObject) {
        System.out.println("@source4:realtimeSensorData");
        System.out.println(jsonObject);
        return jsonObject.toString();
    }

    public Source4() {
        System.out.println("init websocket class:source4");
    }
}
