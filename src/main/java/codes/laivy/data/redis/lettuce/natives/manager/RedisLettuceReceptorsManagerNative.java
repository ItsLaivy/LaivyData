package codes.laivy.data.redis.lettuce.natives.manager;

import codes.laivy.data.api.values.ResultData;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.lettuce.RedisLettuceReceptor;
import codes.laivy.data.redis.manager.RedisReceptorsManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RedisLettuceReceptorsManagerNative implements RedisReceptorsManager<RedisLettuceReceptor> {
    @Override
    public void load(@NotNull RedisLettuceReceptor receptor) {
        receptor.setNew(false);

        for (RedisVariable variable : receptor.getVariables()) {
            String key = variable.getRedisVariableName(receptor);
            if (isKeyRegisteredAtRedis(key)) {
                receptor.setNew(false);
                new RedisInactiveVariable(receptor, variable.getName(), getConnection().sync().get(key));
            } else {
                new RedisActiveVariable(variable, receptor, variable.getDefaultValue());
            }
        }

        receptorSave(receptor);
    }

    @Override
    public void unload(@NotNull RedisLettuceReceptor object) {

    }

    @Override
    public boolean isLoaded(@NotNull RedisLettuceReceptor object) {
        return false;
    }

    @Override
    public @Nullable ResultData getData(@NotNull RedisLettuceReceptor receptor) {
        return null;
    }

    @Override
    public void unload(@NotNull RedisLettuceReceptor receptor, boolean save) {

    }

    @Override
    public void save(@NotNull RedisLettuceReceptor receptor) {

    }

    @Override
    public void delete(@NotNull RedisLettuceReceptor receptor) {

    }
}
