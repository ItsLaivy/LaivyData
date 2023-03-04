package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.redis.lettuce.RedisLettuceDatabase;
import codes.laivy.data.redis.lettuce.RedisLettuceReceptor;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

public class RedisLettuceReceptorNative implements RedisLettuceReceptor {

    private final @NotNull RedisLettuceDatabase database;
    private @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id;

    private final @NotNull Set<ActiveVariableContainer> activeVariableContainers = new LinkedHashSet<>();
    private final @NotNull Set<InactiveVariableContainer> inactiveVariableContainers = new LinkedHashSet<>();

    private boolean loaded = false;
    private boolean isNew = false;

    public RedisLettuceReceptorNative(
            @NotNull RedisLettuceDatabase database,
            @NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id
    ) {
        this.database = database;
        this.id = id;
    }

    @Override
    public void load() {
        getDatabase().getManager().getReceptorsManager().load(this);
        loaded = true;
    }

    @Override
    public void save() {
        getDatabase().getManager().getReceptorsManager().save(this);
    }

    @Override
    public void unload(boolean save) {
        getDatabase().getManager().getReceptorsManager().unload(this);
        loaded = false;
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void delete() {
        unload(false);
        getDatabase().getManager().getReceptorsManager().delete(this);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    /**
     * Use at your own risk, it's made for internal use.
     * @param isNew the new state of receptor
     *
     * @author Laivy
     * @since 1.0
     */
    @ApiStatus.Internal
    public void setNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Override
    public <T> @Nullable T get(@NotNull String id) {
        return null;
    }

    @Override
    public void set(@NotNull String id, @Nullable Object object) {

    }

    @Override
    public @NotNull Set<@NotNull InactiveVariableContainer> getInactiveContainers() {
        return inactiveVariableContainers;
    }

    @Override
    public @NotNull Set<@NotNull ActiveVariableContainer> getActiveContainers() {
        return activeVariableContainers;
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
    public void setId(@NotNull @Pattern("^[a-zA-Z_][a-zA-Z0-9_:-]{0,127}$") @Subst("redis_key") String id) {
        this.id = id;
    }

    @Override
    public @NotNull RedisLettuceDatabase getDatabase() {
        return database;
    }
}
