package codes.laivy.data.redis.lettuce;

import codes.laivy.data.redis.RedisReceptor;
import org.jetbrains.annotations.NotNull;

public interface RedisLettuceReceptor extends RedisReceptor {
    @Override
    @NotNull RedisLettuceDatabase getDatabase();
}
