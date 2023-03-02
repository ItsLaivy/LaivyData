package codes.laivy.data.redis;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.redis.manager.RedisManager;
import org.jetbrains.annotations.NotNull;

public interface RedisDatabase extends Database {
    @Override
    @NotNull RedisManager<RedisReceptor, RedisVariable, RedisDatabase, RedisTable> getManager();
}
