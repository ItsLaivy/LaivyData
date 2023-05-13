package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.variable.MysqlVariableType;
import codes.laivy.data.sql.variable.SqlVariableConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     The native MySQL Variable of LaivyData.
 *     This native variable autoload at constructor.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class MysqlVariableNative implements MysqlVariable {

    private final @NotNull MysqlTable table;
    private @NotNull String id;

    private final @NotNull MysqlVariableType<MysqlVariable> type;
    private final @Nullable Object defValue;

    private final @Nullable SqlVariableConfiguration configuration;

    private boolean loaded = false;

    public MysqlVariableNative(
            @NotNull MysqlTable table,
            @NotNull String id,
            @NotNull MysqlVariableType<MysqlVariable> type,
            @Nullable Object defValue
    ) {
        this(table, id, type, defValue, null);
    }

    public MysqlVariableNative(
            @NotNull MysqlTable table,
            @NotNull String id,
            @NotNull MysqlVariableType<MysqlVariable> type,
            @Nullable Object defValue,
            @Nullable SqlVariableConfiguration configuration
    ) {
        this(table, id, type, defValue, configuration, true);
    }
    public MysqlVariableNative(
            @NotNull MysqlTable table,
            @NotNull String id,
            @NotNull MysqlVariableType<MysqlVariable> type,
            @Nullable Object defValue,
            @Nullable SqlVariableConfiguration configuration,
            boolean autoLoad
    ) {
        this.table = table;
        this.id = id;
        this.type = type;
        this.defValue = defValue;
        this.configuration = configuration;

        // Parsing the default value
        if (!getType().isCompatible(getDefault())) {
            throw new RuntimeException("This default variable object isn't compatible with that variable type");
        }

        if (autoLoad) {
            load();
        }
    }

    @Override
    public @Nullable Object getDefault() {
        return defValue;
    }

    @Override
    public void load() {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("This table isn't loaded!");
        }

        getDatabase().getManager().getVariablesManager().load(this);
        getTable().getLoadedVariables().add(this);
        loaded = true;
    }

    @Override
    public void unload() {
        getDatabase().getManager().getVariablesManager().unload(this);
        getTable().getLoadedVariables().remove(this);
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
    public @Nullable SqlVariableConfiguration getConfiguration() {
        return configuration;
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
    public @NotNull MysqlVariableType<MysqlVariable> getType() {
        return type;
    }

    @Override
    public @NotNull MysqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Override
    public @NotNull MysqlTable getTable() {
        return table;
    }
}
