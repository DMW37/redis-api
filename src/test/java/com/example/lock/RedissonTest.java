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
        for (int i = 0; i < 40; i++) {
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

            if (Thread.currentThread().getName().contains("2")) {
                rLock1.lock();
                print2();
                rLock1.unlock();
            } else if (Thread.currentThread().getName().contains("3")) {
                rLock1.lock();
                print3();
                rLock1.unlock();
            } else if (Thread.currentThread().getName().contains("4")) {
                rLock1.lock();
                print4();
                rLock1.unlock();
            }else if(Thread.currentThread().getName().contains("1")){
                rLock1.lock();
                print1();
                rLock1.unlock();
            }
        }
    }
    public static void print1() {
        for (int i = 0; i < 25; i++) {
            if (a > 99) {
                break;
            }
            System.out.println(Thread.currentThread().getName() + ": i=" + (i));
            a++;
            long time = System.currentTimeMillis();
            System.out.println("time:" + time);
        }
    }

    public static void print2() {
        for (int i = 25; i < 50; i++) {
            if (a > 99) {
                break;
            }
            System.out.println(Thread.currentThread().getName() + ": i=" + (i));
            a++;
            long time = System.currentTimeMillis();
            System.out.println("time:" + time);

        }
    }

    public static void print3() {
        for (int i = 50; i < 75; i++) {
            if (a > 99) {
                break;
            }
            System.out.println(Thread.currentThread().getName() + ": i=" + (i));
            a++;
            long time = System.currentTimeMillis();
            System.out.println("time:" + time);

        }
    }
    public static void print4() {
        for (int i = 75; i < 100; i++) {
            if (a > 99) {
                break;
            }
            System.out.println(Thread.currentThread().getName() + ": i=" + (i));
            a++;
            long time = System.currentTimeMillis();
            System.out.println("time:" + time);
        }
    }

}
