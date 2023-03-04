package codes.laivy.data.redis.lettuce;

import codes.laivy.data.redis.RedisTable;
import org.jetbrains.annotations.NotNull;

public interface RedisLettuceTable extends RedisTable {
    @Override
    @NotNull RedisLettuceDatabase getDatabase();
}
