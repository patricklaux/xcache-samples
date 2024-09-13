package com.igeeksky.xcache.sample.spring;

import com.igeeksky.xcache.samples.domain.User;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
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