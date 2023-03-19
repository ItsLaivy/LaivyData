package codes.laivy.data.redis.lettuce.natives.manager;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.lettuce.RedisLettuceReceptor;
import codes.laivy.data.redis.lettuce.natives.RedisLettuceResultDataNative;
import codes.laivy.data.redis.manager.RedisReceptorsManager;
import codes.laivy.data.redis.values.RedisResultData;
import codes.laivy.data.redis.variable.RedisKey;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainer;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainerImpl;
import codes.laivy.data.redis.variable.container.RedisInactiveVariableContainerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

        Map<String, Object> map = Objects.requireNonNull(getData(receptor)).next();

        for (RedisVariable variable : receptor.getVariables()) {
            String key = receptor.getKey(variable);
            if (map.containsKey(key)) {
                receptor.getActiveContainers().add(new RedisActiveVariableContainerImpl(variable, receptor, variable.getType().deserialize((String) map.get(key))));
            } else {
                receptor.getActiveContainers().add(new RedisActiveVariableContainerImpl(variable, receptor, variable.getDefault()));
            }
            map.remove(key);
        }
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            receptor.getInactiveContainers().add(new RedisInactiveVariableContainerImpl(entry.getKey(), receptor, entry.getValue()));
        }

        save(receptor);
    }

    @Override
    public void unload(@NotNull RedisLettuceReceptor receptor) {
        this.unload(receptor, true);
    }

    @Override
    public boolean isLoaded(@NotNull RedisLettuceReceptor receptor) {
        return receptor.isLoaded();
    }

    @Override
    public @Nullable RedisResultData getData(@NotNull RedisLettuceReceptor receptor) {
        Set<Map<String, @Nullable Object>> content = new LinkedHashSet<>();
        Map<String, @Nullable Object> map = new LinkedHashMap<>();

        for (RedisKey key : receptor.getKeys()) {
            map.put(key.getKey(), key.getValue());
        }

        content.add(map);
        return new RedisLettuceResultDataNative(content);
    }

    @Override
    public void unload(@NotNull RedisLettuceReceptor receptor, boolean save) {
        if (save) {
            receptor.save();
        }
    }

    @Override
    public void save(@NotNull RedisLettuceReceptor receptor) {
        for (@NotNull ActiveVariableContainer container : new LinkedHashSet<>(receptor.getActiveContainers())) {
            RedisActiveVariableContainer redisContainer = (RedisActiveVariableContainer) container;

            if (redisContainer.getVariable() != null) {
                String key = receptor.getKey(redisContainer.getVariable());
                receptor.getDatabase().getConnection().setKey(() -> key, redisContainer);
            } else {
                throw new NullPointerException("The active containers of a receptor needs to have a variable!");
            }
        }
    }

    @Override
    public void delete(@NotNull RedisLettuceReceptor receptor) {
        for (@NotNull RedisKey key : new LinkedHashSet<>(receptor.getKeys())) {
            receptor.getDatabase().getConnection().delete(key);
        }
    }
}
