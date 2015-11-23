package test.websocket;

import com.uv.websocket.WSServer;
import com.uv.websocket.annotation.ReceiveMsgType;
import com.uv.websocket.annotation.WSServerEndpoint;
import com.uv.websocket.message.MessageType;
import net.sf.json.JSONObject;

/**
 * Created by uv2sun on 15/11/22.
 */

@WSServerEndpoint("/ws/source4")
public class Source4 extends WSServer {

    @ReceiveMsgType(MessageType.REALTIME_SENSOR_DATA)
    public String realtimeSensorData(JSONObject jsonObject) {
        System.out.println("@source4:"+MessageType.REALTIME_SENSOR_DATA);
        System.out.println(jsonObject);
        return jsonObject.toString();
    }

    public Source4() {
        System.out.println("init websocket class:source4");
    }
}
