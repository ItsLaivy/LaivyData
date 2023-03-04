package codes.laivy.data.redis;

import codes.laivy.data.api.table.Table;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface RedisTable extends Table {
    @Override
    @NotNull RedisDatabase getDatabase();

    /**
     * The full list of all loaded receptors of this table
     * @return a set of all loaded receptors
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull Set<RedisReceptor> getLoadedReceptors();

    /**
     * The full list of all loaded variables of this table
     *
     * @return a set of all loaded variables
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull Set<RedisVariable> getLoadedVariables();

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    @NotNull String getId();

    @Override
    void setId(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id);
}
