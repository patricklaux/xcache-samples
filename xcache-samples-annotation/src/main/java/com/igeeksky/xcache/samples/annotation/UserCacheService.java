package com.igeeksky.xcache.samples.annotation;

import com.igeeksky.xcache.annotation.*;
import com.igeeksky.xcache.samples.User;
import com.igeeksky.xcache.samples.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户缓存服务
 *
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/13
 */
@Service
@CacheConfig(name = "user", keyType = Long.class, valueType = User.class)
public class UserCacheService {

    private final static Logger log = LoggerFactory.getLogger(UserCacheService.class);

    private final UserDao userDao;

    public UserCacheService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 根据用户ID获取单个用户信息
     * <p>
     * 使用 {@link Cacheable} 注解，对应 V value = cache.get(K key) 方法。
     * 缓存键：默认采用方法的第一个参数，因为缓存值存在时，此注解将不执行方法，因此键不能从方法返回值中获取。
     * 缓存值：方法返回值。<p>
     * 特别注意：方法返回值类型 与 缓存值类型 必须一致，否则将出现类型转换异常。
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Cacheable
    public User getUser(Long id) {
        return userDao.findUser(id);
    }

    /**
     * 根据用户ID批量获取用户信息
     * <p>
     * 使用 {@link CacheableAll} 注解，对应 Map results = cache.getAll(Set) 方法。<p>
     * 缓存的键集：Set 类型，默认采用方法的第一个参数；<p>
     * 缓存结果集：Map 类型，因此，方法返回值同样必须为 Map 类型。
     *
     * @param ids 用户ID集合
     * @return 用户信息集合
     */
    @CacheableAll
    public Map<Long, User> getUsers(Set<Long> ids) {
        log.info("getUsers: {}", ids);
        return userDao.findUserList(ids);
    }

    /**
     * 新增用户
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
    @CachePut(key = "#user.id")
    public User updateUser(User user) {
        return userDao.update(user);
    }

    /**
     * 批量更新用户信息
     *
     * @param users 用户信息集合
     */
    @CachePutAll
    public Map<Long, User> updateUsers(List<User> users) {
        return userDao.batchUpdate(users);
    }

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     */
    @CacheEvict
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    /**
     * 批量删除用户信息
     *
     * @param ids 用户ID集合
     */
    @CacheEvictAll
    public void deleteUsers(Set<Long> ids) {
        userDao.batchDelete(ids);
    }

    /**
     * 清空数据
     */
    @CacheClear
    public void clear() {
        userDao.clear();
    }

}
