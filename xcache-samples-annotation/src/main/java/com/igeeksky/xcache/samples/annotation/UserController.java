package com.igeeksky.xcache.samples.annotation;

import com.igeeksky.xcache.samples.Response;
import com.igeeksky.xcache.samples.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用户信息接口
 *
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/7
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        log.info("UserController init...");
        this.userService = userService;
    }

    /**
     * 根据用户ID获取单个用户信息
     */
    @GetMapping("/get/{id}")
    public Response<User> getUser(@PathVariable("id") Long id) {
        return userService.getUser(id);
    }

    /**
     * 根据用户ID批量获取用户信息
     */
    @GetMapping("/get/list")
    public Response<Map<Long, User>> getUsers(@RequestParam Set<Long> ids) {
        return userService.getUsers(ids);
    }

    /**
     * 新增单个用户信息
     */
    @PostMapping("/create")
    public Response<User> createUser(@RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * 修改单个用户信息
     */
    @PostMapping("/update")
    public Response<User> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * 批量修改用户信息
     */
    @PostMapping("/update/list")
    public Response<Map<Long, User>> updateUsers(@RequestBody List<User> users) {
        return userService.updateUsers(users);
    }

    /**
     * 删除单个用户信息
     */
    @DeleteMapping("/delete/{id}")
    public Response<Void> deleteUser(@PathVariable Long id) {
        return userService.deleteUser(id);
    }

    /**
     * 删除多个用户信息
     */
    @DeleteMapping("/delete/list")
    public Response<Void> deleteUsers(@RequestParam Set<Long> ids) {
        return userService.deleteUsers(ids);
    }

    /**
     * 清空所有数据，以便测试
     *
     * @return Response
     */
    @DeleteMapping("/clear")
    public Response<Void> clear() {
        return userService.clear();
    }

}
