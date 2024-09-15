package com.igeeksky.xcache.samples;

import com.igeeksky.xtool.core.collection.Maps;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@Repository
public class UserDao {

    // 读写锁（避免幻读）
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    // 模拟自增主键
    private final AtomicLong idGenerator = new AtomicLong(1);
    // 模拟数据库
    private final Map<Long, User> database = new ConcurrentHashMap<>();

    /**
     * 根据用户ID获取用户信息
     */
    public User findUser(Long id) {
        lock.readLock().lock();
        try {
            User user = database.get(id);
            return (user != null) ? user.clone() : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 根据用户ID批量获取用户信息
     */
    public Map<Long, User> findUserList(Set<? extends Long> ids) {
        lock.readLock().lock();
        try {
            Map<Long, User> users = Maps.newHashMap(ids.size());
            for (Long id : ids) {
                User user = database.get(id);
                if (user != null) {
                    users.put(id, user.clone());
                }
            }
            return users;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * 新增用户信息
     * <p>
     * 如果用户名已存在，抛出异常
     *
     * @param user 用户信息
     */
    public User save(User user) {
        lock.writeLock().lock();
        try {
            String name = user.getName();
            if (isExistName(name)) {
                throw new RuntimeException("name:[" + name + "] is exist");
            }
            user.setId(idGenerator.getAndIncrement());
            database.put(user.getId(), user.clone());
            return user;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 更新用户信息
     * <p>
     * 如果用户不存在，抛出异常
     *
     * @param user 用户信息
     */
    public User update(User user) {
        lock.writeLock().lock();
        try {
            Long id = user.getId();
            User old = database.get(id);
            if (old == null) {
                throw new RuntimeException("user is not exist");
            }
            String name = user.getName();
            if (!Objects.equals(old.getName(), name)) {
                if (isExistName(name)) {
                    throw new RuntimeException("name:[" + name + "] is exist");
                }
            }
            database.put(id, user.clone());
            return user;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 批量更新用户信息
     *
     * @param users 用户信息集合
     */
    public Map<Long, User> batchUpdate(List<User> users) {
        Map<Long, User> map = Maps.newHashMap(users.size());
        lock.writeLock().lock();
        try {
            for (User user : users) {
                User updated = this.update(user);
                map.put(updated.getId(), updated);
            }
            return map;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 删除用户信息
     *
     * @param id 用户ID
     */
    public void delete(Long id) {
        lock.writeLock().lock();
        try {
            database.remove(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 批量删除用户信息
     *
     * @param ids 用户ID集合
     */
    public void batchDelete(Set<Long> ids) {
        lock.writeLock().lock();
        try {
            for (Long id : ids) {
                this.delete(id);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 清空数据
     */
    public void clear() {
        lock.writeLock().lock();
        try {
            database.clear();
            idGenerator.set(1L);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * 判断用户名是否已存在
     * <p>
     * 类似于数据库唯一索引
     */
    private boolean isExistName(String name) {
        Collection<User> values = database.values();
        for (User user : values) {
            if (Objects.equals(name, user.getName())) {
                return true;
            }
        }
        return false;
    }

}