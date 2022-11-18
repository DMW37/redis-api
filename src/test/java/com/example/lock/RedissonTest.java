package com.example.lock;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.CountDownLatch;

/**
 * @author: 邓明维
 * @date: 2022/11/18
 * @description: 类比秒杀100商品场景或者抢100张优惠券的场景，我们这里处理的是并发输出一百个数：0-99
 *  带锁不分段196ms
 *  分四段，带同一把锁，78ms
 */
public class RedissonTest {

    public static CountDownLatch latch = new CountDownLatch(5);
    public static RedissonClient redissonClient;
    public static RLock rLock1;
    public static volatile int a = 0;
    public static void main(String[] args) throws Exception {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://xjdmw:6379").setPassword("redis");
        redissonClient = Redisson.create(config);
        rLock1 = redissonClient.getLock("xx");
        for (int i = 0; i < 4; i++) {
            new Thread(new PrintTest()).start();
        }

    }

    private static class PrintTest implements Runnable {
        @Override
        public void run() {
            try {
                latch.wait();
            } catch (Exception e) {

            }

            long time = System.currentTimeMillis();
            rLock1.lock();
            print1();
            rLock1.unlock();
            long time2 = System.currentTimeMillis();
            System.out.println("time:" + (time2-time));
        }
    }
    public static void print1() {
        for (int i = 0; i < 100; i++) {
            if (a > 99) {
                break;
            }
            a++;
        }
    }

}
