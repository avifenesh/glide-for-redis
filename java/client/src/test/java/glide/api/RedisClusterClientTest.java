/** Copyright GLIDE-for-Redis Project Contributors - SPDX Identifier: Apache-2.0 */
package glide.api;

import static glide.api.models.configuration.RequestRoutingConfiguration.SimpleRoute.ALL_NODES;
import static glide.api.models.configuration.RequestRoutingConfiguration.SimpleRoute.RANDOM;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import glide.managers.CommandManager;
import glide.managers.RedisExceptionCheckedFunction;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import redis_request.RedisRequestOuterClass.RedisRequest;
import response.ResponseOuterClass.Response;

public class RedisClusterClientTest {

    private final String[] TEST_ARGS = new String[0];

    @Test
    @SneakyThrows
    public void custom_command_returns_single_value() {
        var commandManager = new TestCommandManager(null);

        var client = new TestClient(commandManager, "TEST");

        var value = client.customCommand(TEST_ARGS).get();
        assertAll(
                () -> assertTrue(value.hasSingleData()),
                () -> assertEquals("TEST", value.getSingleValue()));
    }

    @Test
    @SneakyThrows
    public void custom_command_returns_multi_value() {
        var commandManager = new TestCommandManager(null);

        var data = Map.of("key1", "value1", "key2", "value2");
        var client = new TestClient(commandManager, data);

        var value = client.customCommand(TEST_ARGS).get();
        assertAll(
                () -> assertTrue(value.hasMultiData()), () -> assertEquals(data, value.getMultiValue()));
    }

    @Test
    @SneakyThrows
    // test checks that even a map returned as a single value when single node route is used
    public void custom_command_with_single_node_route_returns_single_value() {
        var commandManager = new TestCommandManager(null);

        var data = Map.of("key1", "value1", "key2", "value2");
        var client = new TestClient(commandManager, data);

        var value = client.customCommand(TEST_ARGS, RANDOM).get();
        assertAll(
                () -> assertTrue(value.hasSingleData()), () -> assertEquals(data, value.getSingleValue()));
    }

    @Test
    @SneakyThrows
    public void custom_command_with_multi_node_route_returns_multi_value() {
        var commandManager = new TestCommandManager(null);

        var data = Map.of("key1", "value1", "key2", "value2");
        var client = new TestClient(commandManager, data);

        var value = client.customCommand(TEST_ARGS, ALL_NODES).get();
        assertAll(
                () -> assertTrue(value.hasMultiData()), () -> assertEquals(data, value.getMultiValue()));
    }

    private static class TestClient extends RedisClusterClient {

        private final Object object;

        public TestClient(CommandManager commandManager, Object objectToReturn) {
            super(null, commandManager);
            object = objectToReturn;
        }

        @Override
        protected Object handleObjectResponse(Response response) {
            return object;
        }
    }

    private static class TestCommandManager extends CommandManager {

        private final Response response;

        public TestCommandManager(Response responseToReturn) {
            super(null);
            response = responseToReturn;
        }

        @Override
        public <T> CompletableFuture<T> submitCommandToChannel(
                RedisRequest.Builder command, RedisExceptionCheckedFunction<Response, T> responseHandler) {
            return CompletableFuture.supplyAsync(() -> responseHandler.apply(response));
        }
    }
}