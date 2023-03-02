package codes.laivy.data.sql.sqlite.natives;

import codes.laivy.data.sql.sqlite.SqliteDatabase;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.sqlite.variable.SqliteVariableType;
import codes.laivy.data.sql.variable.SqlVariableConfiguration;
import io.netty.util.internal.UnstableApi;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <p>
 *     The native SQLite Variable of LaivyData.
 *     This native variable autoload at constructor.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (02/03/2023)
 */
@UnstableApi
public class SqliteVariableNative implements SqliteVariable {

    private final @NotNull SqliteTable table;
    private @NotNull @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$") @Subst("column_name") String id;

    private final @NotNull SqliteVariableType<SqliteVariable> type;
    protected @Nullable Object defValue;

    private final @Nullable SqlVariableConfiguration configuration;

    private boolean loaded = false;

    public SqliteVariableNative(
            @NotNull SqliteTable table,
            @NotNull @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$") @Subst("column_name") String id,
            @NotNull SqliteVariableType<SqliteVariable> type
    ) {
        this(table, id, type, null);
    }

    public SqliteVariableNative(
            @NotNull SqliteTable table,
            @NotNull @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$") @Subst("column_name") String id,
            @NotNull SqliteVariableType<SqliteVariable> type,
            @Nullable SqlVariableConfiguration configuration
    ) {
        this.table = table;
        this.id = id;
        this.type = type;
        this.defValue = null;
        this.configuration = configuration;

        if (!table.isLoaded()) {
            throw new IllegalStateException("This table isn't loaded!");
        }

        load();
    }

    @Override
    public @Nullable Object getDefault() {
        return defValue;
    }

    @Override
    public void load() {
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
    @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$")
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$") @Subst("column_name") String id) {
        this.id = id;
    }

    @Override
    public @NotNull SqliteVariableType<SqliteVariable> getType() {
        return type;
    }

    @Override
    public @NotNull SqliteDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Override
    public @NotNull SqliteTable getTable() {
        return table;
    }
}
