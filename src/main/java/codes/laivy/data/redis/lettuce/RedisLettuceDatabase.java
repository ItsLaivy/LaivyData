package codes.laivy.data.redis.lettuce;

import codes.laivy.data.redis.RedisDatabase;
import codes.laivy.data.redis.lettuce.connection.RedisLettuceConnection;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisLettuceDatabase extends RedisDatabase {
    @Override
    @NotNull RedisLettuceManager<RedisLettuceReceptor, RedisLettuceVariable, RedisLettuceDatabase, RedisLettuceTable> getManager();

    @Override
    @NotNull RedisLettuceConnection getConnection();
}
