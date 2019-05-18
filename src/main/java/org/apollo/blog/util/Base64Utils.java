package org.apollo.blog.util;

import org.apache.commons.codec.binary.Base64;

import java.io.*;

/**
 * base64 工具类
 */
public class Base64Utils {

    /**
     * 图片转base64
     *
     * @param ext  文件类型
     * @param data 字节数组
     */
    public static String getImageBASE64(String ext, byte[] data) {
        if (ext == null || data == null) {
            return null;
        }
        Base64 encoder = new Base64();
        return "data:image/" + ext.toLowerCase() + ";base64," + encoder.encode(data);
    }

    /**
     * base64转图片
     *
     * @param imgStr base64编码字符串
     * @param path   图片路径-具体到文件
     */
    public static boolean generateImage(String imgStr, String path) {
        if (imgStr == null || path == null) {
            return false;
        }
        String base64img = imgStr.substring(imgStr.indexOf(",") + 1);
        Base64 decoder = new Base64();
        try {
            // 解密
            byte[] b = decoder.decode(base64img);
            // 处理数据
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {
                    b[i] += 256;
                }
            }
            OutputStream out = new FileOutputStream(path);
            out.write(b);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 文件读取缓冲区大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * BASE64字符串解码为二进制数据
     */
    public static byte[] decode(String base64) throws Exception {
        return Base64.decodeBase64(base64.getBytes());
    }

    /**
     * 二进制数据编码为BASE64字符串
     */
    public static String encode(byte[] bytes) throws Exception {
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * 将文件编码为BASE64字符串
     * 大文件慎用，可能会导致内存溢出
     *
     * @param filePath 文件绝对路径
     */
    public static String encodeFile(String filePath) throws Exception {
        byte[] bytes = fileToByte(filePath);
        return encode(bytes);
    }

    /**
     * BASE64字符串转回文件
     *
     * @param filePath 文件绝对路径
     * @param base64   编码字符串
     */
    public static void decodeToFile(String filePath, String base64) throws Exception {
        byte[] bytes = decode(base64);
        byteArrayToFile(bytes, filePath);
    }

    /**
     * 文件转换为二进制数组
     *
     * @param filePath 文件路径
     */
    public static byte[] fileToByte(String filePath) throws Exception {
        byte[] data = new byte[0];
        File file = new File(filePath);
        if (file.exists()) {
            FileInputStream in = new FileInputStream(file);
            ByteArrayOutputStream out = new ByteArrayOutputStream(2048);
            byte[] cache = new byte[CACHE_SIZE];
            int nRead = 0;
            while ((nRead = in.read(cache)) != -1) {
                out.write(cache, 0, nRead);
                out.flush();
            }
            out.close();
            in.close();
            data = out.toByteArray();
        }
        return data;
    }

    /**
     * 二进制数据写文件
     *
     * @param bytes    二进制数据
     * @param filePath 文件生成目录
     */
    public static void byteArrayToFile(byte[] bytes, String filePath) throws Exception {
        InputStream in = new ByteArrayInputStream(bytes);
        File destFile = new File(filePath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        destFile.createNewFile();
        OutputStream out = new FileOutputStream(destFile);
        byte[] cache = new byte[CACHE_SIZE];
        int nRead = 0;
        while ((nRead = in.read(cache)) != -1) {
            out.write(cache, 0, nRead);
            out.flush();
        }
        out.close();
        in.close();
    }
}
