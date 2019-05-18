package org.apollo.blog.shiro.filter;

import com.alibaba.fastjson.JSON;
import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apollo.blog.exception.ResponseCode;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class ShiroLoginFilter extends FormAuthenticationFilter {
    /**
     * 在访问controller前判断是否登录，返回json，不进行重定向。
     *
     * @return true-继续往下执行，false-该filter过滤器已经处理，不继续执行其他过滤器
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws IOException {
        if (isAjax(request)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("code", ResponseCode.NO_LOGIN.getCode());
            resultMap.put("msg", ResponseCode.NO_LOGIN.getMsg());
            PrintWriter out = response.getWriter();
            out.println(JSON.toJSONString(resultMap));
            out.flush();
            out.close();
            return false;
        }
        return true;
    }

    private boolean isAjax(ServletRequest request) {
        String header = ((HttpServletRequest) request).getHeader("X-Requested-With");
        if ("XMLHttpRequest".equalsIgnoreCase(header)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
