package org.apollo.blog.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * cookie操作工具类
 */
public class CookieUtils {

    public static Cookie[] getCookies(HttpServletRequest request) {
        return request.getCookies();
    }

    public static void showCookie(HttpServletRequest request) {
        Cookie[] c = getCookies(request);
        for (int i = 0; i < (c == null ? 0 : c.length); i++) {
            System.out.println("一条cookie____  name: " + c[i].getName() + "  || value: " + c[i].getValue());
        }
    }

    public static void saveCookie(HttpServletResponse response, Cookie cookie) {
        response.addCookie(cookie);
    }

    /**
     * 添加cookie
     */
    public static void addCookie(HttpServletResponse response, String name, Object object) {
        try {
            Cookie cookie = new Cookie(name, (String) object);
            cookie.setPath("/");
            // 设置保存cookie最大时长
            cookie.setMaxAge(Integer.MAX_VALUE);
            saveCookie(response, cookie);
        } catch (Exception e) {
            System.out.println(" -------添加cookie 失败！--------" + e.getMessage());
        }
    }

    /**
     * 获取cookie
     */
    public static <T> T getCookie(HttpServletRequest request, String name, Class<T> clazz) {
        try {

            Cookie[] cookies = getCookies(request);
            String v = null;
            for (int i = 0; i < (cookies == null ? 0 : cookies.length); i++) {
                if ((name).equalsIgnoreCase(cookies[i].getName())) {
                    v = cookies[i].getValue();
                }
            }
            if (v != null) {
                return new Gson().fromJson(v, clazz);
            }
        } catch (Exception e) {
            System.out.println("------获取 clazz Cookie 失败----- " + e.getMessage());
        }
        return null;
    }

    /**
     * 获取cookie
     */
    public static String getCookie(HttpServletRequest request, String name) {
        try {

            Cookie[] cookies = getCookies(request);

            for (int i = 0; i < (cookies == null ? 0 : cookies.length); i++) {
                if ((name).equalsIgnoreCase(cookies[i].getName())) {
                    return cookies[i].getValue();
                }
            }
        } catch (Exception e) {
            System.out.println(" --------获取String cookie 失败--------   " + e.getMessage());
        }
        return null;
    }

    /**
     * 删除cookie
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        try {

            Cookie[] cookies = getCookies(request);
            for (int i = 0; i < (cookies == null ? 0 : cookies.length); i++) {
                if ((name).equalsIgnoreCase(cookies[i].getName())) {
                    Cookie cookie = new Cookie(name, "");
                    cookie.setPath("/");
                    // 设置保存cookie最大时长为0，即使其失效
                    cookie.setMaxAge(0);
                    saveCookie(response, cookie);
                }
            }

        } catch (Exception e) {
            System.out.println(" -------删除cookie失败！--------" + e.getMessage());
        }
    }
}