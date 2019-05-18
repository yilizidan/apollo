package org.apollo.blog.service.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.Properties;

@Slf4j
@Component
public class PersonCacheDataUtil {

    public String getPersonPic(Long userid) {
        if (PersonCacheDataUtil.class.getClassLoader().getResource("properties/init.properties") != null) {
            try (InputStream in = PersonCacheDataUtil.class.getClassLoader().getResource("properties/init.properties").openStream()) {
                Properties properties = new Properties();
                properties.load(in);
                return properties.getProperty("user_image");

            } catch (IOException e) {
                log.error("获取默认头像失败 " + e.getMessage(), e);
            }
        }
        return null;

        /*String propic = null;
        if (PersonCacheDataUtil.class.getClassLoader().getResource("properties/" + userid + ".properties") != null) {
            try (InputStream in = PersonCacheDataUtil.class.getClassLoader().getResource("properties/" + userid + ".properties").openStream()) {
                Properties properties = new Properties();
                properties.load(in);
                propic = properties.getProperty("userImage");
            } catch (IOException e) {
                log.error("获取头像失败 " + e.getMessage(), e);
                propic = null;
            }
        } else {
            if (PersonCacheDataUtil.class.getClassLoader().getResource("properties/init.properties") != null) {
                try (InputStream in = PersonCacheDataUtil.class.getClassLoader().getResource("properties/init.properties").openStream()) {
                    Properties properties = new Properties();
                    properties.load(in);
                    propic = properties.getProperty("user_image");

                    String classPath = PersonCacheDataUtil.class.getClassLoader().getResource("").toString();
                    String prefix = classPath.substring(classPath.indexOf(":") + 1);
                    String suffix = "properties/" + userid + ".properties";
                    File file = new File(prefix + suffix);
                    file.createNewFile();
                    String path = file.getAbsolutePath();

                    try (OutputStream out = new FileOutputStream(path)) {
                        Properties pt = new Properties();
                        pt.setProperty("userImage", propic);
                        pt.store(out, "用户头像");
                    } catch (FileNotFoundException e) {
                        log.error("ModulusAndExponent.properties is not found.", e);
                    } catch (IOException e) {
                        log.error("OutputStream output failed.", e);
                    }

                } catch (IOException e) {
                    log.error("获取默认头像失败 " + e.getMessage(), e);
                    propic = null;
                }
            }
        }
        return propic;*/
    }

    public void uptPersonPic(Long userid, String propic) {
        Properties properties = new Properties();
        //存放密钥的绝对地址
        String path = null;
        try {
            path = PersonCacheDataUtil.class.getClassLoader().getResource("properties/" + userid + ".properties").toString();
            path = path.substring(path.indexOf(":") + 1);
        } catch (NullPointerException e) {
            //如果不存#fileName#就创建
            String classPath = PersonCacheDataUtil.class.getClassLoader().getResource("").toString();
            String prefix = classPath.substring(classPath.indexOf(":") + 1);
            String suffix = "properties/" + userid + ".properties";
            File file = new File(prefix + suffix);
            try {
                file.createNewFile();
                path = file.getAbsolutePath();
            } catch (IOException e1) {
                log.error(userid + ".properties" + " create fail.", e1);
            }
        }
        try (OutputStream out = new FileOutputStream(path)) {
            properties.setProperty("userImage", propic);
            properties.store(out, "用户头像");
        } catch (FileNotFoundException e) {
            log.error("ModulusAndExponent.properties is not found.", e);
        } catch (IOException e) {
            log.error("OutputStream output failed.", e);
        }
    }
}
