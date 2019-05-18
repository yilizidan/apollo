package org.apollo.blog.filter;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Enumeration;

/**
 *
 * 解决在Filter中读取Request中的流后，后续controller或restful（@RequestBody）接口中无法获取流的问题
 *
 * @author penwei
 * @date   2018年11月9日 下午8:52:37
 *
 */
public class BodyReaderHttpServletRequestWrapper extends
		HttpServletRequestWrapper {

   private final byte[] body;

   public BodyReaderHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
       super(request);
       body = HttpHelper.getBodyString(request).getBytes(Charset.forName("UTF-8"));
   }

   @Override
   public BufferedReader getReader() throws IOException {
       return new BufferedReader(new InputStreamReader(getInputStream()));
   }

   @Override
   public ServletInputStream getInputStream() throws IOException {

       final ByteArrayInputStream bais = new ByteArrayInputStream(body);

       return new ServletInputStream() {
           @Override
           public int read() throws IOException {
               return bais.read();
           }

           @Override
           public boolean isFinished() {
               // TODO Auto-generated method stub
               return false;
           }

           @Override
           public boolean isReady() {
               // TODO Auto-generated method stub
               return false;
           }

           @Override
           public void setReadListener(ReadListener listener) {
               // TODO Auto-generated method stub

           }
       };
   }

   @Override
   public String getHeader(String name) {
       return super.getHeader(name);
   }

   @Override
   public Enumeration<String> getHeaderNames() {
       return super.getHeaderNames();
   }

   @Override
   public Enumeration<String> getHeaders(String name) {
       return super.getHeaders(name);
   }

}