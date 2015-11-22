package test.websocket;

import net.sf.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by uv2sun on 15/11/19.
 */
@ServerEndpoint("/ws/source2")
public class Source2 {
    private static Set<Session> wsSet = Collections.synchronizedSet(new HashSet<Session>());

    @OnMessage
    public void msg(Session s, String msg) throws IOException {
        System.out.println("@OnMessage:Session=" + s + ",Msg=" + msg);
        if ("getData".equals(msg)) {
        } else if ("close".equals(msg)) {
            s.close();
        }
    }

    @OnOpen
    public void open(final Session s, EndpointConfig e) {
        System.out.println("@OnOpen:Session=" + s + ",EndpointConfig=" + e);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    JSONObject jo = JSONObject.fromObject("{'id':2, 'name':'litx', 'age':" + ((int) (Math.random() * 1000)) + "}");
                    s.getAsyncRemote().sendText(jo.toString());
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();
        wsSet.add(s);
    }

    @OnClose
    public void close(Session s, CloseReason r) {
        System.out.println("@OnClose:Session=" + s + ",CloseReason=" + r);
        s.getAsyncRemote().sendText("Server is agreed close");
        wsSet.remove(s);
    }

    @OnError
    public void error(Session s, Throwable t) {
        System.out.println("@OnError:Session=" + s + ",Throwable=" + t);
    }

    public Source2() {
        System.out.println("init websocket class:source2");
    }
}
