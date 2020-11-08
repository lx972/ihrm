package cn.lx.ihrm.system.config;

import cn.lx.ihrm.system.util.ApplicationContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.util.Destroyable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * cn.lx.shiro.config
 *
 * @Author Administrator
 * @date 10:53
 */
@Slf4j
public class RedisCache<K, V> implements Cache<K, V> , Destroyable {

    private final String name;

    public RedisCache(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Cache name cannot be null.");
        } else {
            this.name = name;
        }
    }


    @Override
    public V get(K k) throws CacheException {
        RedisTemplate<K, V> redisTemplate = (RedisTemplate<K, V>) ApplicationContextUtils.getBean("redisTemplate");
        log.info("RedisCache:get");
        return (V) redisTemplate.opsForValue().get(name+":"+k);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        RedisTemplate<String, V> redisTemplate = (RedisTemplate<String, V>) ApplicationContextUtils.getBean("redisTemplate");;
        //redisTemplate.boundHashOps(name).expire(3, TimeUnit.SECONDS);
        //redisTemplate.boundHashOps(name).put(k, v);
        log.info("RedisCache:put");
        redisTemplate.opsForValue().set(name+":"+k,v,10,TimeUnit.MINUTES);
        return null;
    }

    @Override
    public V remove(K k) throws CacheException {
        RedisTemplate redisTemplate = (RedisTemplate<K, V>) ApplicationContextUtils.getBean("redisTemplate");;
        //redisTemplate.boundHashOps(name).delete(k);
        redisTemplate.delete(name+":"+k);
        return null;
    }

    @Override
    public void clear() throws CacheException {
        RedisTemplate redisTemplate = (RedisTemplate<K, V>) ApplicationContextUtils.getBean("redisTemplate");;
        //Set keys = redisTemplate.boundHashOps(name).keys();
        //redisTemplate.boundHashOps(name).delete(keys);

        redisTemplate.delete( keys());
    }

    @Override
    public int size() {
        //RedisTemplate redisTemplate = (RedisTemplate<K, V>) ApplicationContextUtils.getBean("redisTemplate");;
        //return redisTemplate.boundHashOps(name).size().intValue();
        return keys() .size();
    }

    @Override
    public Set<K> keys() {
        RedisTemplate redisTemplate = (RedisTemplate<K, V>) ApplicationContextUtils.getBean("redisTemplate");;
        //这种方式比直接使用keys方法效率好
        Set<K> keys = (Set<K>) redisTemplate.execute(new RedisCallback<Set<K>>() {
            /**
             * Gets called by {@link RedisTemplate} with an active Redis connection. Does not need to care about activating or
             * closing the connection or handling exceptions.
             *
             * @param connection active Redis connection
             * @return a result object or {@code null} if none
             * @throws DataAccessException
             */
            @Override
            public Set<K> doInRedis(RedisConnection connection) throws DataAccessException {
                Set<K> keys = new HashSet<K>();
                // 放在try中自动释放cursor
                Cursor<byte[]> scan = connection.scan(new ScanOptions.ScanOptionsBuilder().match(name + "*").build());
                while (scan.hasNext()) {
                    String key = new String(scan.next(), Charset.defaultCharset());
                    keys.add((K)key);
                }
                return keys;
            }
        });
        return !keys.isEmpty() ? Collections.unmodifiableSet(keys) : (Set<K>) Collections.emptySet();
    }

    @Override
    public Collection<V> values() {
        RedisTemplate redisTemplate = (RedisTemplate<K, V>) ApplicationContextUtils.getBean("redisTemplate");;
        //List values = redisTemplate.boundHashOps(name).values();
        List values = redisTemplate.opsForValue().multiGet(keys());
        return (Collection)(!values.isEmpty() ? Collections.unmodifiableCollection(values) : Collections.emptyList());

    }

    /**
     * 清空redis
     * @throws Exception
     */
    @Override
    public void destroy() throws Exception {
        clear();
    }
}
