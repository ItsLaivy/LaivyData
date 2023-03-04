package codes.laivy.data.redis.lettuce.natives.manager;

import codes.laivy.data.redis.lettuce.RedisLettuceTable;
import codes.laivy.data.redis.manager.RedisTablesManager;
import org.jetbrains.annotations.NotNull;

public class RedisLettuceTablesManagerNative implements RedisTablesManager<RedisLettuceTable> {
    @Override
    public void load(@NotNull RedisLettuceTable object) {

    }

    @Override
    public void unload(@NotNull RedisLettuceTable object) {

    }

    @Override
    public void delete(@NotNull RedisLettuceTable object) {

    }

    @Override
    public boolean isLoaded(@NotNull RedisLettuceTable object) {
        return false;
    }
}
