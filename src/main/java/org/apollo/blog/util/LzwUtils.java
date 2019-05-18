package org.apollo.blog.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
public class LzwUtils {

    /**
     * 压缩
     */
    public List<Integer> encode(String data) {
        List<Integer> result = new ArrayList<>();
        // 初始化Dictionary
        int idleCode = 256;
        HashMap<String, Integer> dic = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dic.put((char) i + "", i);
        }
        // 词组前缀
        String prefix = "";
        // 词组后缀
        String suffix = "";
        int i = 0;
        for (char c : data.toCharArray()) {
            suffix = prefix + c;
            if (dic.containsKey(suffix)) {
                prefix = suffix;
            } else {
                dic.put(suffix, idleCode++);
                result.add(dic.get(prefix));
                prefix = "" + c;
                i++;
            }
        }
        // 最后一次输出
        if (!prefix.equals("")) {
            result.add(dic.get(prefix));
        }
        return result;
    }

    /**
     * 解压
     */
    public String decode(List<Integer> array) {
        StringBuilder result = new StringBuilder();
        int deCode = 256;
        HashMap<Integer, String> dic = new HashMap<>();
        for (int i = 0; i < 256; i++) {
            dic.put(i, (char) i + "");
        }
        String p = "";
        String c = "";
        for (int code : array) {
            if (dic.containsKey(code)) {
                c = dic.get(code);
            } else if (code == deCode) {
                c = c + c.charAt(0);
            } else {
                log.info("bad Code!");
            }

            if (!p.equals("")) {
                dic.put(deCode++, p + c.charAt(0));
            }

            result.append(c);
            p = c;
        }
        return result.toString();
    }
}
