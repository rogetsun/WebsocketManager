package com.uv.websocket;

import com.uv.util.ScanClassUtil;
import com.uv.websocket.annotation.WSServerEndpoint;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.websocket.DeploymentException;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

/**
 * Created by uv2sun on 15/11/22.
 * websocket 监听,web容器启动时,加载Endpoint实现类到ServerContainer.
 */
public class WSListener implements ServletContextListener {


    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        /**
         * web.xml配置参数,websocket的endpoint包路径.逗号隔开多个
         */
        try {
            String pkgs = servletContextEvent.getServletContext().getInitParameter("WSServerEndpointPackage");
            String urlPrefix = "";
            try {
                urlPrefix = servletContextEvent.getServletContext().getInitParameter("WSServerEndpointUrlPrefix");
            } catch (Exception e) {
                System.out.println("没有配置websocket的url前缀");
            }
//            System.out.println(pkgs);
            for (String pkg : pkgs.split(",")) {
                /**
                 * 递归扫描每个包下面有WSServerPoint注解的类,认为是endpoint实现类
                 */
                for (Class<?> aClass : ScanClassUtil.getClasses(pkg)) {
                    if (aClass.isInterface()) continue;
                    WSServerEndpoint point = aClass.getAnnotation(WSServerEndpoint.class);
                    if (point != null) {
                        String url = point.value();
                        if (url.length() > 0) {
                            /**
                             * 注册Endpoint,注意url增加了前缀
                             */
                            addEndpoint(servletContextEvent, aClass, urlPrefix, url);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加websocket 的serverEndPoint节点到服务容器,
     * 同时给节点的userProperties中增加servletContext
     *
     * @param servletContextEvent
     * @param aClass
     * @param url
     */
    private void addEndpoint(ServletContextEvent servletContextEvent, Class<?> aClass, String urlPrefix, String url) {
        /**
         * 拼接最终的URL
         */
        String URL;
        if ((urlPrefix.endsWith("/") && !url.startsWith("/"))
                || (!urlPrefix.endsWith("/") && url.startsWith("/"))) {
            URL = urlPrefix + url;
        } else if (!urlPrefix.endsWith("/") && !url.startsWith("/")) {
            URL = urlPrefix + "/" + url;
        } else {
            URL = urlPrefix + url.substring(1);
        }
        System.out.println("scan Endpoint:" + aClass + ",url:" + URL);
        /**
         * 从servletContext中取得服务容器
         */
        final ServerContainer serverContainer = (ServerContainer) servletContextEvent
                .getServletContext().getAttribute("javax.websocket.server.ServerContainer");
        /**
         * 生成endpoint实现类
         */
        ServerEndpointConfig sec = ServerEndpointConfig.Builder
                .create(aClass, URL)
                .configurator(new MyEndpointConfigurator())     //增加httpSession到userProperties
                .build();
        /**
         * 给endpoint的userProperties配置属性中增加servletContext
         */
        sec.getUserProperties().put("servletContext", servletContextEvent.getServletContext());
        try {
            /**
             * 将endpoint添加到服务容器
             */
            serverContainer.addEndpoint(sec);
        } catch (DeploymentException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    class MyEndpointConfigurator extends ServerEndpointConfig.Configurator {
        @Override
        public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
            /**
             * 如果request.getHttpSession不为空,则放入ServerEndpointConfig,
             * 进而在websocket的onOpen等方法中就可以通过EndpointConfig.getUserproperties获取到
             * 但tomcat的实现,并为将此getHttpSession得到的session关联到请求的http.HttpSession
             */
            try {
                HttpSession s = (HttpSession) request.getHttpSession();
                if (s != null) {
                    sec.getUserProperties().put(HttpSession.class.getName(), s);
                } else {
                    WSFilter.PrincipalHasSession principalHasSession = (WSFilter.PrincipalHasSession) request.getUserPrincipal();
                    sec.getUserProperties().put(HttpSession.class.getName(), principalHasSession.getSession());
                }
            } catch (Exception e) {
                System.out.println("web容器不支持websocket获取HttpSession,同时未配置过滤器WSFilter实现HttpSession的放入EndpointConfig.getUserProperties(HttpSession.class.getName()).");
                e.printStackTrace();
            }
        }

    }

}
