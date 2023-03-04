package codes.laivy.data.redis.lettuce;

import codes.laivy.data.redis.RedisVariable;
import org.jetbrains.annotations.NotNull;

public interface RedisLettuceVariable extends RedisVariable {
    @Override
    @NotNull RedisLettuceDatabase getDatabase();
}
