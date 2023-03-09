package codes.laivy.data.redis;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.redis.manager.RedisManager;
import codes.laivy.data.redis.values.RedisConnection;
import codes.laivy.data.redis.variable.RedisKey;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisDatabase extends Database {
    @Override
    @NotNull RedisManager<?, ?, ?, ?> getManager();

    /**
     * Gets a full list of all registered keys by this database
     * @return the set containing all keys of this database
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<String> getKeys();

    @Contract(pure = true)
    @NotNull RedisConnection getConnection();

    @NotNull Set<RedisReceptor> getLoadedReceptors();

    @NotNull Set<RedisVariable> getLoadedVariables();

    @Override
    @NotNull String getId();

    // Keys

    /**
     * Gets the redis key of a receptor
     * @param receptor the receptor
     * @param variable the variable
     * @return the redis key of the variable at this receptor
     */
    @Nullable RedisKey getKey(@NotNull RedisReceptor receptor, @NotNull RedisVariable variable);
}
