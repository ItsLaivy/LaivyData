package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.redis.lettuce.connection.RedisLettuceConnection;
import codes.laivy.data.redis.variable.RedisKey;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainer;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.concurrent.TimeUnit;

public class RedisLettuceConnectionNative implements RedisLettuceConnection {

    private final @NotNull RedisClient client;
    private final @NotNull StatefulRedisConnection<String, String> connection;
    private final @NotNull RedisCommands<String, String> sync;

    public RedisLettuceConnectionNative(@NotNull String host, int port) {
        this(host, null, 16000, port, false);
    }
    public RedisLettuceConnectionNative(@NotNull String host, @NotNull String password, @Range(from = 0, to = 65535) int port) {
        this(host, password, 16000, port, false);
    }
    public RedisLettuceConnectionNative(@NotNull String host, @Nullable String password, @Range(from = 1, to = Integer.MAX_VALUE) int timeoutMillis, @Range(from = 0, to = 65535) int port, boolean ssl) {
        // RedisClient creator
        RedisURI.Builder builder = RedisURI.builder()
                .withHost(host)
                .withPort(port)
                .withSsl(ssl)
                .withTimeout(timeoutMillis, TimeUnit.MILLISECONDS);
        if (password != null) builder.withPassword(password);

        this.client = RedisClient.create(builder.build());
        this.connection = this.client.connect();
        this.sync = connection.sync();
        //
    }

    public @NotNull RedisClient getClient() {
        return client;
    }

    public @NotNull StatefulRedisConnection<String, String> getConnection() {
        return connection;
    }

    @ApiStatus.Experimental
    public @NotNull RedisCommands<String, String> getSync() {
        return sync;
    }

    @Override
    public @NotNull RedisKey getKey(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String key) throws NullPointerException {
        if (getSync().exists(new String[] {key}) == 1) {
            return new RedisKey() {
                @Override
                @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
                public @NotNull String getKey() {
                    return key;
                }
                @Override
                public @Nullable String getValue() {
                    return getSync().get(key);
                }
            };
        } else {
            throw new NullPointerException("Couldn't find a key named '" + key + "' at this database");
        }
    }

    @Override
    public void setKey(@NotNull RedisKey key, @Nullable RedisActiveVariableContainer container) {
        if (container != null) {
            getSync().set(key.getKey(), container.getVariable().getType().serialize(container.get()));
        } else {
            getSync().set(key.getKey(), null);
        }
    }
}
