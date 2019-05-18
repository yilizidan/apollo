package org.apollo.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author penwei
 */
@Slf4j
public final class HttpUtils {


	private final static String[] ignoreIP = {"192.168", "0.0.0.0"};

	/**
	 * 获取客户端ip地址
	 */
	public static String getCliectIp(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.trim() == "" || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.trim() == "" || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.trim() == "" || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		// 多个路由时，取第一个非unknown的ip
		final String[] arr = ip.split(",");
		for (final String str : arr) {
			if (!"unknown".equalsIgnoreCase(str)) {
				ip = str;
				break;
			}
		}
		return ip;
	}

	public static boolean isIgnore(String ip) {
		if (StringUtils.isBlank(ip)) {
			throw new IllegalArgumentException("ip参数不能为空");
		}
		for (String ignoreIp : ignoreIP) {
			if (ip.equals(ignoreIp) || ip.indexOf(ignoreIp) > -1) {
				log.debug("{} is in ignore table", ip);
				return true;
			}
		}
		return false;
	}


	/***
	 * 获取 request 中 json 字符串的内容
	 */
	public static String getRequestJsonString(HttpServletRequest request)
			throws IOException {
		String submitMehtod = request.getMethod();
		// GET
		if ("GET".equalsIgnoreCase(submitMehtod)) {
			return new String(request.getQueryString().getBytes("iso-8859-1"), "utf-8").replaceAll("%22", "\"");
			// POST
		} else {
			return getRequestPostStr(request);
		}
	}

	/**
	 * 描述:获取 post 请求的 byte[] 数组
	 * <pre>
	 * 举例：
	 * </pre>
	 */
	public static byte[] getRequestPostBytes(HttpServletRequest request)
			throws IOException {
		int contentLength = request.getContentLength();
		if (contentLength < 0) {
			return null;
		}
		byte buffer[] = new byte[contentLength];
		for (int i = 0; i < contentLength; ) {

			int readlen = request.getInputStream().read(buffer, i,
					contentLength - i);
			if (readlen == -1) {
				break;
			}
			i += readlen;
		}
		return buffer;
	}

	/**
	 * 描述:获取 post 请求内容
	 * <pre>
	 * 举例：
	 * </pre>
	 */
	public static String getRequestPostStr(HttpServletRequest request)
			throws IOException {
		byte buffer[] = getRequestPostBytes(request);
		String charEncoding = request.getCharacterEncoding();
		if (charEncoding == null) {
			charEncoding = "UTF-8";
		}
		return new String(buffer, charEncoding);
	}

	/**
	 * 判断是否为ajax请求
	 */
	public static String getRequestType(HttpServletRequest request) {
		return request.getHeader("x-requested-with");
	}


	public static void porxyUrl(String url, OutputStream outputStream) {
		if (StringUtils.isBlank(url)) {
			return;
		}
		byte[] buf = new byte[1024];
		int size = 0;
		HttpURLConnection httpUrl = null;
		try {
			URL urlObject = new URL(url);
			httpUrl = (HttpURLConnection) urlObject.openConnection();
			httpUrl.connect();
		} catch (MalformedURLException e) {
			log.error("porxyheadimg MalformedURLException", e);
		} catch (IOException e) {
			log.error("porxyheadimg IOException", e);
		}
		if (httpUrl == null) {
			return;
		}
		try (OutputStream os = outputStream;
		     InputStream stm = httpUrl.getInputStream();
		     BufferedInputStream bis = new BufferedInputStream(stm)) {
			while ((size = bis.read(buf)) != -1) {
				os.write(buf, 0, size);
			}
			os.flush();
			httpUrl.disconnect();
		} catch (IOException e) {
			log.error("代理用户微信头像", e);
		}
	}

}