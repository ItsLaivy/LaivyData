package codes.laivy.data.redis.lettuce.natives.manager;

import codes.laivy.data.api.values.ResultData;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.lettuce.RedisLettuceReceptor;
import codes.laivy.data.redis.manager.RedisReceptorsManager;
import codes.laivy.data.redis.variable.RedisKey;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainer;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainerImpl;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Laivy
 * @since 1.0
 */
public class RedisLettuceReceptorsManagerNative implements RedisReceptorsManager<RedisLettuceReceptor> {
    @Override
    public void load(@NotNull RedisLettuceReceptor receptor) {
        receptor.setNew(false);

        // TODO: 05/03/2023 Storing data
//        if (receptor.getStoringData()) {
//
//        }

        receptor.setNew(receptor.getKeys().isEmpty());

        for (RedisVariable variable : receptor.getVariables()) {
            RedisKey key = receptor.getDatabase().getKey(receptor, variable);
            if (key != null) {
                receptor.getActiveContainers().add(new RedisActiveVariableContainerImpl(variable, receptor, variable.getType().deserialize(key.getValue())));
            } else {
                receptor.getActiveContainers().add(new RedisActiveVariableContainerImpl(variable, receptor, variable.getDefault()));
            }
        }

        save(receptor);
    }

    @Override
    public void unload(@NotNull RedisLettuceReceptor receptor) {
        // TODO: 05/03/2023 Storing data
//        if (receptor.getStoringData()) {
//
//        }
    }

    @Override
    public boolean isLoaded(@NotNull RedisLettuceReceptor receptor) {
        return false;
    }

    @Override
    public @Nullable ResultData getData(@NotNull RedisLettuceReceptor receptor) {
        return null;
    }

    @Override
    public void unload(@NotNull RedisLettuceReceptor receptor, boolean save) {
        if (save) {
            receptor.save();
        }
    }

    @Override
    public void save(@NotNull RedisLettuceReceptor receptor) {
        for (@NotNull ActiveVariableContainer container : receptor.getActiveContainers()) {
            RedisActiveVariableContainer redisContainer = (RedisActiveVariableContainer) container;

            if (redisContainer.getVariable() != null) {
                @Subst("redis_key") String key = receptor.getKey(redisContainer.getVariable());
                receptor.getDatabase().getConnection().setKey(() -> key, redisContainer);
            } else {
                throw new NullPointerException("The active containers of a receptor needs to have a variable!");
            }
        }
    }

    @Override
    public void delete(@NotNull RedisLettuceReceptor receptor) {
        for (@NotNull RedisKey key : receptor.getKeys()) {
            receptor.getDatabase().getConnection().delete(key);
        }
    }
}
