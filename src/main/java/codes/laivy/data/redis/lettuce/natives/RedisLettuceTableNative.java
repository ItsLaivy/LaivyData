package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.lettuce.RedisLettuceDatabase;
import codes.laivy.data.redis.lettuce.RedisLettuceTable;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class RedisLettuceTableNative implements RedisLettuceTable {

    private final @NotNull RedisLettuceDatabase database;
    private @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id;

    private final @NotNull Set<RedisReceptor> receptors = new LinkedHashSet<>();
    private final @NotNull Set<RedisVariable> variables = new LinkedHashSet<>();

    public RedisLettuceTableNative(
            @NotNull RedisLettuceDatabase database,
            @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id
    ) {
        this.database = database;
        this.id = id;
    }

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id) {
        this.id = id;
    }

    @Override
    public void load() {

    }

    @Override
    public void unload() {

    }

    @Override
    public void delete() {

    }

    @Override
    public boolean isLoaded() {
        return false;
    }

    @Override
    public @NotNull Set<RedisReceptor> getLoadedReceptors() {
        return receptors;
    }

    @Override
    public @NotNull Set<RedisVariable> getLoadedVariables() {
        return variables;
    }

    @Override
    public @NotNull RedisLettuceDatabase getDatabase() {
        return database;
    }
}
