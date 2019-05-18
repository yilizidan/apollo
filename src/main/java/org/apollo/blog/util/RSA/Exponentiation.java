package org.apollo.blog.util.RSA;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * 主要用于计算超大整数超大次幂然后对超大的整数取模。
 */
@Component
@Slf4j
public class Exponentiation {
    /**
     * 超大整数超大次幂然后对超大的整数取模
     (base ^ exponent) mod n
     */
    public BigInteger expMode(BigInteger base, BigInteger exponent, BigInteger n){
        char[] binaryArray = new StringBuilder(exponent.toString(2)).reverse().toString().toCharArray() ;
        int r = binaryArray.length ;
        List<BigInteger> baseArray = new ArrayList<BigInteger>() ;

        BigInteger preBase = base ;
        baseArray.add(preBase);
        for(int i = 0 ; i < r - 1 ; i ++){
            BigInteger nextBase = preBase.multiply(preBase).mod(n) ;
            baseArray.add(nextBase) ;
            preBase = nextBase ;
        }
        BigInteger awb = this.multi(baseArray.toArray(new BigInteger[baseArray.size()]), binaryArray) ;
        return awb.mod(n) ;
    }


    private BigInteger multi(BigInteger[] array, char[] binArray){
        BigInteger result = BigInteger.ONE ;
        for(int index = 0 ; index < array.length ; index ++){
            BigInteger a = array[index] ;
            if(binArray[index] == '0'){
                continue ;
            }
            result = result.multiply(a) ;
        }
        return result ;
    }
}
