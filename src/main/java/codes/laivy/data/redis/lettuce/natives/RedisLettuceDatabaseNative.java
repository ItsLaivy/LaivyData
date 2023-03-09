package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.lettuce.*;
import codes.laivy.data.redis.lettuce.connection.RedisLettuceConnection;
import codes.laivy.data.redis.lettuce.natives.manager.RedisLettuceManagerNative;
import codes.laivy.data.redis.variable.RedisKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>
 *     The native Redis Lettuce Database of LaivyData.
 *     This native database autoload when created by the constructor.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (07/03/2023)
 */
public class RedisLettuceDatabaseNative implements RedisLettuceDatabase {

    private final @NotNull RedisLettuceManagerNative manager;
    private final @NotNull String id;

    private boolean loaded = false;

    private final @NotNull Set<RedisReceptor> receptors = new LinkedHashSet<>();
    private final @NotNull Set<RedisVariable> variables = new LinkedHashSet<>();

    public RedisLettuceDatabaseNative(
            @NotNull RedisLettuceManagerNative manager,
            @NotNull String id
    ) {
        this.manager = manager;
        this.id = id;

        load();
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
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @Nullable RedisKey getKey(@NotNull RedisReceptor receptor, @NotNull RedisVariable variable) {
        if (receptor.getDatabase() == this && variable.getDatabase() == this) {
            return getConnection().getKey(receptor.getKey(variable));
        } else {
            throw new IllegalArgumentException("The receptor and/or the variable database isn't this database!");
        }
    }

    @Override
    public @NotNull RedisLettuceManagerNative getManager() {
        return manager;
    }

    @Override
    public @NotNull RedisLettuceConnection getConnection() {
        return getManager().getConnection();
    }

    @Override
    public @NotNull Set<RedisReceptor> getLoadedReceptors() {
        return receptors;
    }

    @Override
    public @NotNull Set<RedisVariable> getLoadedVariables() {
        return variables;
    }
}
