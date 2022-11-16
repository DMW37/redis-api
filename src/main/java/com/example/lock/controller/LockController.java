package com.example.lock.controller;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: 邓明维
 * @date: 2022/11/16
 * @description:
 */
@RestController
@RequestMapping
public class LockController {

    @Autowired
    private Redisson redisson;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/lock")
    public String lock() {
        String lockKey = "productKey001";
       // String clientId = UUID.randomUUID().toString();
        RLock redissonLock = redisson.getLock(lockKey);
        try {
           /* Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(lockKey, clientId, 30, TimeUnit.SECONDS);
            if (!flag) {
                return "error";
            }*/
            synchronized (this.getClass()) {
                redissonLock.lock();
                int stock = Integer.parseInt(stringRedisTemplate.opsForValue().get("stock"));
                if (stock > 0) {
                    int realStock = stock - 1;
                    stringRedisTemplate.opsForValue().set("stock", realStock + "");
                    System.out.println("减库存成功，剩余" + realStock);
                } else {
                    System.out.println("扣减失败，库存不足");
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } finally {
            redissonLock.unlock();
          /*  if (clientId.equals(stringRedisTemplate.opsForValue().get(lockKey))){
                stringRedisTemplate.delete(lockKey);
            }*/
        }
        return "end";
    }
}
