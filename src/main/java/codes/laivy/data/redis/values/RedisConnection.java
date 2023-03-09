package codes.laivy.data.redis.values;

import codes.laivy.data.redis.variable.RedisKey;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainer;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisConnection {

    /**
     * Gets a redis key or throws a NullPointerException in case of invalid key
     * @param key the redis key
     * @return the {@link RedisKey}, or null if the key doesn't exist
     */
    @Nullable RedisKey getKey(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String key);

    /**
     * Gets the keys using the pattern
     * @param pattern the pattern
     * @return keys included at this pattern
     */
    @NotNull Set<RedisKey> getKeys(@NotNull String pattern);

    /**
     * Sets the redis key value at the redis database
     * @param key the redis key instance
     * @param container the value
     */
    void setKey(@NotNull RedisKey key, @Nullable RedisActiveVariableContainer container);

    /**
     * Checks if a key exists at the database
     * @return true if exists, false otherwise
     * @param key the redis string key
     * @deprecated Use {@link #exists(RedisKey)} instead
     */
    @Deprecated
    boolean exists(@NotNull String key);
    /**
     * Checks if a key exists at the database
     * @return true if exists, false otherwise
     * @param key the redis key
     */
    boolean exists(@NotNull RedisKey key);

    /**
     * Deletes a redis key
     * @param key the redis key
     */
    void delete(@NotNull RedisKey key);

}
