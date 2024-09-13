package com.igeeksky.xcache;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.igeeksky.xcache.extension.jackson.JacksonCodec;
import com.igeeksky.xcache.samples.domain.Response;
import com.igeeksky.xcache.samples.domain.User;
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
    void getUsers() {
        Response<Void> clear = clear();
        Assertions.assertEquals(Response.ok(), clear);

        User Jack1 = createUser("{\"name\":\"Jack1\",\"age\":18}").getData();
        User Jack2 = createUser("{\"name\":\"Jack2\",\"age\":18}").getData();
        User Jack3 = createUser("{\"name\":\"Jack3\",\"age\":18}").getData();
        Map<Long, User> created = Map.of(Jack1.getId(), Jack1, Jack2.getId(), Jack2, Jack3.getId(), Jack3);

        String url = "/user/get/list?ids=1,2,3,4,5";
        byte[] body = sendAndReceive(createGetRequest(url));
        Response<Map<Long, User>> response = RESPONSE_MAP_CODEC.decode(body);
        System.out.printf("%s : %s\n", "getUsers", response);

        Map<Long, User> data = response.getData();
        Assertions.assertEquals(3, data.size());
        Assertions.assertEquals(created, data);
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
        Response<Void> clear = clear();
        Assertions.assertEquals(Response.ok(), clear);

        User user = createUser("{\"name\":\"Jack5\",\"age\":18}").getData();
        user.setAge(20);

        Response<User> response = updateUser(user.toString());
        System.out.printf("%s : %s\n", "updateUser", response.getData());
        Assertions.assertEquals(user, response.getData());
    }

    @Test
    void updateUsers() {
        Response<Void> clear = clear();
        Assertions.assertEquals(Response.ok(), clear);

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

        User user10 = createUser("{\"name\":\"Jack10\",\"age\":18}").getData();
        User user11 = createUser("{\"name\":\"Jack11\",\"age\":18}").getData();
        User user12 = createUser("{\"name\":\"Jack12\",\"age\":18}").getData();

        Long user10Id = user10.getId();
        Long user11Id = user11.getId();
        Long user12Id = user12.getId();

        Assertions.assertEquals(user10, getUser(user10Id).getData());
        Assertions.assertEquals(user11, getUser(user11Id).getData());
        Assertions.assertEquals(user12, getUser(user12Id).getData());

        HttpRequest request = createDeleteRequest(String.format("/user/delete/list?ids=%s,%s,%s", user10Id, user11Id, user12Id));
        byte[] body = sendAndReceive(request);
        Response<Void> response = RESPONSE_VOID_CODEC.decode(body);
        Assertions.assertEquals(Response.ok(), response);

        Assertions.assertNull(getUser(user10Id).getData());
        Assertions.assertNull(getUser(user11Id).getData());
        Assertions.assertNull(getUser(user12Id).getData());
    }

    private static Response<User> getUser(long id) {
        String url = "/user/get/" + id;
        byte[] body = sendAndReceive(createGetRequest(url));
        return RESPONSE_USER_CODEC.decode(body);
    }

    private static Response<User> createUser(String user) {
        String url = "/user/create";
        byte[] body = sendAndReceive(createPostRequest(url, user));
        return RESPONSE_USER_CODEC.decode(body);
    }

    private static Response<User> updateUser(String user) {
        String url = "/user/update";
        byte[] body = sendAndReceive(createPostRequest(url, user));
        return RESPONSE_USER_CODEC.decode(body);
    }

    private static Response<Void> deleteUser(Long id) {
        String url = "/user/delete/" + id;
        byte[] body = sendAndReceive(createDeleteRequest(url));
        return RESPONSE_VOID_CODEC.decode(body);
    }

    private static Response<Void> clear() {
        String url = "/user/clear";
        byte[] body = sendAndReceive(createDeleteRequest(url));
        return RESPONSE_VOID_CODEC.decode(body);
    }

    private static HttpRequest createGetRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(HOST + url))
                .GET()
                .build();
    }

    private static HttpRequest createDeleteRequest(String url) {
        return HttpRequest.newBuilder()
                .uri(URI.create(HOST + url))
                .DELETE()
                .build();
    }

    private static HttpRequest createPostRequest(String url, String body) {
        return HttpRequest.newBuilder()
                .uri(URI.create(HOST + url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }

    private static byte[] sendAndReceive(HttpRequest request) {
        try {
            HttpResponse<byte[]> response = CLIENT.send(request, RESPONSE_HANDLER);
            return response.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}