package org.apollo.blog.util;


import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author xiongfan
 * @description 时间转换工具
 * @date 2018/9/1 17:48:01
 */
public class LocalDateTimeUtil {

	public static final String DATETIME_FORMAT = "yyyy/MM/dd HH:mm:ss";
	public static final String DATE_FORMAT = "yyyy/MM/dd";
	public static final String DATETIME_FORMAT_MINUTS = "yyyy/MM/dd HH:mm";
	public static final String DATE_FORMAT_STRING = "yyyyMMdd";

	/**
	 * 功能描述: LocalDateTime转String
	 */
	public static String getDateTimeAsString(LocalDateTime localDateTime, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return localDateTime.format(formatter);
	}

	/**
	 * 功能描述: LocalDate转String
	 */
	public static String getDateAsString(LocalDate localDate, String format) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
		return localDate.format(formatter);
	}

	/**
	 * 功能描述: LocalDateTime转时间戳（毫秒）
	 */
	public static long getTimestampOfDateTime(LocalDateTime localDateTime) {
		ZoneId zone = ZoneId.systemDefault();
		Instant instant = localDateTime.atZone(zone).toInstant();
		return instant.toEpochMilli();
	}


	/**
	 * 功能描述: 时间戳（毫秒）转LocalDateTime
	 */
	public static LocalDateTime getDateTimeOfTimestamp(long timestamp) {
		Instant instant = Instant.ofEpochMilli(timestamp);
		ZoneId zone = ZoneId.systemDefault();
		return LocalDateTime.ofInstant(instant, zone);
	}


	public static LocalDate getLocalDateOfTimestamp(long timestamp) {
		LocalDateTime time = getDateTimeOfTimestamp(timestamp);
		return LocalDate.of(time.getYear(), time.getMonth(), time.getDayOfMonth());
	}

	/**
	 * 功能描述: String转LocalDateTime
	 */
	public static LocalDateTime getStringToDateTime(String time, String format) {
		DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
		return LocalDateTime.parse(time, df);
	}


	/**
	 * 功能描述: 时间戳（毫秒）转字符串
	 */
	public static String getTimestampToString(long timestamp, String format) {
		// 1.时间戳（毫秒）先转LocalDateTime
		LocalDateTime temp = getDateTimeOfTimestamp(timestamp);
		// 2.LocalDateTime转String
		return getDateTimeAsString(temp, format);
	}

	/**
	 * 功能描述: 时间戳（毫秒）转字符串 默认 yyyy/MM/dd HH:mm:ss
	 */
	public static String getTimestampToString(long timestamp) {
		return getTimestampToString(timestamp, DATETIME_FORMAT);
	}

	/**
	 * 功能描述: String转时间戳（毫秒）
	 */
	public static long getStringToTimestamp(String time, String format) {
		// 1.String转LocalDateTime
		LocalDateTime temp = getStringToDateTime(time, format);
		// 2.LocalDateTime转时间戳（毫秒）
		return getTimestampOfDateTime(temp);
	}

	/**
	 * 获取昨天的Date(去掉时分秒，只有年月日)
	 */
	public static Date getYesterday() {
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDate ld = LocalDate.now();
		LocalDate yesterday = ld.plus(-1, ChronoUnit.DAYS);
		ZonedDateTime zdt = yesterday.atStartOfDay(zoneId);
		Date date = Date.from(zdt.toInstant());
		return date;

	}

	/**
	 * 功能描述:LocalDaTe转Date
	 */

	public static Date locaDateToDate(LocalDate localDate) {
		ZoneId zoneId = ZoneId.systemDefault();
		ZonedDateTime zdt = localDate.atStartOfDay(zoneId);
		return Date.from(zdt.toInstant());
	}

	/**
	 * 功能描述:LocalDaTe转时间戳
	 */
	public static Long locaDateToTimestamp(LocalDate localDate) {
		return locaDateToDate(localDate).getTime();
	}

	/**
	 * Date转换为LocalDateTime
	 */
	public static LocalDateTime convertDateToLDT(Date date) {
		return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
	}

	public static Date localDateTimeToDate(LocalDateTime ld) {
		return Date.from(ld.atZone(ZoneId.systemDefault()).toInstant());
	}


	/**
	 * @param
	 * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
	 * @author fy.zhang
	 */
	public static String formatDuring(long mss) {
		long days = mss / (1000 * 60 * 60 * 24);
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		return days + " days " + hours + " hours " + minutes + " minutes "
				+ seconds + " seconds ";
	}

	/**
	 * @param begin 时间段的开始
	 * @param end   时间段的结束
	 * @return 输入的两个Date类型数据之间的时间间格用* days * hours * minutes * seconds的格式展示
	 * @author fy.zhang
	 */
	public static String formatDuring(Date begin, Date end) {
		return formatDuring(end.getTime() - begin.getTime());
	}

	/**
	 * 小时转毫秒
	 */
	public static long hourToMillisecond(double hour) {
		return new BigDecimal(hour).multiply(new BigDecimal(3600000)).longValue();
	}

	/**
	 * 毫秒转小时
	 */
	public static double millisecondToHour(Long millisecond) {
		return (new BigDecimal(millisecond).divide(new BigDecimal(3600000), 2)).doubleValue();
	}

	/**
	 * 获取两个日期间隔的所有日期
	 */
	public static List<LocalDate> getBetweenDate(LocalDate startDate, LocalDate endDate) {
		AssertUtils.notNull(startDate, "getBetweenDate startDate is cannot null");
		AssertUtils.notNull(endDate, "getBetweenDate endDate is cannot null");
		List<LocalDate> list = new ArrayList<>();
		long distance = ChronoUnit.DAYS.between(startDate, endDate);
		if (distance < 1) {
			return list;
		}
		Stream.iterate(startDate, d -> {
			return d.plusDays(1);
		}).limit(distance + 1).forEach(f -> {
			list.add(f);
		});
		return list;
	}

	/**
	 * 将传入的时间戳转为当天开始的时间戳
	 */
	public static long dayBeginMillis(long millisecond) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(millisecond);
		ca.set(Calendar.HOUR_OF_DAY, 0);
		ca.set(Calendar.MINUTE, 0);
		ca.set(Calendar.SECOND, 0);
		ca.set(Calendar.MILLISECOND, 0);
		return ca.getTimeInMillis();

	}

	/**
	 * 将传入的时间戳转为当天开始的截止时间戳
	 */
	public static long dayEndMillis(long millisecond) {
		Calendar ca = Calendar.getInstance();
		ca.setTimeInMillis(millisecond);
		ca.set(Calendar.HOUR_OF_DAY, 23);
		ca.set(Calendar.MINUTE, 59);
		ca.set(Calendar.SECOND, 59);
		ca.set(Calendar.MILLISECOND, 999);
		return ca.getTimeInMillis();
	}


	/**
	 * 获得本周一与当前日期相差的天数
	 */
	public static int getMondayPlus() {
		Calendar cd = Calendar.getInstance();
		int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == 1) {
			return -6;
		} else {
			return 2 - dayOfWeek;
		}
	}

	/**
	 * 加天数后的日期
	 */
	public static Date plus(Date date, int day) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		cd.add(Calendar.DATE, day);
		return cd.getTime();
	}

	/**
	 * 获得当前周- 周一的日期
	 */
	public static Date getCurrentMonday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus);
		currentDate.set(Calendar.HOUR, 0);
		currentDate.set(Calendar.MINUTE, 0);
		currentDate.set(Calendar.SECOND, 0);
		currentDate.set(Calendar.MILLISECOND, 0);
		return currentDate.getTime();
	}


	/**
	 * 获得当前周- 周日  的日期
	 */
	public static Date getCurrentSunday() {
		int mondayPlus = getMondayPlus();
		GregorianCalendar currentDate = new GregorianCalendar();
		currentDate.add(GregorianCalendar.DATE, mondayPlus + 6);
		currentDate.set(Calendar.HOUR, 23);
		currentDate.set(Calendar.MINUTE, 59);
		currentDate.set(Calendar.SECOND, 59);
		currentDate.set(Calendar.MILLISECOND, 999);
		return currentDate.getTime();
	}

	/**
	 * 获取指定时间的指定格式
	 */
	public static String formatTime(LocalDateTime time, String pattern) {
		return time.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 获取当前时间的指定格式
	 */
	public static String formatNow(String pattern) {
		return formatTime(LocalDateTime.now(), pattern);
	}

	/**
	 * 日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
	 */
	public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
		return time.plus(number, field);
	}

	/**
	 * 日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
	 */
	public static LocalDateTime minu(LocalDateTime time, long number, TemporalUnit field) {
		return time.minus(number, field);
	}

	/**
	 * 获取两个日期的差  field参数为ChronoUnit.*
	 */
	public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
		Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
		if (field == ChronoUnit.YEARS) {
			return period.getYears();
		}
		if (field == ChronoUnit.MONTHS) {
			return period.getYears() * 12 + period.getMonths();
		}
		return field.between(startTime, endTime);
	}

	public static String getHourDay() {
		LocalDateTime now = LocalDateTime.now();
		String year = now.getYear() + "";
		String month = now.getMonth().getValue() > 9 ? String.valueOf(now.getMonth().getValue()) : "0" + now.getMonth().getValue();
		String day = now.getDayOfMonth() > 9 ? String.valueOf(now.getDayOfMonth()) : "0" + now.getDayOfMonth();
		String hour = now.getHour() > 9 ? String.valueOf(now.getHour()) : "0" + now.getHour();
		String minute = now.getMinute() > 9 ? String.valueOf(now.getMinute()) : "0" + now.getMinute();
		String second = now.getSecond() > 9 ? String.valueOf(now.getSecond()) : "0" + now.getSecond();
		return year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + second;
	}

	public static String getYear() {
		LocalDateTime now = LocalDateTime.now();
		return now.getYear() + "";
	}

	public static String getMonth() {
		LocalDateTime now = LocalDateTime.now();
		return now.getMonth().getValue() > 9 ? String.valueOf(now.getMonth().getValue()) : "0" + now.getMonth().getValue();
	}

	public static String getDay() {
		LocalDateTime now = LocalDateTime.now();
		return now.getDayOfMonth() > 9 ? String.valueOf(now.getDayOfMonth()) : "0" + now.getDayOfMonth();
	}

	public static String getHour() {
		LocalDateTime now = LocalDateTime.now();
		return now.getHour() > 9 ? String.valueOf(now.getHour()) : "0" + now.getHour();
	}

	public static String getMinute() {
		LocalDateTime now = LocalDateTime.now();
		return now.getMinute() > 9 ? String.valueOf(now.getMinute()) : "0" + now.getMinute();
	}

	public static String getSecond() {
		LocalDateTime now = LocalDateTime.now();
		return now.getSecond() > 9 ? String.valueOf(now.getSecond()) : "0" + now.getSecond();
	}
}