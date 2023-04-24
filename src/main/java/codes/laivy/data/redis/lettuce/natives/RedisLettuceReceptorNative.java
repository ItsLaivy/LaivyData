package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.redis.RedisVariable;
import codes.laivy.data.redis.lettuce.RedisLettuceDatabase;
import codes.laivy.data.redis.lettuce.RedisLettuceReceptor;
import codes.laivy.data.redis.lettuce.RedisLettuceTable;
import codes.laivy.data.redis.variable.RedisKey;
import codes.laivy.data.redis.variable.container.RedisActiveVariableContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * The native Redis Lettuce receptor support of the LaivyData.
 *
 * @author Laivy
 * @since 1.0
 */
public class RedisLettuceReceptorNative implements RedisLettuceReceptor {

    private final @NotNull RedisLettuceDatabase database;
    private @NotNull String id;

    private final @NotNull Set<ActiveVariableContainer> activeVariableContainers = new LinkedHashSet<>();
    private final @NotNull Set<InactiveVariableContainer> inactiveVariableContainers = new LinkedHashSet<>();

    private boolean loaded = false;
    private boolean isNew = false;

    private final @Nullable RedisLettuceTable table;

    public RedisLettuceReceptorNative(
            @NotNull RedisLettuceDatabase database,
            @NotNull String id
    ) {
        this(database, null, id);
    }
    public RedisLettuceReceptorNative(
            @NotNull RedisLettuceTable table,
            @NotNull String id
    ) {
        this(table.getDatabase(), table, id);
    }
    public RedisLettuceReceptorNative(
            @NotNull RedisLettuceDatabase database,
            @Nullable RedisLettuceTable table,
            @NotNull String id
    ) {
        this.database = database;
        this.table = table;
        this.id = id;
    }

    @Override
    public void load() {
        if (isLoaded()) {
            throw new IllegalStateException("The receptor already is loaded.");
        }

        getActiveContainers().clear();
        getInactiveContainers().clear();

        getDatabase().getManager().getReceptorsManager().load(this);

        getDatabase().getLoadedReceptors().add(this);
        if (getTable() != null) {
            getTable().getLoadedReceptors().add(this);
        }

        loaded = true;
    }

    @Override
    public void save() {
        getDatabase().getManager().getReceptorsManager().save(this);
    }

    @Override
    public void unload(boolean save) {
        getDatabase().getManager().getReceptorsManager().unload(this, save);

        getActiveContainers().clear();
        getInactiveContainers().clear();

        getDatabase().getLoadedReceptors().remove(this);
        if (getTable() != null) {
            getTable().getLoadedReceptors().remove(this);
        }

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
        if (!isLoaded()) {
            throw new IllegalStateException("The receptor isn't loaded.");
        }

        for (ActiveVariableContainer activeVar : getActiveContainers()) {
            if (activeVar instanceof RedisActiveVariableContainer) {
                RedisActiveVariableContainer container = (RedisActiveVariableContainer) activeVar;

                if (container.getVariable() != null) {
                    if (container.getVariable().getId().equals(id)) {
                        //noinspection unchecked
                        return (T) container.get();
                    }
                } else {
                    throw new NullPointerException("The active containers of a receptor needs to have a variable!");
                }
            } else {
                throw new IllegalArgumentException("This receptor contains illegal container types");
            }
        }
        throw new NullPointerException("Couldn't find a variable with id '" + id + "' at the receptor '" + getId() + "'");
    }

    @Override
    public void set(@NotNull String id, @Nullable Object object) {
        if (!isLoaded()) {
            throw new IllegalStateException("The receptor isn't loaded.");
        }

        boolean set = false;
        for (ActiveVariableContainer activeVar : getActiveContainers()) {
            if (activeVar instanceof RedisActiveVariableContainer) {
                RedisActiveVariableContainer container = (RedisActiveVariableContainer) activeVar;

                if (container.getVariable() != null) {
                    if (container.getVariable().getId().equals(id)) {
                        container.set(object);
                        set = true;
                    }
                } else {
                    throw new NullPointerException("The active containers of a receptor needs to have a variable!");
                }
            } else {
                throw new IllegalArgumentException("This receptor contains illegal container types");
            }
        }

        if (!set) {
            throw new NullPointerException("Couldn't find a variable with id '" + id + "' at the receptor '" + getId() + "'");
        }
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
    public @NotNull Set<RedisKey> getKeys() {
        String pattern = getDatabase().getManager().getName() + ":";
        if (getTable() != null) pattern += getTable().getId() + ":";
        pattern += "*:" + getId();

        return getDatabase().getConnection().getKeys(pattern);
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
    public @NotNull RedisLettuceDatabase getDatabase() {
        return database;
    }

    @Override
    public @NotNull String getKey(@NotNull RedisVariable variable) {
        String key = getDatabase().getManager().getName() + ":";
        if (getTable() != null) key += getTable().getId() + ":";
        key += variable.getId() + ":" + getId();

        return key;
    }

    @Override
    public @Nullable RedisLettuceTable getTable() {
        return table;
    }
}
