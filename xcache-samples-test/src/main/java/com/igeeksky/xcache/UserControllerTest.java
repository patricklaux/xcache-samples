package com.igeeksky.xcache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igeeksky.xcache.extension.jackson.JacksonCodec;
import com.igeeksky.xcache.samples.Response;
import com.igeeksky.xcache.samples.User;
import com.igeeksky.xtool.core.json.SimpleJSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * @author Patrick.Lau
 * @since 1.0.0 2024/9/11
 */
public class UserControllerTest {

    private static final String HOST = "http://localhost:8080";

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    private static final HttpResponse.BodyHandler<byte[]> RESPONSE_HANDLER =
            responseInfo -> HttpResponse.BodySubscribers.ofByteArray();

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JavaType RESPONSE_USER_TYPE = MAPPER.getTypeFactory().constructParametricType(Response.class, User.class);
    private static final JavaType RESPONSE_VOID_TYPE = MAPPER.getTypeFactory().constructParametricType(Response.class, User.class);
    private static final JavaType RESPONSE_MAP_TYPE = MAPPER.getTypeFactory().constructType(new TypeReference<Response<Map<Long, User>>>() {
    });

    private static final JacksonCodec<Response<User>> RESPONSE_USER_CODEC = new JacksonCodec<>(MAPPER, RESPONSE_USER_TYPE);
    private static final JacksonCodec<Response<Void>> RESPONSE_VOID_CODEC = new JacksonCodec<>(MAPPER, RESPONSE_VOID_TYPE);
    private static final JacksonCodec<Response<Map<Long, User>>> RESPONSE_MAP_CODEC = new JacksonCodec<>(MAPPER, RESPONSE_MAP_TYPE);

    @Test
    void getUser() {
        Response<Void> clear = clear();
        Assertions.assertEquals(Response.ok(), clear);

        Response<User> createResponse = createUser("{\"name\":\"Jack0\",\"age\":18}");
        User created = createResponse.getData();

        Response<User> response = getUser(created.getId());
        System.out.printf("%s : %s\n", "getUser", response.getData());
        Assertions.assertEquals(created, response.getData());
    }

    @Test
    void getUserById() {
        User user = getUser(1L).getData();
        System.out.printf("%s : %s\n", "getUserById", user);
    }

    @Test
    void getUsers() {
        clear();

        User Jack1 = createUser("{\"name\":\"Jack1\",\"age\":18}").getData();
        User Jack2 = createUser("{\"name\":\"Jack2\",\"age\":18}").getData();
        User Jack3 = createUser("{\"name\":\"Jack3\",\"age\":18}").getData();
        Map<Long, User> created = Map.of(Jack1.getId(), Jack1, Jack2.getId(), Jack2, Jack3.getId(), Jack3);

        // 第一次调用时，缓存缺少 4,5 的数据，方法执行
        String url = "/user/get/list?ids=1,2,3,4,5";
        Response<Map<Long, User>> response = getUsers(url);
        System.out.printf("%s : %s\n", "getUsers", response);

        Map<Long, User> data = response.getData();
        Assertions.assertEquals(3, data.size());
        Assertions.assertEquals(created, data);

        // 第二次调用时，缓存全部命中，方法不执行
        Response<Map<Long, User>> response2 = getUsers(url);
        Map<Long, User> data2 = response2.getData();
        Assertions.assertEquals(3, data2.size());
        Assertions.assertEquals(created, data2);
    }

    @Test
    void createUser() {
        User user = createUser("{\"name\":\"Jack4\",\"age\":18}").getData();
        System.out.printf("%s : %s\n", "createUser", user);
        Assertions.assertNotNull(user.getId());
        Assertions.assertEquals(18, user.getAge());
        Assertions.assertEquals("Jack4", user.getName());
    }

    @Test
    void updateUser() {
        clear();

        User user = createUser("{\"name\":\"Jack5\",\"age\":18}").getData();
        user.setAge(20);

        Response<User> response = updateUser(user.toString());
        System.out.printf("%s : %s\n", "updateUser", response.getData());
        Assertions.assertEquals(user, response.getData());
    }

    @Test
    void updateUsers() {
        clear();

        User Jack6 = createUser("{\"name\":\"Jack6\",\"age\":18}").getData();
        User Jack7 = createUser("{\"name\":\"Jack7\",\"age\":18}").getData();
        User Jack8 = createUser("{\"name\":\"Jack8\",\"age\":18}").getData();

        Jack6.setAge(20);
        Jack7.setAge(21);
        Jack8.setAge(22);

        List<User> users = List.of(Jack6, Jack7, Jack8);

        byte[] body = sendAndReceive(createPostRequest("/user/update/list", SimpleJSON.toJSONString(users)));
        Response<Map<Long, User>> response = RESPONSE_MAP_CODEC.decode(body);
        System.out.printf("%s : %s\n", "updateUser", response);
        Assertions.assertEquals(users, response.getData().values().stream().toList());

        Assertions.assertEquals(Jack6, getUser(Jack6.getId()).getData());
        Assertions.assertEquals(Jack7, getUser(Jack7.getId()).getData());
        Assertions.assertEquals(Jack8, getUser(Jack8.getId()).getData());
    }

    @Test
    void deleteUser() {
        clear();

        User user = createUser("{\"name\":\"Jack9\",\"age\":18}").getData();
        Assertions.assertNotNull(user);

        Long id = user.getId();
        Response<Void> response = deleteUser(id);
        System.out.printf("%s : %s\n", "deleteUser", response);
        Assertions.assertEquals(Response.ok(), response);

        User user1 = getUser(id).getData();
        Assertions.assertNull(user1);
    }

    @Test
    void deleteUsers() {
        clear();

        // 创建用户
        User user10 = createUser("{\"name\":\"Jack10\",\"age\":18}").getData();
        User user11 = createUser("{\"name\":\"Jack11\",\"age\":18}").getData();
        User user12 = createUser("{\"name\":\"Jack12\",\"age\":18}").getData();

        Long user10Id = user10.getId();
        Long user11Id = user11.getId();
        Long user12Id = user12.getId();

        // 创建用户后查询，用户不为空
        Assertions.assertEquals(user10, getUser(user10Id).getData());
        Assertions.assertEquals(user11, getUser(user11Id).getData());
        Assertions.assertEquals(user12, getUser(user12Id).getData());

        // 删除用户
        HttpRequest request = createDeleteRequest(String.format("/user/delete/list?ids=%s,%s,%s", user10Id, user11Id, user12Id));
        byte[] body = sendAndReceive(request);
        Response<Void> response = RESPONSE_VOID_CODEC.decode(body);
        Assertions.assertEquals(Response.ok(), response);

        // 删除用户后再次查询，应该返回 null
        Assertions.assertNull(getUser(user10Id).getData());
        Assertions.assertNull(getUser(user11Id).getData());
        Assertions.assertNull(getUser(user12Id).getData());
    }

    /**
     * 发送获取用户请求
     *
     * @param id 用户ID
     * @return 包含用户信息的响应对象，类型为 {@code Response<User>}，其中 User 为获取到的用户信息
     */
    private static Response<User> getUser(long id) {
        String url = "/user/get/" + id;
        byte[] body = sendAndReceive(createGetRequest(url));
        return RESPONSE_USER_CODEC.decode(body);
    }

    /**
     * 发送获取用户列表请求
     *
     * @param url 请求URL，包含查询参数，如："/user/get/list?ids=1,2,3"
     * @return 包含用户列表的响应对象，类型为 {@code Response<Map<Long, User>>}，其中 Map 的键为用户ID，值为用户信息
     */
    private static Response<Map<Long, User>> getUsers(String url) {
        byte[] body = sendAndReceive(createGetRequest(url));
        return RESPONSE_MAP_CODEC.decode(body);
    }

    /**
     * 发送创建用户请求
     *
     * @param user 用户信息(JSON String)
     * @return 包含用户信息的响应对象，类型为 {@code Response<User>}，其中 User 为创建的用户信息
     */
    private static Response<User> createUser(String user) {
        // 创建用户请求的URL路径
        String url = "/user/create";
        // 发送请求并接收响应，请求体即为用户信息字符串，经过序列化后发送
        byte[] body = sendAndReceive(createPostRequest(url, user));
        // 解码响应体，返回包含用户信息的响应对象
        return RESPONSE_USER_CODEC.decode(body);
    }

    /**
     * 发送更新用户请求
     *
     * @param user 用户信息(JSON String)
     * @return 包含更新后的用户信息的响应对象，类型为 {@code Response<User>}，其中 User 为更新后的用户信息
     */
    private static Response<User> updateUser(String user) {
        String url = "/user/update";
        byte[] body = sendAndReceive(createPostRequest(url, user));
        return RESPONSE_USER_CODEC.decode(body);
    }

    /**
     * 发送删除用户请求
     *
     * @param id 用户ID
     * @return 包含删除操作结果的响应对象，类型为 {@code Response<Void>}
     */
    private static Response<Void> deleteUser(Long id) {
        String url = "/user/delete/" + id;
        byte[] body = sendAndReceive(createDeleteRequest(url));
        return RESPONSE_VOID_CODEC.decode(body);
    }

    /**
     * 清空数据
     *
     * @return 包含清空操作结果的响应对象，类型为 {@code Response<Void>}
     */
    private static Response<Void> clear() {
        String url = "/user/clear";
        byte[] body = sendAndReceive(createDeleteRequest(url));
        return RESPONSE_VOID_CODEC.decode(body);
    }

    /**
     * 创建GET请求
     *
     * @param url 请求URL路径
     * @return HttpRequest对象，用于发送GET请求
     */
    private static HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(HOST + url))
                .GET()
                .build();
    }

    /**
     * 创建DELETE请求
     *
     * @param url 请求URL路径
     * @return HttpRequest对象，用于发送DELETE请求
     */
    private static HttpRequest createDeleteRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(HOST + url))
                .DELETE()
                .build();
    }

    /**
     * 创建POST请求
     *
     * @param url  请求URL路径
     * @param body 请求体，类型为 JSON格式的字符串
     * @return HttpRequest对象，用于发送POST请求
     */
    private static HttpRequest createPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(HOST + url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    /**
     * 发送HTTP请求并接收响应
     *
     * @param request HTTP请求对象
     * @return 响应体，类型为 byte[]
     */
    private static byte[] sendAndReceive(HttpRequest request) {
        try {
            return CLIENT.send(request, RESPONSE_HANDLER).body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}