package cn.lx.ihrm.common.shiro;

import lombok.Setter;
import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.springframework.stereotype.Component;


/**
 * cn.lx.shiro.config
 *
 * @Author Administrator
 * @date 11:27
 */
@Setter
@Component
public class RedisCacheManager extends AbstractCacheManager {

    /**
     * 仿写{@link MemoryConstrainedCacheManager}
     * 创建一个redis缓存，然后缓存实现增删改查
     *
     * @param name
     * @return
     * @throws CacheException
     */
    @Override
    protected Cache createCache(String name) throws CacheException {
        return new RedisCache(name);
    }

}
