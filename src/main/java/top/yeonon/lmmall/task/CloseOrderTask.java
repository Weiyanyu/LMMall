package top.yeonon.lmmall.task;

import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.yeonon.lmmall.service.IOrderService;

import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

/**
 * @Author yeonon
 * @date 2018/4/17 0017 15:06
 **/
@Component
@Log
public class CloseOrderTask {

    @Autowired
    private IOrderService orderService;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Scheduled(cron = "0/5 * * * * ? ")
    public void closeOrderV2() {
        int timeOut = 50000;
        Boolean isSuccess = redisTemplate.opsForValue().setIfAbsent("CLOSE_ORDER_LOCK", String.valueOf(System.currentTimeMillis()) + timeOut);
        if (isSuccess != null && isSuccess) {
            log.info("获取锁成功,开始执行任务");
            closeOrder(2);
        } else {
            //并不直接放弃，先获得设置锁的时候放入的时间戳
            String lockStr = (String) redisTemplate.opsForValue().get("CLOSE_ORDER_LOCK");
            //如果这个时间戳不为null且当前时间已经大于了原来的时间，此时就表示可以去抢夺资源了
            //这也就防止了某个资源被同一个进程持续持有
            if (lockStr != null && System.currentTimeMillis() > Long.parseLong(lockStr)) {

                //使用getSet方法来设置新值并返回旧值，如果旧值为null，表示可以获取锁了
                //如果不为null，但是旧值和lockStr相同，就说明期间没有其他线程来设置值，即没有其他线程抢先一步获取锁
                //那么，当前线程就有权利获取锁，这又防止了死锁
                String getSetResult = (String) redisTemplate.opsForValue().getAndSet("CLOSE_ORDER_LOCK",
                        String.valueOf(System.currentTimeMillis()) + timeOut);
                if (getSetResult == null || getSetResult.equals(lockStr)) {
                    closeOrder(2);
                } else {
                    log.info("获取锁失败，放弃任务");
                }
            } else {
                log.info("获取锁失败，放弃任务");
            }
        }
    }

    private void closeOrder(int hour) {
        redisTemplate.expire("CLOSE_ORDER_LOCK", 50000, TimeUnit.MILLISECONDS);
        orderService.closeOrder(hour);
        redisTemplate.delete("CLOSE_ORDER_LOCK");
        log.info(Thread.currentThread().getName() + "释放锁");
    }

}