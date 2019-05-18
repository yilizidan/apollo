package org.apollo.blog.util;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 图片压缩工具类
 */
public class ThumbnailsUtil {


    public static void commpress(File srcfile, File desfile, double accuracy) throws IOException {
        Thumbnails.of(srcfile).scale(1f).outputQuality(accuracy).toFile(desfile);
    }

    /**
     * 按照比例进行缩放 scale(比例)
     *
     * @param srcPath  源图片地址
     * @param desPath  目标图片地址
     * @param accuracy 精度，压缩的比率，建议小于0.9
     * @throws IOException
     */
    public static void scale(String srcPath, String desPath, double accuracy) throws IOException {
        Thumbnails.of(srcPath).scale(accuracy).toFile(desPath);
    }

    /**
     * 按照比例进行缩放 scale(比例)
     *
     * @param desfile  源图片File
     * @param srcfile  目标图片File
     * @param accuracy 精度，压缩的比率，建议小于0.9
     * @throws IOException
     */
    public static void scale(File srcfile, File desfile, double accuracy) throws IOException {
        Thumbnails.of(srcfile).scale(accuracy).toFile(desfile);
    }

    /**
     * 不按照比例，指定大小进行缩放
     * keepAspectRatio(false) 默认是按照比例缩放的
     *
     * @param srcPath 源图片地址
     * @param desPath 目标图片地址
     * @param width   指定宽度
     * @param height  指定高度
     * @throws IOException
     */
    public static void size(String srcPath, String desPath, int width, int height) throws IOException {
        Thumbnails.of(srcPath).size(width, height).keepAspectRatio(false).scale(1).toFile(desPath);
    }

    /**
     * 旋转
     *
     * @param srcPath 源图片地址
     * @param desPath 目标图片地址
     * @param rotate  (角度),正数：顺时针 负数：逆时针
     * @throws IOException
     */
    public static void rotate(String srcPath, String desPath, int rotate) throws IOException {
        /**
         * rotate(角度),正数：顺时针 负数：逆时针
         */
        Thumbnails.of(srcPath).rotate(rotate).scale(1).toFile(desPath);
    }

    /**
     * 水印
     *
     * @param positions     枚举值
     * @param srcPath       源图片地址
     * @param desPath       目标图片地址
     * @param watermarkPath 水印图地址
     * @param opacity       透明度
     * @throws IOException
     */
    public static void watermark(Positions positions, String srcPath, String desPath, String watermarkPath, float opacity) throws IOException {
        /**
         * watermark(位置，水印图，透明度)
         */
        Thumbnails.of(srcPath).watermark(positions, ImageIO.read(new File(watermarkPath)), opacity).scale(1).outputQuality(1).toFile(desPath);
    }

    /**
     * 裁剪 指定坐标
     *
     * @param srcPath 源图片地址
     * @param desPath 目标图片地址
     * @param x       指定x坐标
     * @param y       指定y坐标
     * @param width   裁剪区域宽
     * @param height  裁剪区域高
     * @throws IOException
     */
    public static void sourceRegion(String srcPath, String desPath, int x, int y, int width, int height) throws IOException {
        Thumbnails.of(srcPath).sourceRegion(x, y, width, height).size(width, height).keepAspectRatio(false).scale(1).toFile(desPath);
    }

    /**
     * 裁剪
     *
     * @param positions 枚举值
     * @param srcPath   源图片地址
     * @param desPath   目标图片地址
     * @param width     区域宽
     * @param height    区域高
     * @throws IOException
     */
    public static void sourceRegion(Positions positions, String srcPath, String desPath, int width, int height) throws IOException {
        Thumbnails.of(srcPath).sourceRegion(positions, width, height).size(width, height).keepAspectRatio(false).scale(1).toFile(desPath);
    }

    /**
     * 转化图像格式
     *
     * @param srcPath 源图片地址
     * @param desPath 目标图片地址
     * @param format  图像格式
     * @throws IOException
     */
    public static void outputFormat(String srcPath, String desPath, String format) throws IOException {
        Thumbnails.of(srcPath).outputFormat(format).scale(1).toFile(desPath);
    }

    public static void outputFormat(File srcPath, File desPath) throws IOException {
        Thumbnails.of(srcPath).outputFormat("jpg").scale(1).toFile(desPath);
    }

    /**
     * 输出到OutputStream
     *
     * @param srcPath 源图片地址
     * @param desPath 目标图片地址
     * @throws IOException
     */
    public static OutputStream toOutputStream(String srcPath, String desPath) throws IOException {
        OutputStream os = new FileOutputStream(desPath);
        Thumbnails.of(srcPath).toOutputStream(os);
        return os;
    }

    /**
     * 输出到BufferedImage
     *
     * @param srcPath 源图片地址
     * @param desPath 目标图片地址
     * @param ext     文件类型
     * @throws IOException
     */
    public static BufferedImage asBufferedImage(String srcPath, String desPath, String ext) throws IOException {
        //asBufferedImage() 返回BufferedImage
        BufferedImage thumbnail = Thumbnails.of(srcPath).asBufferedImage();
        ImageIO.write(thumbnail, ext, new File(desPath));
        return thumbnail;
    }
}
