package com.igeeksky.xcache.sample.base;

import com.igeeksky.xcache.common.Cache;
import com.igeeksky.xcache.common.CacheLoader;
import com.igeeksky.xcache.core.CacheManager;
import com.igeeksky.xcache.samples.domain.User;
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
public class UserCacheService {

    private final UserDao userDao;
    private final Cache<Long, User> cache;
    private final CacheLoader<Long, User> cacheLoader;

    public UserCacheService(UserDao userDao, CacheManager cacheManager) {
        this.userDao = userDao;
        this.cache = cacheManager.getOrCreateCache("user", Long.class, User.class);
        this.cacheLoader = new UserCacheLoader(this.userDao);
    }

    /**
     * 根据用户ID获取单个用户信息
     *
     * @param id 用户ID
     */
    public User getUser(Long id) {
        return cache.get(id, cacheLoader);
    }

    /**
     * 根据用户ID批量获取用户信息
     *
     * @param ids 用户ID集合
     */
    public Map<Long, User> getUsers(Set<Long> ids) {
        return cache.getAll(ids, this.cacheLoader);
    }

    /**
     * 新增用户
     *
     * @param user 用户信息
     */
    public User saveUser(User user) {
        User created = userDao.save(user);
        cache.put(user.getId(), created);
        return created;
    }

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    public User updateUser(User user) {
        User updated = userDao.update(user);
        cache.put(user.getId(), updated);
        return updated;
    }

    /**
     * 批量更新用户信息
     *
     * @param users 用户信息集合
     */
    public Map<Long, User> updateUsers(List<User> users) {
        Map<Long, User> updates = userDao.batchUpdate(users);
        cache.putAll(updates);
        return updates;
    }

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     */
    public void deleteUser(Long id) {
        userDao.delete(id);
        cache.evict(id);
    }

    /**
     * 批量删除用户信息
     *
     * @param ids 用户ID集合
     */
    public void deleteUsers(Set<Long> ids) {
        userDao.batchDelete(ids);
        cache.evictAll(ids);
    }

    /**
     * 清空数据
     */
    public void clear() {
        userDao.clear();
        cache.clear();
    }

    /**
     * 内部类实现 CacheLoader
     *
     * @param userDao
     */
    private record UserCacheLoader(UserDao userDao) implements CacheLoader<Long, User> {

        @Override
        public User load(Long id) {
            return this.userDao.findUser(id);
        }

        @Override
        public Map<Long, User> loadAll(Set<? extends Long> ids) {
            return this.userDao.findUserList(ids);
        }

    }

}
