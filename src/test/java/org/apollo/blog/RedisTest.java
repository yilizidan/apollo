package org.apollo.blog;

import redis.clients.jedis.Jedis;

public class RedisTest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("Green521388");
        //查看服务是否运行
        System.out.println("服务正在运行: "+jedis.ping());
    }
}
