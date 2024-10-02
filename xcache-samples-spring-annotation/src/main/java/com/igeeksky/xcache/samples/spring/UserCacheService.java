package com.igeeksky.xcache.samples.spring;

import com.igeeksky.xcache.samples.User;
import com.igeeksky.xcache.samples.UserDao;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * 用户缓存服务
 * <p>
 * Spring Cache 没有 {@code CacheableAll}, {@code CachePutAll}, {@code CacheEvictAll} 这三个注解. <p>
 * Xcache 完整实现了 Spring cache 接口，因此正常使用 Spring cache 注解即可，并无特别限制. <p>
 * Xcache 适配 Spring cache 的 cacheManager 名称为 springCacheManager ，如无其它 cacheManager，可以不指定.
 *
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/13
 */
@Service
@CacheConfig(cacheNames = "user", cacheManager = "springCacheManager")
public class UserCacheService {

    private final UserDao userDao;

    public UserCacheService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 获取单个用户信息
     *
     * @param id 用户ID
     */
    @Cacheable(key = "#id")
    public User getUser(Long id) {
        return userDao.findUser(id);
    }

    /**
     * 新增用户信息
     *
     * @param user 用户信息
     */
    @CachePut(key = "#result.id")
    public User saveUser(User user) {
        return userDao.save(user);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    @CachePut(key = "#result.id")
    public User updateUser(User user) {
        return userDao.update(user);
    }

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     */
    @CacheEvict(key = "#id")
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    /**
     * 清空数据
     */
    @CacheEvict(allEntries = true)
    public void clear() {
        userDao.clear();
    }

}
