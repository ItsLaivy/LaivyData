package codes.laivy.data.redis.values;

import codes.laivy.data.redis.variable.RedisKey;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainer;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface RedisConnection {

    /**
     * Gets a redis key or throws a NullPointerException in case of invalid key
     * @param key the redis key
     * @return the {@link RedisKey}
     * @throws NullPointerException if this key doesn't exist at the redis
     */
    @NotNull RedisKey getKey(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String key) throws NullPointerException;

    /**
     * Sets the redis key value at the redis database
     * @param key the redis key instance
     * @param container the value
     */
    void setKey(@NotNull RedisKey key, @Nullable RedisActiveVariableContainer container);

}
