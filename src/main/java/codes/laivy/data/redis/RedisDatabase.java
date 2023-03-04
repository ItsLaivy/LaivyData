package codes.laivy.data.redis;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.redis.manager.RedisManager;
import codes.laivy.data.redis.values.RedisConnection;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface RedisDatabase extends Database {
    @Override
    @NotNull RedisManager<?, ?, ?, ?> getManager();

    /**
     * Gets a full list of all registered keys by this database
     * @return the set containing all keys of this database
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull Set<String> getKeys();

    @Contract(pure = true)
    @NotNull RedisConnection getConnection();

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    @NotNull String getId();
}
