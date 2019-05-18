package org.apollo.blog.util;

import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.MultimediaInfo;
import ws.schild.jave.MultimediaObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author xiongfan
 * @description 文件处理工具
 * @date 2018/9/18 17:11:18
 */
@Slf4j
@Component
public class FileCommonUtil {

	/**
	 * 功能描述:视频解析秒数
	 */
	public long audioParseSecond(File source) {
		long second = 0;
		try {
			MultimediaObject instance = new MultimediaObject(source);
			MultimediaInfo result = instance.getInfo();
			second = result.getDuration();
			log.debug("此视频时长为:" + second + "秒！");
		} catch (Exception e) {
			log.debug("视频解析错误");
		}
		return second;
	}

	/**
	 * 功能描述:MultipartFile 转 File
	 */
	public File multipartFileToTemp(MultipartFile file) {
		File dfile = null;
		try {
			dfile = File.createTempFile("prefix", "_" + file.getOriginalFilename());
			file.transferTo(dfile);
		} catch (IOException e) {
			log.debug("IO异常");
			return null;
		}
		return dfile;
	}

	/**
	 * 功能描述:视频进度计算
	 *
	 * @param endTime  结束时长
	 * @param duration 总时长
	 */
	public Integer getinitReadComplete(Long endTime, Long duration) {
		if (endTime == null || duration == null) {
			return 0;
		}
		if (0 == endTime || 0 == duration) {
			return 0;
		}
		if (endTime > duration) {
			return 100;
		}
		return (int) ((endTime * 100 / duration));
	}

	public void porxyUrl(String url, FileOutputStream fileOutputStream) {
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
			throw new ApiException("下载图片失败");
		} catch (IOException e) {
			log.error("porxyheadimg IOException", e);
			throw new ApiException("下载图片失败");
		}
		if (httpUrl == null) {
			return;
		}
		try (FileOutputStream os = fileOutputStream;
		     InputStream stm = httpUrl.getInputStream();
		     BufferedInputStream bis = new BufferedInputStream(stm)) {
			while ((size = bis.read(buf)) != -1) {
				os.write(buf, 0, size);
			}
			os.flush();
			httpUrl.disconnect();
			bis.close();
			stm.close();
			os.close();
		} catch (Exception e) {
			log.error("下载图片失败", e);
			throw new ApiException("下载图片失败");
		}
	}


	/**
	 * 迭代删除文件夹
	 *
	 * @param dirPath 文件夹路径
	 */
	public void deleteDir(String dirPath) {
		File file = new File(dirPath);
		if (file.isFile()) {
			file.delete();
		} else {
			File[] files = file.listFiles();
			if (files == null) {
				file.delete();
			} else {
				for (int i = 0; i < files.length; i++) {
					deleteDir(files[i].getAbsolutePath());
				}
				file.delete();
			}
		}
	}
}