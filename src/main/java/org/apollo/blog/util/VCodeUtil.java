package org.apollo.blog.util;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

@Slf4j
public class VCodeUtil {
    /**
     * 生成随机码值，包含数字、大小写字母
     *
     * @param number 生成的随机码位数
     */
    public static String getRandomCode(int number) {
        String codeNum = "";
        int[] code = new int[3];
        Random random = new Random();
        for (int i = 0; i < number; i++) {
            int num = random.nextInt(10) + 48;
            int uppercase = random.nextInt(26) + 65;
            int lowercase = random.nextInt(26) + 97;
            code[0] = num;
            code[1] = uppercase;
            code[2] = lowercase;
            codeNum += (char) code[random.nextInt(3)];
        }
        log.info("随机码值:" + codeNum);
        return codeNum;
    }

}
