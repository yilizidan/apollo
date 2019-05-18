package org.apollo.blog.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat yearsdf = new SimpleDateFormat("yyyy");

    public static String strEmpty(String v) {
        return v == null ? "" : v;
    }

    public static boolean strIsEmpty(String v) {
        return (v == null || v.length() == 0) ? true : false;
    }

    /**
     * 汉字转换位汉语拼音首字母，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToFirstSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer("");
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (isChineseText(nameChar[i])) {
                try {
                    pinyinName.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0].charAt(0));
                } catch (Exception e) {
                    return "";
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        return pinyinName.toString();
    }

    /**
     * 汉字转换位汉语拼音，英文字符不变
     *
     * @param chines 汉字
     * @return 拼音
     */
    public static String converterToSpell(String chines) {
        StringBuffer pinyinName = new StringBuffer("");
        char[] nameChar = chines.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < nameChar.length; i++) {
            if (isChineseText(nameChar[i])) {
                try {
                    pinyinName.append(PinyinHelper.toHanyuPinyinStringArray(nameChar[i], defaultFormat)[0]);
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                    return "";
                }
            } else {
                pinyinName.append(nameChar[i]);
            }
        }
        return pinyinName.toString();
    }

    /**
     * 校验一个字符是否是汉字
     *
     * @param c 被校验的字符
     * @return true代表是汉字
     */
    private static boolean isChineseText(char c) {
        if ((c >= 0x4e00) && (c <= 0x9fbb)) {
            return true;
        }
        return false;
    }

    /**
     * 返回文本字符串
     *
     * @param inputString html字符串
     */
    public static String html2Text(String inputString) throws Exception {
        if (strIsEmpty(inputString)) {
            return null;
        }
        //含html标签的字符串
        String htmlStr = inputString;
        String textStr = "";
        Pattern p_script = null;
        Matcher m_script = null;
        Pattern p_style = null;
        Matcher m_style = null;
        Pattern p_html = null;
        Matcher m_html = null;

        //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
        String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
        //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
        //定义HTML标签的正则表达式
        String regEx_html = "<[^>]+>";

        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        m_script = p_script.matcher(htmlStr);
        //过滤script标签
        htmlStr = m_script.replaceAll("");

        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        m_style = p_style.matcher(htmlStr);
        //过滤style标签
        htmlStr = m_style.replaceAll("");

        p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        m_html = p_html.matcher(htmlStr);
        //过滤html标签
        htmlStr = m_html.replaceAll("");
        htmlStr = htmlStr.replace("  ", "");
        htmlStr = htmlStr.replace("\n", "");
        htmlStr = htmlStr.replace("\t", "");
        textStr = htmlStr.trim();
        //返回文本字符串
        return textStr;
    }

    /**
     * 检查字符串是否不是<code>null</code>和空字符串<code>""</code>。
     *
     * @param str 要检查的字符串
     * @return 如果不为空, 则返回<code>true</code>
     */
    public static boolean isNotEmpty(String str) {
        return ((str != null) && (str.length() > 0));
    }

    /**
     * 比较两个字符串（大小写敏感）。   *
     *
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }
        return str1.equals(str2);
    }

    public static boolean equalsByLong(Long value, Long value1) {
        if (value == null) {
            return value1 == null;
        }
        return value.equals(value1);
    }

    /**
     * 将字符串中所有指定的字符，替换成另一个。
     *
     * @param str        要扫描的字符串
     * @param searchstr  要搜索的字符
     * @param replacestr 替换字符
     * @return 被替换后的字符串，如果原始字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String replace(String str, String searchstr, String replacestr) {
        if (str == null) {
            return null;
        }
        return str.replace(searchstr, replacestr);
    }

    /**
     * 判断字符串是否只包含数字。
     *
     * @param str 要检查的字符串
     * @return 如果字符串非<code>null</code>并且全由unicode数字组成，则返回<code>true</code>
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /* ============================================================================ */
    /* 字符串分割函数。 */
    /*                                                                              */
    /* 将字符串按指定分隔符分割。 */
    /* ============================================================================ */

    /**
     * 1. 将字符串按空白字符分割。
     * 2. 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p>
     * StringUtil.split(null)       = null
     * StringUtil.split("")         = []
     * StringUtil.split("abc def")  = ["abc", "def"]
     * StringUtil.split("abc  def") = ["abc", "def"]
     * StringUtil.split(" abc ")    = ["abc"]
     *
     * @param str 要分割的字符串
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * 1. 将字符串按指定字符分割。
     * 2. 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p>
     * StringUtil.split(null, *)         = null
     * StringUtil.split("", *)           = []
     * StringUtil.split("a.b.c", '.')    = ["a", "b", "c"]
     * StringUtil.split("a..b.c", '.')   = ["a", "b", "c"]
     * StringUtil.split("a:b:c", '.')    = ["a:b:c"]
     * StringUtil.split("a b c", ' ')    = ["a", "b", "c"]
     *
     * @param str           要分割的字符串
     * @param separatorChar 分隔符
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, char separatorChar) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return new String[0];
        }

        List list = new ArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;

        while (i < length) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }

                start = ++i;
                continue;
            }

            match = true;
            i++;
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 1. 将字符串按指定字符分割。
     * 2. 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p>
     * StringUtil.split(null, *)                = null
     * StringUtil.split("", *)                  = []
     * StringUtil.split("abc def", null)        = ["abc", "def"]
     * StringUtil.split("abc def", " ")         = ["abc", "def"]
     * StringUtil.split("abc  def", " ")        = ["abc", "def"]
     * StringUtil.split(" ab:  cd::ef  ", ":")  = ["ab", "cd", "ef"]
     * StringUtil.split("abc.def", "")          = ["abc.def"]
     *
     * @param str            要分割的字符串
     * @param separatorChars 分隔符
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    /**
     * 1. 将字符串按指定字符分割。
     * 2. 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * <p>
     * StringUtil.split(null, *, *)                 = null
     * StringUtil.split("", *, *)                   = []
     * StringUtil.split("ab cd ef", null, 0)        = ["ab", "cd", "ef"]
     * StringUtil.split("  ab   cd ef  ", null, 0)  = ["ab", "cd", "ef"]
     * StringUtil.split("ab:cd::ef", ":", 0)        = ["ab", "cd", "ef"]
     * StringUtil.split("ab:cd:ef", ":", 2)         = ["ab", "cdef"]
     * StringUtil.split("abc.def", "", 2)           = ["abc.def"]
     *
     * @param str            要分割的字符串
     * @param separatorChars 分隔符
     * @param max            返回的数组的最大个数，如果小于等于0，则表示无限制
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, String separatorChars, int max) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        if (length == 0) {
            return new String[0];
        }
        List list = new ArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;
        if (separatorChars == null) {
            // null表示使用空白作为分隔符
            while (i < length) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // 优化分隔符长度为1的情形
            char sep = separatorChars.charAt(0);
            while (i < length) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        } else {
            // 一般情形
            while (i < length) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                match = true;
                i++;
            }
        }
        if (match) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(new String[list.size()]);
    }

    /**
     * 检查字符串中是否包含指定的字符串。如果字符串为<code>null</code>，将返回<code>false</code>。
     * <p>
     * StringUtil.contains(null, *)     = false
     * StringUtil.contains(*, null)     = false
     * StringUtil.contains(";", ";")      = true
     * StringUtil.contains("abc", "")   = true
     * StringUtil.contains("abc", "a")  = true
     * StringUtil.contains("abc", "z")  = false
     *
     * @param str       要扫描的字符串
     * @param searchStr 要查找的字符串
     * @return 如果找到，则返回<code>true</code>
     */
    public static boolean contains(String str, String searchStr) {
        if ((str == null) || (searchStr == null)) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }
    
    /* ============================================================================ */
    /* 时间操作函数。 */
    /* ============================================================================ */

    /**
     * 获取两个日期相差的月数
     *
     * @param d1 较大的日期
     * @param d2 较小的日期
     * @return 如果d1>d2返回 月数差 否则返回0
     */
    public static int getMonthDiff(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);
        if (c1.getTimeInMillis() < c2.getTimeInMillis()) {
            return 0;
        }
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c2.get(Calendar.DAY_OF_MONTH);
        // 获取年的差值 假设 d1 = 2015-8-16  d2 = 2011-9-30
        int yearInterval = year1 - year2;
        // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
        if (month1 < month2 || month1 == month2 && day1 < day2) {
            yearInterval--;
        }
        // 获取月数差值
        int monthInterval = (month1 + 12) - month2;
        if (day1 < day2) {
            monthInterval--;
        }
        monthInterval %= 12;
        return yearInterval * 12 + monthInterval;
    }

    /**
     * 将double格式化为指定小数位的String，不足小数位用0补全
     *
     * @param v     需要格式化的数字
     * @param scale 小数点后保留几位
     */
    public static String roundByScale(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        if (scale == 0) {
            return new DecimalFormat("0").format(v);
        }
        String formatStr = "0.";
        for (int i = 0; i < scale; i++) {
            formatStr = formatStr + "0";
        }
        return new DecimalFormat(formatStr).format(v);
    }

    /**
     * 日期转星期
     * return 1-7 周一 - 周日
     */
    public static int dateToWeek(String datetime) {
        // 获得一个日历
        Calendar cal = Calendar.getInstance();
        Date datet = null;
        try {
            datet = sdf.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 指示一个星期中的某天。
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return w;
    }


    /**
     * 查询日期间有几天一周中的某一天 (不支持结束日期早于起始日期、周几输入错误等)
     * 日期格式 yyyy-MM-dd yyyy-MM-dd 1-7(表示周一到周日)
     *
     * @param startDay  准备查询的起始日期
     * @param endDay    准备查询的结束日期
     * @param dayOfWeek 准备查的一周中的某一天(准备查周几？)
     * @return 包含所查周几的天数
     */
    public static int getMondayNumber(String startDay, String endDay, int dayOfWeek) throws ParseException {
        int differenceDay = 0;
        //转换起始日期
        Date startDate = sdf.parse(startDay);
        //转换结束日期
        Date endDate = sdf.parse(endDay);

        if (startDate.getTime() > endDate.getTime()) {
            return 0;
        }

        String startyear = yearsdf.format(startDate);
        String endyear = yearsdf.format(endDate);
        if (!equals(startyear, endyear)) {
            endDate = sdf.parse(startyear + "-12-31");
            differenceDay += getMondayNumber(endyear + "-01-01", endDay, dayOfWeek);
        }

        //实例化起始和结束Calendar对象
        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();
        startCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        endCalendar.setFirstDayOfWeek(Calendar.MONDAY);
        //分别设置Calendar对象的时间
        startCalendar.setTime(startDate);
        endCalendar.setTime(endDate);

        //拿到起始日期是星期几
        int startDayOfWeek = startCalendar.get(Calendar.DAY_OF_WEEK);
        if (startDayOfWeek == 1) {
            startDayOfWeek = 7;
        } else {
            startDayOfWeek--;
        }

        //拿到结束日期是星期几
        int endDayOfWeek = endCalendar.get(Calendar.DAY_OF_WEEK);
        if (endDayOfWeek == 1) {
            endDayOfWeek = 7;
        } else {
            endDayOfWeek--;
        }

        //计算相差的周数
        int differenceWeek = weeksBetween(startDate, endDate);

        //开始计算
        if (startDayOfWeek <= dayOfWeek) {
            if (endDayOfWeek >= dayOfWeek) {
                differenceDay = differenceWeek + 1;
            } else {
                differenceDay = differenceWeek;
            }
        } else if (startDayOfWeek > dayOfWeek) {
            if (endDayOfWeek < dayOfWeek) {
                differenceDay = differenceWeek - 1;
            } else {
                differenceDay = differenceWeek;
            }
        } else {
            differenceDay = differenceWeek;
        }
        return differenceDay;
    }

    private static Integer getWeekOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);
        int mouth = calendar.get(Calendar.MONTH);
        // JDK think 2015-12-31 as 2016 1th week
        //如果月份是12月，且求出来的周数是第一周，说明该日期实质上是这一年的第53周，也是下一年的第一周
        if (mouth >= 11 && week <= 1) {
            week += 52;
        }
        return week;
    }

    private static Integer getYearOfDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    private static Integer weeksBetween(Date fromDate, Date toDate) {
        if (fromDate.before(toDate)) {
            Date temp = fromDate;
            fromDate = toDate;
            toDate = temp;
        }
        Integer weekNum = (getWeekOfYear(fromDate) - getWeekOfYear(toDate))
                + (getYearOfDate(fromDate) - getYearOfDate(toDate)) * 52;
        return ++weekNum;
    }


    /**
     * 获取两个日期之间的所有日期（yyyy-MM-dd） 指定获取周几
     *
     * @param begin     开始时间
     * @param end       结束时间
     * @param dayOfWeek 获取周几
     */
    public static List<Date> getBetweenDates(String begin, String end, int dayOfWeek) throws Exception {
        List<Date> result = new ArrayList<>();
        Date start = sdf.parse(begin);
        Date finish = sdf.parse(end);
        Calendar tempStart = Calendar.getInstance();
        tempStart.setTime(start);
        while (start.getTime() <= finish.getTime()) {
            String time = sdf.format(tempStart.getTime());
            if (dateToWeek(time) == (dayOfWeek == 7 ? 0 : dayOfWeek)) {
                result.add(tempStart.getTime());
            }
            tempStart.add(Calendar.DAY_OF_YEAR, 1);
            start = tempStart.getTime();
        }
        return result;
    }
}
