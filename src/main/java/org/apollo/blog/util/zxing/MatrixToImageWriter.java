package org.apollo.blog.util.zxing;

import com.google.zxing.common.BitMatrix;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Order
 * @date 2018/8/22 14:08:22
 */
@Slf4j
@Component
public class MatrixToImageWriter {
	//用于设置图案的颜色
	private static final int BLACK = 0xFF000000;
	//用于背景色
	private static final int WHITE = 0xFFFFFFFF;

	private MatrixToImageWriter() {
	}

	public BufferedImage toBufferedImage(BitMatrix matrix) {
		int width = matrix.getWidth();
		int height = matrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, (matrix.get(x, y) ? BLACK : WHITE));
			}
		}
		return image;
	}

	public void writeToFile(BitMatrix matrix, String format, File file, String logUri) throws IOException {
		log.debug("write to file");
		BufferedImage image = toBufferedImage(matrix);

		if (!StringUtils.isEmpty(logUri)) {
			//设置logo图标
			QRCodeFactory logoConfig = new QRCodeFactory();
			image = logoConfig.setMatrixLogo(image, logUri);
		}
		if (!ImageIO.write(image, format, file)) {
			log.debug("生成图片失败");
			throw new IOException("Could not write an image of format " + format + " to " + file);
		} else {
			log.debug("图片生成成功！");
		}
	}

	public void writeToStream(BitMatrix matrix, String format, OutputStream stream, String logUri)
		throws IOException {

		BufferedImage image = toBufferedImage(matrix);

		//设置logo图标
		QRCodeFactory logoConfig = new QRCodeFactory();
		image = logoConfig.setMatrixLogo(image, logUri);

		if (!ImageIO.write(image, format, stream)) {
			throw new IOException("Could not write an image of format " + format);
		}
	}

}
