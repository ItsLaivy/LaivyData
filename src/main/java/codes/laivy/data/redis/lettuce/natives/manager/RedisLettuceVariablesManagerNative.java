package codes.laivy.data.redis.lettuce.natives.manager;

import codes.laivy.data.redis.lettuce.RedisLettuceVariable;
import codes.laivy.data.redis.manager.RedisVariablesManager;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public class RedisLettuceVariablesManagerNative implements RedisVariablesManager<RedisLettuceVariable> {
    @Override
    public void load(@NotNull RedisLettuceVariable object) {

    }

    @Override
    public void unload(@NotNull RedisLettuceVariable object) {

    }

    @Override
    public boolean isLoaded(@NotNull RedisLettuceVariable object) {
        return object.isLoaded();
    }

    @Override
    public void delete(@NotNull RedisLettuceVariable variable) {

    }
}
