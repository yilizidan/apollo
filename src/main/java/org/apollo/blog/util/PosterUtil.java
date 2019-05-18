package org.apollo.blog.util;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.google.zxing.WriterException;
import lombok.extern.slf4j.Slf4j;
import org.apollo.blog.util.zxing.QRCodeFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 海报生成工具
 *
 * @author liuqianshun
 * @date 2018/9/10 16:34:10
 */
@Slf4j
@Component
public class PosterUtil {

	@Resource
	private QRCodeFactory qrCodeFactory;

	public static void main(String[] args) throws Exception {
		new PosterUtil().generate("https://pub.file.k12.vip/2019/03/07/1103489173777678338.png",
				"http://thirdwx.qlogo.cn/mmopen/ibibYZlia1BIUxjvUcID83K9ksyFXNaIcOElPRQkjfHO6GJRZLoXUEf7h82ict3C1LK089bib53H6xzBj4Zlt3qR5ekb6IwvZEllia/132",
				"wa1ker77", "熊叔带你说英语", "http://yoyo.is-programmer.com/posts/12748.html");
		new PosterUtil().generateCard("http://yoyo.is-programmer.com/posts/12748.html",
				"http://thirdwx.qlogo.cn/mmopen/ibibYZlia1BIUxjvUcID83K9ksyFXNaIcOElPRQkjfHO6GJRZLoXUEf7h82ict" +
						"3C1LK089bib53H6xzBj4Zlt3qR5ekb6IwvZEllia/132", "一只鱼");
	}

	/**
	 * 功能描述:图片缩放,w，h为缩放的目标宽度和高度
	 * src为源文件目录，dest为缩放后保存目录
	 */
	public void zoomImage(String src, String dest, int w, int h) throws Exception {
		double wr = 0, hr = 0;
		File srcFile = new File(src);
		File destFile = new File(dest);
		//读取图片
		BufferedImage bufImg = ImageIO.read(srcFile);
		//获取缩放比例
		wr = w * 1.0 / bufImg.getWidth();
		hr = h * 1.0 / bufImg.getHeight();

		AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
		Image Itemp = ato.filter(bufImg, null);
		try {
			//写入缩减后的图片
			ImageIO.write((BufferedImage) Itemp, dest.substring(dest.lastIndexOf(".") + 1), destFile);
		} catch (Exception ex) {
			log.error("图片缩放转换异常", ex);
		}
	}

	/**
	 * 功能描述:打卡分享
	 */
	public String generateCard(String url, String headImgUrl, String nickname) throws Exception {
		Queue<File> queue = new LinkedList<>();
		// 获取新文件的地址
		File path = new File(ResourceUtils.getURL("classpath:").getPath());
		if (!path.exists()) {
			path = new File("");
		}
		File upload = new File(path.getAbsolutePath(), "static/upload/");
		if (!upload.exists()) {
			upload.mkdirs();
		}

		// 0.白色画布
		String whiteBottom = getWhiteBottom();
		File whiteBottomFile = new File(whiteBottom);
		queue.offer(whiteBottomFile);

		// 1.把头像url保存带Java临时目录
		String headImg = downloadToTempDir(headImgUrl);
		String headImgRound = squareToRound(new File(headImg));
		File headImgFile = new File(headImgRound);
		queue.offer(headImgFile);

		// 3.生成二维码
		String content = url;
		//https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx983da8778930f639&redirect_uri=http%3A%2F%2Fxiongfan.s1.natapp.cc%2Fwechat%2Foauth2%2FgetOAuth2UserInfo&response_type=code&scope=snsapi_userinfo&state=/wx/index&connect_redirect=1#wechat_redirect";
		String qr = createQrToTempDir(content, "jpg", null, new int[]{200, 200});
		File qrFile = new File(qr);
		queue.offer(qrFile);

		// 添加字体的属性设置
		Font nicknameFont = new Font("微软雅黑", Font.PLAIN, 48);
		Font descFont = new Font("微软雅黑", Font.PLAIN, 32);
		// 加载白色底部
		BufferedImage imageWhiteBottom = ImageIO.read(whiteBottomFile);
		// 加载头像
		BufferedImage imageHead = ImageIO.read(headImgFile);
		// 加载二维码
		BufferedImage imageQr = ImageIO.read(qrFile);
		// 以本地图片为模板
		Graphics2D g = imageWhiteBottom.createGraphics();
		// 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
		g.drawImage(imageHead, (imageWhiteBottom.getWidth() - imageHead.getWidth()) / 2, 200, 100, 100, null);
		g.drawImage(imageQr, (imageWhiteBottom.getWidth() - imageQr.getWidth()) / 2, 1022, 200, 200, null);
		// 添加昵称
		g.setFont(nicknameFont);
		g.setColor(Color.BLACK);
		int nameWidth = g.getFontMetrics().stringWidth(nickname);
		g.drawString(nickname, (imageWhiteBottom.getWidth() - nameWidth) / 2, 400);
		String name1 = "获课预习宝，slogan";
		int width1 = g.getFontMetrics().stringWidth(name1);
		g.drawString(name1, (imageWhiteBottom.getWidth() - width1) / 2, 1000);

		g.setFont(descFont);
		g.setColor(Color.BLACK);
		String name2 = "在获课预习宝学语文";
		int width2 = g.getFontMetrics().stringWidth(name2);
		g.drawString(name2, (imageWhiteBottom.getWidth() - width2) / 2, 450);
		// 完成模板修改
		g.dispose();


		String filename = IdWorker.getId() + ".png";
		String outPutFile = upload + File.separator + filename;
		// 生成新的合成过的用户二维码并写入新图片
		ImageIO.write(imageWhiteBottom, "png", new File(outPutFile));

		deleteFile(queue);

		return null;
	}


	/**
	 * 删除文件
	 *
	 * @param queue 文件队列
	 */
	private void deleteFile(Queue<File> queue) {
		int i = 0;
		final int queueSize = queue.size() * 2;
		//防止死循环
		while (!queue.isEmpty() || i < queueSize) {
			File file = queue.poll();
			if (file != null) {
				try {
					file.delete();
				} catch (Exception e) {
					queue.offer(file);
				}
			}
			i++;
		}
	}


	/**
	 * @param url        海报url
	 * @param headImgUrl 头像url
	 * @param nickname   昵称
	 * @param courseName 课程名称
	 * @param linkurl    链接地址
	 */
	public String generate(String url, String headImgUrl, String nickname, String courseName, String linkurl) throws Exception {
		Queue<File> queue = new LinkedList<>();
		try {
			// 获取新文件的地址
			File path = new File(ResourceUtils.getURL("classpath:").getPath());
			if (!path.exists()) {
				path = new File("");
			}
			File upload = new File(path.getAbsolutePath(), "static/upload/");
			if (!upload.exists()) {
				upload.mkdirs();
			}

			// 0.白色画布
			String whiteBottom = getWhiteBottom();
			File whiteBottomFile = new File(whiteBottom);
			queue.offer(whiteBottomFile);

			// 1.把海报保存到Java临时目录
			String backPoster = downloadToTempDir(url);
			String filename1 = IdWorker.getId() + ".jpg";
			String outPutFile1 = upload + File.separator + filename1;
			zoomImage(backPoster, outPutFile1, 736, 980);
			File backPosterFile = new File(outPutFile1);
			queue.offer(backPosterFile);

			// 2.把头像url保存带Java临时目录
			String headImg = downloadToTempDir(headImgUrl);
			String headImgRound = squareToRound(new File(headImg));
			File headImgFile = new File(headImgRound);
			queue.offer(headImgFile);

			// 3.生成二维码
			//https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx983da8778930f639&redirect_uri=http%3A%2F%2Fxiongfan.s1.natapp.cc%2Fwechat%2Foauth2%2FgetOAuth2UserInfo&response_type=code&scope=snsapi_userinfo&state=/wx/index&connect_redirect=1#wechat_redirect";
			String qr = createQrToTempDir(linkurl, "jpg", null, new int[]{200, 200});
			File qrFile = new File(qr);
			queue.offer(qrFile);

			// 添加字体的属性设置
			Font nicknameFont = new Font("微软雅黑", Font.PLAIN, 48);
			Font descFont = new Font("微软雅黑", Font.PLAIN, 32);
			// 加载白色底部
			BufferedImage imageWhiteBottom = ImageIO.read(whiteBottomFile);
			// 加载海报图
			BufferedImage imagePoster = ImageIO.read(backPosterFile);
			// 加载头像
			BufferedImage imageHead = ImageIO.read(headImgFile);
			// 加载二维码
			BufferedImage imageQr = ImageIO.read(qrFile);
			// 以本地图片为模板
			Graphics2D g = imageWhiteBottom.createGraphics();
			// 在模板上添加用户二维码(地址,左边距,上边距,图片宽度,图片高度,未知)
			g.drawImage(imagePoster, 32, 32, 736, 980, null);
			g.drawImage(imageHead, 64, 960, 100, 100, null);
			g.drawImage(imageQr, 572, 1022, 200, 200, null);
			// 添加昵称
			g.setFont(nicknameFont);
			g.setColor(Color.BLACK);
			g.drawString(nickname, 64, imageWhiteBottom.getHeight() - 240);
			g.setFont(descFont);
			g.setColor(Color.GRAY);
			g.drawString("已报名学习", 64, imageWhiteBottom.getHeight() - 205);
			// 添加课程
			g.setFont(nicknameFont);
			g.setColor(Color.BLACK);
			if (courseName.length() > 11) {
				g.drawString(courseName.substring(0, 11), 64, imageWhiteBottom.getHeight() - 130);
				String endStr = courseName.substring(11, courseName.length() - 1);
				if (endStr.length() > 11) {
					endStr = endStr.substring(0, 10) + "...";
				}
				g.drawString(endStr, 64, imageWhiteBottom.getHeight() - 70);
			} else {
				g.drawString(courseName, 64, imageWhiteBottom.getHeight() - 130);
			}
			// 完成模板修改
			g.dispose();


			String filename = IdWorker.getId() + ".png";
			String outPutFile = upload + File.separator + filename;
			// 生成新的合成过的用户二维码并写入新图片
			ImageIO.write(imageWhiteBottom, "png", new File(outPutFile));

			deleteFile(queue);

			return outPutFile;

		} catch (Exception e) {
			log.error("海报生成异常", e);
		}
		return null;
	}


	/**
	 * 功能描述: 生成二维码到临时目录
	 */
	private String createQrToTempDir(String content, String format, String logUri, int[] size)
			throws IOException, WriterException {
		File path = new File(ResourceUtils.getURL("classpath:").getPath());
		if (!path.exists()) {
			path = new File("");
		}
		File upload = new File(path.getAbsolutePath(), "static/upload/");
		if (!upload.exists()) {
			upload.mkdirs();
		}
		String outPutFile = upload + File.separator + IdWorker.getId() + ".jpg";
		qrCodeFactory.creatQrImage(content, format, outPutFile, logUri, size);
		return outPutFile;
	}

	/**
	 * 功能描述: 下载网络资源到临时目录
	 */
	private String downloadToTempDir(String urlStr) throws IOException {
		File path = new File(ResourceUtils.getURL("classpath:").getPath());
		if (!path.exists()) {
			path = new File("");
		}
		File upload = new File(path.getAbsolutePath(), "static/upload/");
		if (!upload.exists()) {
			upload.mkdirs();
		}
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		//设置超时间为3秒
		conn.setConnectTimeout(3 * 1000);
		//防止屏蔽程序抓取而返回403错误
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		//得到输入流
		InputStream inputStream = conn.getInputStream();
		//获取自己数组
		byte[] getData = readInputStream(inputStream);
		//文件保存位置
		//File saveDir = new File(path);
		if (!upload.exists()) {
			upload.mkdir();
		}
		File file = new File(upload + File.separator + IdWorker.getId() + ".jpg");
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(getData);
		if (fos != null) {
			fos.close();
		}
		if (inputStream != null) {
			inputStream.close();
		}
		return file.toString();
	}

	/**
	 * 从输入流中获取字节数组
	 */
	public byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}


	/**
	 * 功能描述: 把正方形图片切成圆形
	 */
	private String squareToRound(File file) throws IOException {
		BufferedImage bi1 = ImageIO.read(file);
		// 创建一个带透明色的BufferedImage
		BufferedImage image = new BufferedImage(bi1.getWidth(), bi1.getHeight(), BufferedImage.TYPE_INT_ARGB);
		// 创建一个椭圆形的2D图像
		Ellipse2D.Double shape = new Ellipse2D.Double(0, 0, bi1.getWidth(), bi1.getHeight());
		Graphics2D g2 = image.createGraphics();
		image = g2.getDeviceConfiguration()
				.createCompatibleImage(bi1.getWidth(), bi1.getHeight(), Transparency.TRANSLUCENT);
		g2 = image.createGraphics();
		g2.setComposite(AlphaComposite.Clear);
		g2.fill(new Rectangle(image.getWidth(), image.getHeight()));
		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
		g2.setClip(shape);
		g2.drawImage(bi1, 0, 0, null);
		g2.dispose();
		File path = new File(ResourceUtils.getURL("classpath:").getPath());
		if (!path.exists()) {
			path = new File("");
		}
		File upload = new File(path.getAbsolutePath(), "static/upload/");
		if (!upload.exists()) {
			upload.mkdirs();
		}
		String outPutFile = upload + File.separator + IdWorker.getId() + ".jpg";
		ImageIO.write(image, "jpg", new File(outPutFile));
		return outPutFile;
	}


	/**
	 * 功能描述: 获取白色底部画布
	 */
	private String getWhiteBottom() {
		BufferedImage image = new BufferedImage(800, 1360, BufferedImage.TYPE_INT_RGB);
		//获取画布
		Graphics2D g = (Graphics2D) image.getGraphics();
		//画长方形
		g.setColor(Color.white);
		g.fillRect(0, 0, 800, 1360);
		//画长方形
		g.setColor(Color.LIGHT_GRAY);
		g.fillRect(50, 80, 700, 800);
		//执行
		g.dispose();
		//输出图片结果
		try {
			File path = new File(ResourceUtils.getURL("classpath:").getPath());
			if (!path.exists()) {
				path = new File("");
			}
			File upload = new File(path.getAbsolutePath(), "static/upload/");
			String outPutFile = upload + File.separator + IdWorker.getId() + ".jpg";
			ImageIO.write(image, "jpg", new File(outPutFile));
			return outPutFile;
		} catch (IOException e) {
			return null;
		}
	}
}
