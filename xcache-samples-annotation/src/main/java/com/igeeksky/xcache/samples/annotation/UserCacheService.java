package com.igeeksky.xcache.samples.annotation;

import com.igeeksky.xcache.annotation.*;
import com.igeeksky.xcache.samples.User;
import com.igeeksky.xcache.samples.UserDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 用户缓存服务
 *
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/13
 */
@Import({UserDao.class})
@Service
// @CacheConfig 统一配置缓存注解公共参数
@CacheConfig(name = "user", keyType = Long.class, valueType = User.class)
public class UserCacheService {

    private static final Logger log = LoggerFactory.getLogger(UserCacheService.class);

    private final UserDao userDao;

    public UserCacheService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * 获取单个用户信息
     * <p>
     * {@link Cacheable} 注解，对应 {@code V value = cache.get(K key, CacheLoader<K,V> loader) } 方法。
     * <p>
     * 如未配置 key 表达式，采用方法的第一个参数作为缓存键；如已配置 key 表达式，解析该表达式提取键.
     *
     * @param id 用户ID
     * @return {@code User} – 用户信息
     */
    @Cacheable
    public User getUser(Long id) {
        return userDao.findUser(id);
    }

    /**
     * 获取单个用户信息
     *
     * @param id 用户ID
     * @return {@code Optional<User>} – 用户信息 <br>
     * 如果检测到方法返回值类型为 {@code Optional}，缓存实现会自动采用 {@code Optional.ofNullable()} 包装返回值.
     */
    @Cacheable
    public Optional<User> getOptionalUser(Long id) {
        User user = userDao.findUser(id);

        // 错误：方法返回值为 Optional 类型时，当用户不存在，直接返回 null
        // return user == null ? null : Optional.of(user);

        // 正确：使用 Optional.ofNullable(value) 包装可能为空的值
        return Optional.ofNullable(user);
    }

    /**
     * 获取单个用户信息
     *
     * @param id 用户ID
     * @return {@code CompletableFuture<User>} – 用户信息 <br>
     * 如果检测到方法返回值类型为 {@code CompletableFuture}，缓存实现会自动采用 {@code CompletableFuture.completedFuture()} 包装返回值.
     */
    @Cacheable
    public CompletableFuture<User> getFutureUser(Long id) {
        User user = userDao.findUser(id);

        // 错误：方法返回值为 CompletableFuture 类型时，当用户不存在，直接返回 null
        // return user == null ? null : CompletableFuture.completedFuture(user);

        // 正确：使用 CompletableFuture.completedFuture 包装可能为空的值
        return CompletableFuture.completedFuture(user);
    }

    /**
     * 批量获取用户信息
     * <p>
     * {@link CacheableAll} 注解，对应 {@code Map<K,V> results = cache.getAll(Set<K> keys, CacheLoader<K,V> loader) }方法.<p>
     * 缓存的键集：Set 类型。如未配置 keys 表达式，采用方法的第一个参数作为键集；如已配置 keys 表达式，解析该表达式提取键集.<p>
     * 缓存结果集：Map 类型.
     *
     * @param ids 用户ID集合
     * @return {@code Map<Long, User>} – 用户信息集合
     */
    @CacheableAll
    public Map<Long, User> getUsers(Set<Long> ids) {
        log.debug("getUsers: {}", ids);
        return userDao.findUserList(ids);
    }

    /**
     * 批量获取用户信息
     *
     * @param ids 用户ID集合
     * @return {@code Optional<Map<Long, User>>} – 用户信息集合 <br>
     * 如果检测到方法返回值类型为 {@code Optional}，缓存实现会自动采用 {@code Optional.ofNullable()} 包装返回值.
     */
    @CacheableAll
    public Optional<Map<Long, User>> getOptionalUsers(Set<Long> ids) {
        log.debug("getOptionalUsers: {}", ids);
        return Optional.ofNullable(userDao.findUserList(ids));
    }

    /**
     * 批量获取用户信息
     *
     * @param ids 用户ID集合
     * @return {@code CompletableFuture<Map<Long, User>>} – 用户信息集合 <br>
     * 如果检测到方法返回值类型为 {@code CompletableFuture}，缓存实现会自动采用 {@code CompletableFuture.completedFuture()} 包装返回值.
     */
    @CacheableAll(keys = "#ids")
    public CompletableFuture<Map<Long, User>> getFutureUsers(Set<Long> ids) {
        log.debug("getFutureUsers: {}", ids);
        return CompletableFuture.completedFuture(userDao.findUserList(ids));
    }

    /**
     * 新增用户信息
     * <p>
     * {@link CachePut} 注解，对应 {@code cache.put(K key, V value)} 方法.<p>
     * 如未配置 key 表达式，采用方法的第一个参数作为缓存键；如已配置 key 表达式，解析该表达式提取键.<p>
     * 如未配置 value 表达式，采用方法返回结果作为缓存值；如已配置 value 表达式，解析该表达式提取值.
     *
     * @param user 用户信息（无ID）
     * @return {@code User} – 用户信息（有ID）
     */
    @CachePut(key = "#result.id")
    public User saveUser(User user) {
        return userDao.save(user);
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return {@code User} – 用户信息
     */
    @CachePut(key = "#user.id", value = "#user")
    public User updateUser(User user) {
        return userDao.update(user);
    }

    /**
     * 批量更新用户信息
     * <p>
     * {@link CachePutAll} 注解， 对应 {@code cache.putAll(Map<K,V> keyValues) }方法.<p>
     * 如未配置 keyValues 表达式，默认采用方法返回值；如已配置 keyValues 表达式，解析该表达式提取键值对集合.
     *
     * @param users 用户信息列表
     * @return {@code Map<Long, User>} – 用户信息集合
     */
    @CachePutAll
    public Map<Long, User> updateUsers(List<User> users) {
        return userDao.batchUpdate(users);
    }

    /**
     * 删除用户信息
     * <p>
     * {@link CacheRemove} 注解，对应 {@code cache.evict(K key)} 方法.
     *
     * @param id 用户ID
     */
    @CacheRemove
    public void deleteUser(Long id) {
        userDao.delete(id);
    }

    /**
     * 批量删除用户信息
     * <p>
     * {@link CacheRemoveAll} 注解，对应 {@code cache.evictAll(Set<K> keys) }方法.
     *
     * @param ids 用户ID集合
     */
    @CacheRemoveAll
    public void deleteUsers(Set<Long> ids) {
        userDao.batchDelete(ids);
    }

    /**
     * 清空数据
     * <p>
     * {@link CacheClear} 注解，对应 {@code cache.clear()} 方法.
     */
    @CacheClear
    public void clear() {
        userDao.clear();
    }

}
