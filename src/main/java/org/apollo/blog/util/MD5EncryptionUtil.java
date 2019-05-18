package org.apollo.blog.util;

import org.apache.shiro.crypto.hash.SimpleHash;

import java.util.UUID;

public class MD5EncryptionUtil {

    /**
     * 根据用户名随机生成盐值
     *
     * @param username 用户名
     */
    public static String getSalt(String username) {
        return UUID.nameUUIDFromBytes(username.getBytes()).toString().replaceAll("-", "");
    }

    /**
     * 对原始密码进行加密
     *
     * @param originalPassword 密码
     * @param salt             盐值
     * @param username         用户名
     */
    public static String encryptionPwd(String originalPassword, String salt, String username) {
        SimpleHash newPassword = new SimpleHash("md5", originalPassword, username + salt, 2);
        return newPassword.toHex();
    }
}
