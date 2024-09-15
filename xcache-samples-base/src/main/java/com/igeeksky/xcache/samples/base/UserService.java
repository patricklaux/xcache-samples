package com.igeeksky.xcache.samples.base;

import com.igeeksky.xcache.samples.Response;
import com.igeeksky.xcache.samples.User;
import com.igeeksky.xtool.core.collection.CollectionUtils;
import com.igeeksky.xtool.core.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户服务
 *
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@Service
public class UserService {

    private final UserCacheService userCacheService;

    public UserService(UserCacheService userCacheService) {
        this.userCacheService = userCacheService;
    }

    /**
     * 根据用户ID获取单个用户信息
     */
    public Response<User> getUser(Long id) {
        if (id == null) {
            return Response.error("id is null");
        }
        try {
            return Response.ok(userCacheService.getUser(id));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 根据用户ID批量获取用户信息
     */
    public Response<Map<Long, User>> getUsers(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Response.error("ids is empty");
        }
        for (Long id : ids) {
            if (id == null) {
                return Response.error("id is null");
            }
        }
        try {
            return Response.ok(userCacheService.getUsers(ids));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 添加单个新用户
     */
    public Response<User> addUser(User user) {
        Response<User> error = validateNewUser(user);
        if (error != null) {
            return error;
        }
        try {
            return Response.ok(userCacheService.saveUser(user));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 修改单个用户信息
     */
    public Response<User> updateUser(User user) {
        Response<User> error = validateOldUser(user);
        if (error != null) {
            return error;
        }
        try {
            return Response.ok(userCacheService.updateUser(user));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 批量修改用户信息
     */
    public Response<Map<Long, User>> updateUsers(List<User> users) {
        if (CollectionUtils.isEmpty(users)) {
            return Response.error("users is empty");
        }
        for (User user : users) {
            Response<User> error = validateOldUser(user);
            if (error != null) {
                return Response.error(error.getMsg());
            }
        }
        try {
            return Response.ok(userCacheService.updateUsers(users));
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 删除单个用户信息
     */
    public Response<Void> deleteUser(Long id) {
        if (id == null) {
            return Response.error("id is null");
        }
        try {
            userCacheService.deleteUser(id);
            return Response.ok();
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 批量删除用户信息
     */
    public Response<Void> deleteUsers(Set<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Response.error("ids is empty");
        }
        for (Long id : ids) {
            if (id == null) {
                return Response.error("id is null");
            }
        }
        try {
            userCacheService.deleteUsers(ids);
            return Response.ok();
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

    /**
     * 校验新增用户信息是否合法
     */
    private static Response<User> validateNewUser(User user) {
        if (user == null) {
            return Response.error("user is null");
        }
        Long id = user.getId();
        if (id != null) {
            return Response.error("user id is not null");
        }
        String name = StringUtils.trimToNull(user.getName());
        if (name == null) {
            return Response.error("user name is null");
        }
        user.setName(name);
        return null;
    }

    /**
     * 校验修改用户信息是否合法
     */
    private static Response<User> validateOldUser(User user) {
        if (user == null) {
            return Response.error("user is null");
        }
        Long id = user.getId();
        if (id == null) {
            return Response.error("user id is null");
        }
        String name = StringUtils.trimToNull(user.getName());
        if (name == null) {
            return Response.error("user name is null");
        }
        user.setName(name);
        return null;
    }

    /**
     * 清空数据库及缓存的所有元素，以便测试
     */
    public Response<Void> clear() {
        try {
            userCacheService.clear();
            return Response.ok();
        } catch (RuntimeException e) {
            return Response.error(e.getMessage());
        }
    }

}