package codes.laivy.data.redis.manager;

import codes.laivy.data.api.manager.DatabaseManager;
import codes.laivy.data.redis.RedisDatabase;
import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.RedisTable;
import codes.laivy.data.redis.RedisVariable;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * @param <R> The receptor type
 * @param <V> The variable type
 * @param <D> The database type
 * @param <T> The table type
 */
public interface RedisManager<R extends RedisReceptor, V extends RedisVariable, D extends RedisDatabase, T extends RedisTable> extends DatabaseManager<R, V, D, T> {
    @Override
    @NotNull RedisVariablesManager<V> getVariablesManager();

    @Override
    @NotNull RedisReceptorsManager<R> getReceptorsManager();

    @Override
    @NotNull RedisTablesManager<T> getTablesManager();

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    @NotNull String getName();
}
