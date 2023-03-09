package codes.laivy.data.redis.lettuce;

import codes.laivy.data.redis.RedisReceptor;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisLettuceReceptor extends RedisReceptor {
    @Override
    @NotNull RedisLettuceDatabase getDatabase();
}
