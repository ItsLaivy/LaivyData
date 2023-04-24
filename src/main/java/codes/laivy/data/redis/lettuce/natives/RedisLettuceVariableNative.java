package codes.laivy.data.redis.lettuce.natives;

import codes.laivy.data.redis.RedisTable;
import codes.laivy.data.redis.lettuce.RedisLettuceDatabase;
import codes.laivy.data.redis.lettuce.RedisLettuceTable;
import codes.laivy.data.redis.lettuce.RedisLettuceVariable;
import codes.laivy.data.redis.variable.RedisVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The native Redis Lettuce variable support of the LaivyData.
 * The constructor autoload the variable!
 *
 * @author Laivy
 * @since 1.0
 */
public class RedisLettuceVariableNative implements RedisLettuceVariable {

    private final @NotNull RedisLettuceDatabase database;
    private @NotNull String id;
    private final @NotNull RedisVariableType type;
    private final @Nullable Object defValue;

    private final @Nullable RedisLettuceTable table;

    private boolean loaded = false;

    public RedisLettuceVariableNative(
            @NotNull RedisLettuceDatabase database,
            @NotNull String id,
            @NotNull RedisVariableType type,
            @Nullable Object defValue
    ) {
        this(database, null, id, type, defValue);
    }
    public RedisLettuceVariableNative(
            @NotNull RedisLettuceTable table,
            @NotNull String id,
            @NotNull RedisVariableType type,
            @Nullable Object defValue
    ) {
        this(table.getDatabase(), table, id, type, defValue);
    }
    public RedisLettuceVariableNative(
            @NotNull RedisLettuceDatabase database,
            @Nullable RedisLettuceTable table,
            @NotNull String id,
            @NotNull RedisVariableType type,
            @Nullable Object defValue
    ) {
        this.database = database;
        this.table = table;
        this.id = id;
        this.type = type;
        this.defValue = defValue;

        // Parsing the default value
        if (!getType().isCompatible(getDefault())) {
            throw new RuntimeException("This default variable object isn't compatible with that variable type");
        }

        load();
    }

    @Override
    public @Nullable Object getDefault() {
        return defValue;
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
        getDatabase().getManager().getVariablesManager().load(this);

        getDatabase().getLoadedVariables().add(this);
        if (getTable() != null) {
            getTable().getLoadedVariables().add(this);
        }

        loaded = true;
    }

    @Override
    public void unload() {
        getDatabase().getManager().getVariablesManager().unload(this);

        getDatabase().getLoadedVariables().remove(this);
        if (getTable() != null) {
            getTable().getLoadedVariables().remove(this);
        }

        loaded = false;
    }

    @Override
    public void delete() {
        unload();
        getDatabase().getManager().getVariablesManager().delete(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public @NotNull RedisVariableType getType() {
        return type;
    }

    @Override
    public @NotNull RedisLettuceDatabase getDatabase() {
        return database;
    }

    @Override
    public @Nullable RedisTable getTable() {
        return table;
    }
}
