package org.apollo.blog.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP工具
 */
public class GZipUtils {

	public static final int BUFFER = 1024;
	public static final String EXT = ".gz";
	/**
	 * ISO-8859-1
	 */
	private static String encode = "utf-8";
	private static String hexStr = "0123456789ABCDEF";

	/**
	 * 数据压缩
	 *
	 * @param is 输入流
	 * @param os 输出流
	 */
	public static void compress(InputStream is, OutputStream os) throws IOException {

		GZIPOutputStream gos = new GZIPOutputStream(os);

		int count;
		byte[] data = new byte[BUFFER];
		while ((count = is.read(data, 0, BUFFER)) != -1) {
			gos.write(data, 0, count);
		}

		gos.finish();

		gos.flush();
		gos.close();
	}

	/**
	 * 数据解压缩
	 *
	 * @param is 输入流
	 * @param os 输出流
	 */
	public static void decompress(InputStream is, OutputStream os) throws IOException {

		GZIPInputStream gis = new GZIPInputStream(is);

		int count;
		byte[] data = new byte[BUFFER];
		while ((count = gis.read(data, 0, BUFFER)) != -1) {
			os.write(data, 0, count);
		}

		gis.close();
	}

	/**
	 * 数据压缩
	 */
	public static byte[] compress(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 压缩
		compress(bais, baos);

		byte[] output = baos.toByteArray();

		baos.flush();
		baos.close();

		bais.close();

		return output;
	}

	/**
	 * 数据解压缩
	 */
	public static byte[] decompress(byte[] data) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(data);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		// 解压缩
		decompress(bais, baos);
		data = baos.toByteArray();

		baos.flush();
		baos.close();

		bais.close();

		return data;
	}

	/**
	 * 字符串压缩
	 */
	public static String compressStr(String data) throws IOException {
		return BinaryToHexString(compress(data.getBytes(encode)));
	}

	/**
	 * 字符串解压缩
	 */
	public static String decompressStr(String data) throws IOException {
		return new String(decompress(HexStringToBinary(data)), encode);
	}

	/**
	 * 文件压缩
	 */
	public static void compress(File file) throws Exception {
		compress(file, true);
	}

	/**
	 * 文件压缩
	 *
	 * @param delete 是否删除原始文件
	 */
	public static void compress(File file, boolean delete) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(file.getPath() + EXT);

		compress(fis, fos);

		fis.close();
		fos.flush();
		fos.close();

		if (delete) {
			file.delete();
		}
	}

	/**
	 * 文件压缩
	 */
	public static void compress(String path) throws Exception {
		compress(path, true);
	}

	/**
	 * 文件压缩
	 *
	 * @param delete 是否删除原始文件
	 */
	public static void compress(String path, boolean delete) throws Exception {
		File file = new File(path);
		compress(file, delete);
	}

	/**
	 * 文件解压缩
	 */
	public static void decompress(File file) throws Exception {
		decompress(file, true);
	}

	/**
	 * 文件解压缩
	 *
	 * @param delete 是否删除原始文件
	 */
	public static void decompress(File file, boolean delete) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileOutputStream fos = new FileOutputStream(file.getPath().replace(EXT, ""));
		decompress(fis, fos);
		fis.close();
		fos.flush();
		fos.close();

		if (delete) {
			file.delete();
		}
	}

	/**
	 * 文件解压缩
	 */
	public static void decompress(String path) throws Exception {
		decompress(path, true);
	}

	/**
	 * 文件解压缩
	 *
	 * @param delete 是否删除原始文件
	 */
	public static void decompress(String path, boolean delete) throws Exception {
		File file = new File(path);
		decompress(file, delete);
	}


	public static String BinaryToHexString(byte[] bytes) {
		String result = "";
		String hex = "";
		for (int i = 0; i < bytes.length; i++) {
			//字节高4位
			hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
			//字节低4位
			hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
			result += hex;
		}
		return result;
	}

	/**
	 * @return 将十六进制转换为字节数组
	 */
	public static byte[] HexStringToBinary(String hexString) {
		//hexString的长度对2取整，作为bytes的长度
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		//字节高四位
		byte high = 0;
		//字节低四位
		byte low = 0;
		for (int i = 0; i < len; i++) {
			//右移四位得到高位
			high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
			low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
			//高地位做或运算
			bytes[i] = (byte) (high | low);
		}
		return bytes;
	}


	/**
	 * 转换成24位图的BMP
	 */
	private BufferedImage transform_Gray24BitMap(BufferedImage image) {

		int h = image.getHeight();
		int w = image.getWidth();
		// 定义数组，用来存储图片的像素
		int[] pixels = new int[w * h];
		int gray;
		PixelGrabber pg = new PixelGrabber(image, 0, 0, w, h, pixels, 0, w);
		try {
			pg.grabPixels(); // 读取像素值
		} catch (InterruptedException e) {
			throw new RuntimeException("转换成24位图的BMP时，处理像素值异常");
		}

		// 扫描列
		for (int j = 0; j < h; j++) {
			// 扫描行
			for (int i = 0; i < w; i++) {
				// 由红，绿，蓝值得到灰度值
				gray = (int) (((pixels[w * j + i] >> 16) & 0xff) * 0.8);
				gray += (int) (((pixels[w * j + i] >> 8) & 0xff) * 0.1);
				gray += (int) (((pixels[w * j + i]) & 0xff) * 0.1);
				pixels[w * j + i] = (255 << 24) | (gray << 16) | (gray << 8) | gray;
			}
		}

		MemoryImageSource s = new MemoryImageSource(w, h, pixels, 0, w);
		Image img = Toolkit.getDefaultToolkit().createImage(s);
		//如果要转换成别的位图，改这个常量即可
		BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		buf.createGraphics().drawImage(img, 0, 0, null);
		return buf;
	}

}

