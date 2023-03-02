package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.variable.SqlVariableConfiguration;
import codes.laivy.data.sql.variable.type.SqlVariableType;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
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
    private @NotNull @Pattern("^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$") @Subst("column_name") String id;

    private final @NotNull SqlVariableType<MysqlVariable> type;
    private final @Nullable Object defValue;

    private final @Nullable SqlVariableConfiguration configuration;

    private boolean loaded = false;

    public MysqlVariableNative(
            @NotNull MysqlTable table,
            @NotNull @Pattern("^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$") @Subst("column_name") String id,
            @NotNull SqlVariableType<MysqlVariable> type,
            @Nullable Object defValue
    ) {
        this(table, id, type, defValue, null);
    }

    public MysqlVariableNative(
            @NotNull MysqlTable table,
            @NotNull @Pattern("^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$") @Subst("column_name") String id,
            @NotNull SqlVariableType<MysqlVariable> type,
            @Nullable Object defValue,
            @Nullable SqlVariableConfiguration configuration
    ) {
        this.table = table;
        this.id = id;
        this.type = type;
        this.defValue = defValue;
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
    @Pattern("^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$")
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull @Pattern("^[a-zA-Z][a-zA-Z0-9_]{0,62}[a-zA-Z0-9]$") @Subst("column_name") String id) {
        this.id = id;
    }

    @Override
    public @NotNull SqlVariableType<MysqlVariable> getType() {
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
