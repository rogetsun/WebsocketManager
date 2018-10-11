package com.uv.websocket;

import com.uv.event.EventUtil;
import net.sf.json.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by uv2sun on 15/11/22.
 */
public class WSTrigger extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String eventName = req.getParameter("dataType");
        String data = req.getParameter("data");
        EventUtil.trigger(eventName, JSONObject.fromObject(data));
        resp.getWriter().print("{eventName:" + eventName + ", data:" + data + "}");
    }
}
