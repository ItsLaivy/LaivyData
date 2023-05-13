package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.redis.RedisReceptor;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.lettuce.RedisLettuceDatabase;
import codes.laivy.data.redis.lettuce.RedisLettuceTable;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The native Redis Lettuce table support of the LaivyData.
 * this native table autoload at the constructor.
 *
 * @author Laivy
 * @since 1.0
 */
public class RedisLettuceTableNative implements RedisLettuceTable {

    private final @NotNull RedisLettuceDatabase database;
    private @NotNull String id;

    private final @NotNull Set<RedisReceptor> receptors = new LinkedHashSet<>();
    private final @NotNull Set<RedisVariable> variables = new LinkedHashSet<>();

    public RedisLettuceTableNative(
            @NotNull RedisLettuceDatabase database,
            @NotNull String id
    ) {
        this(database, id, true);
    }

    public RedisLettuceTableNative(
            @NotNull RedisLettuceDatabase database,
            @NotNull String id,
            boolean autoLoad
    ) {
        this.database = database;
        this.id = id;

        if (autoLoad) {
            load();
        }
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull String id) {
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
