package org.jee.boot.client.web.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "CorsFilter", urlPatterns = "/*")
public class CorsFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        String origin = request.getHeader("Origin");
        response.addHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type,Content-Length,Authorization,Access,X-Requested-With,Yxtu,token");
        //IE下为了能跨域ajax中写入cookie，还需要加入一个HttpHeader：
        response.setHeader("P3P", "CP=\"CAO PSA OUR\"");
        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }

    private void responseWrite(String json, HttpServletResponse response) {
        try {
            response.setHeader("Content-Type", "application/json;charset=UTF-8");
            response.getWriter().write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
