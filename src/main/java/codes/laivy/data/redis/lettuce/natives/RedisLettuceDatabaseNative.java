package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.redis.lettuce.*;
import codes.laivy.data.redis.lettuce.connection.RedisLettuceConnection;
import codes.laivy.data.redis.lettuce.natives.manager.RedisLettuceManagerNative;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class RedisLettuceDatabaseNative implements RedisLettuceDatabase {

    private final @NotNull RedisLettuceManagerNative manager;
    private final @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id;

    private boolean loaded = false;

    public RedisLettuceDatabaseNative(
            @NotNull RedisLettuceManagerNative manager,
            @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id
    ) {
        this.manager = manager;
        this.id = id;
    }

    @Override
    public void load() {
        getManager().load(this);
        loaded = true;
    }

    @Override
    public void unload() {
        getManager().unload(this);
        loaded = false;
    }

    @Override
    public void delete() {
        unload();
        getManager().delete(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public @NotNull Set<String> getKeys() {
        return new LinkedHashSet<>();
    }

    @Override
    @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$")
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull RedisLettuceManagerNative getManager() {
        return manager;
    }

    @Override
    public @NotNull RedisLettuceConnection getConnection() {
        return getManager().getConnection();
    }
}
