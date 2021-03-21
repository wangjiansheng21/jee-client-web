package org.jee.boot.client.web.filter;

import com.alibaba.fastjson.JSON;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.jee.boot.client.web.service.user.UserService;
import org.jee.boot.client.web.vo.UserSessionVO;
import org.jee.boot.dubbo.ReponseCodeEnum;
import org.jee.boot.dubbo.response.RpcResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebFilter(filterName = "ApplicationFilter", urlPatterns = "/*", initParams = {
        @WebInitParam(name = "ssoLogin", value = "/sso/"),
        @WebInitParam(name = "health", value = "health"),
        @WebInitParam(name = "wx", value = "wx"),
        @WebInitParam(name = "api-docs", value = "/v2/api-docs"),
        @WebInitParam(name = "login", value = "login"),
        @WebInitParam(name = "commonController", value = "commonController"),
        @WebInitParam(name = "cron", value = "/cron/"),
        @WebInitParam(name = "encoding", value = "utf-8"),
        @WebInitParam(name = "cron-quartz", value = "cron-quartz")
})
@Slf4j
public class ApplicationFilter implements Filter {

    private FilterConfig config;

    /**
     * 统一标识的key
     */
    private static String KEY_COOKIE_LOGIN_FLAG = "yxt_u";

    /**
     * session中存放简单用户信息的key
     */
    private static String KEY_SSO_SESSION_USER = "sso_user";

    /**
     * 排除在外的URL
     */
    private static List<String> excludePaths = null;

    /**
     * 静态资源
     */
    private static String staticFileSuffix = null;

    @Autowired
    UserService userService;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        log.info("=====================sessionFilter init finish=========================");
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain fc) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) resp;
        HttpServletRequest request = (HttpServletRequest) req;
        String authtoken = request.getHeader("token");
        log.info("authtoken:----------------->" + authtoken);
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        // 获取配置参数
        String login = config.getInitParameter("login");
        String wx = config.getInitParameter("wx");
        String api_docs = config.getInitParameter("api-docs");
        String commonController = config.getInitParameter("commonController");
        String health = config.getInitParameter("health");
        String cron = config.getInitParameter("cron");
        String cron_quartz = config.getInitParameter("cron-quartz");

        // 不带http://域名:端口的地址
        String uri = request.getRequestURI();
        log.info("uri:-------------------->" + uri);

        // 排除路径不需要通过鉴权过滤
        if (uri.contains(commonController) || uri.contains(login) || uri.contains(wx)
                || uri.contains(api_docs) || uri.contains(cron) || uri.contains(health) || uri.contains(cron_quartz)) {
            fc.doFilter(request, response);
            return;
        }
        //token 缺失
        if (StringUtils.isEmpty(authtoken)) {
            responseWrite(JSON.toJSONString(RpcResponse.build(ReponseCodeEnum.TOKEN_LOST)), response);
            return;
        }
        //获取用户信息
        UserSessionVO userSessionVO = userService.getUserSessionVO(authtoken);
        if (userSessionVO == null) {
            responseWrite(JSON.toJSONString(RpcResponse.build(ReponseCodeEnum.TOKEN_EXPIRE)), response);
            return;
        }
        // 和线程绑定用户信息 TODO
        try {
            fc.doFilter(request, response);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("an error has occurred!", e);
            throw e;
        } catch (ServletException e) {
            log.error("an error has occurred!", e);
            throw e;
        } finally {

        }

    }


    @Override
    public void destroy() {
        log.debug("Destroy ApplicationFilter");
    }

    private void responseWrite(String json, HttpServletResponse response) {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 处理跨域问题
     *
     * @param req
     * @param res
     */
    public static void dealDomain(ServletRequest req, ServletResponse res) {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String origin = request.getHeader("Origin");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length,Authorization,Access,X-Requested-With,Yxtu,token,identityId");
        //IE下为了能跨域ajax中写入cookie，还需要加入一个HttpHeader：
        response.setHeader("P3P", "CP=\"CAO PSA OUR\"");
    }
}
