package org.apollo.blog.util;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 进制操作
 */
//@RunWith(SpringRunner.class)
@SpringBootTest
public class BinaryUtil {

    @Test
    public void Test() {
        try {
            System.out.println(allToBinary(2.01));
            System.out.println(allToBinary(23));
        } catch (Exception e) {
        }

        System.out.println(binaryToDecimal(4 >> 4));

        System.out.println(binaryToDecimal(-4 >>> 4));
        System.out.println(binaryToDecimal(-4 >>> 4));

        System.out.println(toBinary(23));
        System.out.println(toBinary(-23));

        System.out.println(binaryToDecimal(23));
        System.out.println(binaryToDecimal(-23));

        System.out.println("Integer.SIZE=" + Integer.SIZE);
        System.out.println(Integer.SIZE - Integer.numberOfLeadingZeros(23));
    }

    private String toBinary(int num) {
        return Integer.toBinaryString(num);
    }

    /**
     * 将最高位的数移至最低位（移31位），除过最低位其余位置清零，使用& 操作，可以使用和1相与（&），由于1在内存中
     * 除过最低位是1，其余31位都是零，然后把这个数按十进制输出；再移次高位，做相同的操作，直到最后一位
     */
    public String binaryToDecimal(int n) {
        StringBuilder builder = new StringBuilder();
        for (int i = 31; i >= 0; i--) {
            builder.append(n >>> i & 1);
        }
        return builder.toString();
    }

    public StringBuilder allToBinary(double deci) throws Exception {
        //取整数部分
        int in = (int) deci;
        //小数部分
        double d = deci - in;
        StringBuilder total = new StringBuilder();
        total.append(intToBinary(in));
        if (d > 0) {
            total.append(".");
            total.append(doubleToBinary(d));
        }
        return total;
    }

    public StringBuilder doubleToBinary(double d) throws Exception {
        //利用方法重装实现默认参数
        return doubleToBinary(d, 4);
    }

    public StringBuilder doubleToBinary(double d, int count) throws Exception {
        if (count > 32) {
            throw new Exception("The max bit must less than 32!");
        }
        if (count == 0) {
            throw new Exception("The min bit must bigger than 0");
        }
        //每次的乘积
        double multi = 0;
        StringBuilder res = new StringBuilder();
        while (count >= 0) {
            multi = d * 2;
            if (multi >= 1) {
                res.append(1);
                d = multi - 1;
            } else {
                res.append(0);
                d = multi;
            }
            count--;
        }
        return res;
    }

    public StringBuilder intToBinary(int in) throws Exception {
        StringBuilder binary = new StringBuilder();
        while (in != 0) {
            //商
            int quotient = in / 2;
            //余数
            int remender = in % 2;
            binary.append(remender);
            in = quotient;
        }
        return binary.reverse();
    }
}
