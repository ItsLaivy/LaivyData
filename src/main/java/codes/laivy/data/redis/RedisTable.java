package codes.laivy.data.redis;

import codes.laivy.data.api.table.Table;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

/**
 * @author Laivy
 * @since 1.0
 */
public interface RedisTable extends Table {
    @Override
    @NotNull RedisDatabase getDatabase();

    /**
     * The full list of all loaded receptors of this table
     * @return a set of all loaded receptors
     *
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<RedisReceptor> getLoadedReceptors();

    /**
     * The full list of all loaded variables of this table
     *
     * @return a set of all loaded variables
     * @author Laivy
     * @since 1.0
     */
    @NotNull Set<RedisVariable> getLoadedVariables();

    @Override
    @NotNull String getId();

    @Override
    void setId(@NotNull String id);
}
