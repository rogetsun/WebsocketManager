package test.websocket;

import net.sf.json.JSONObject;

import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * Created by uv2sun on 15/11/21.
 */
@ServerEndpoint("/ws/source3")
public class Source3 extends Endpoint {

    public Source3() {
        System.out.println("init websocket class:source3");
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        System.out.println("@override onOpen@Source3");
        System.out.println(endpointConfig.getUserProperties().keySet());
        session.getAsyncRemote().sendText(JSONObject.fromObject("{id:9, name:'litx3', age:999}").toString());
    }
}
