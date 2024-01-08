package org.microboot.cache.impl.redis;

import org.microboot.cache.impl.AbstractCache;
import org.microboot.cache.utils.KeyUtils;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author 胡鹏
 */
public class RedisImpl extends AbstractCache {

    //缓存时间
    private final int expire;
    //缓存时间是否动态生成（true：缓存时间则是expire作为种子乘以一个随机数，false：缓存时间则是expire，默认false）
    private final boolean isDynamic;
    //redisTemplate
    private final RedisTemplate redisTemplate;

    public RedisImpl(String name, int expire, boolean isDynamic, RedisTemplate redisTemplate) {
        this.name = name;
        this.expire = expire;
        this.isDynamic = isDynamic;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object getNativeCache() {
        return this.redisTemplate;
    }

    @Override
    public void clear() {
        /*
            1、不建议使用keys(*) + delete(keys)的方式清除，需要两次I/O才能完成，且来回传输大量key
            2、spring redisTemplate对redis常规操作做了一些封装，但没有封装flushall和flushdb等命令对应的方法
               这时需要拿到connection执行一些特殊的Commands
               不建议使用redisTemplate.getConnectionFactory().getConnection()
               因为获取pool中的redisConnection后，如果忘记释放连接会造成redis连接池中的链接被租赁后不会被释放或者退还到链接池中
               虽然业务已处理完毕，redisConnection已经空闲，但是pool中的redisConnection的状态还没有回到idle状态
            3、推荐使用this.redisTemplate.execute(RedisCallback action)，最后会在finally中释放连接
               即：RedisConnectionUtils.releaseConnection(conn, factory, enableTransactionSupport)
               并且执行flushdb只产生一次I/O传输
         */
        this.redisTemplate.execute((RedisConnection redisConnection) -> {
            redisConnection.flushDb();
            return null;
        });
    }

    @Override
    public void evict(Object key) {
        if (key == null) {
            return;
        }
        String newKey = KeyUtils.newKey(this.name, key);
        this.redisTemplate.delete(newKey);
    }

    @Override
    protected Object getValue(Object key) {
        String newKey = KeyUtils.newKey(this.name, key);
        return this.redisTemplate.opsForValue().get(newKey);
    }

    @Override
    protected void setValue(Object key, Object value) {
        String newKey = KeyUtils.newKey(this.name, key);
        if (expire <= 0) {
            //如果expire小于等于0，则不设置过期时间，即：永不失效
            this.redisTemplate.opsForValue().set(newKey, value);
        } else {
            //ThreadLocalRandom.current()比new Random()获取随机数更高效（随机区间：1 ~ expire）
            this.redisTemplate.opsForValue().set(newKey, value, isDynamic ? ThreadLocalRandom.current().nextInt(1, expire) : expire, TimeUnit.SECONDS);
        }
    }
}
