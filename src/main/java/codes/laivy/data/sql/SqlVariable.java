package codes.laivy.data.sql;

import codes.laivy.data.api.table.Tableable;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.sql.variable.type.SqlVariableConfiguration;
import codes.laivy.data.sql.variable.type.SqlVariableType;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SqlVariable implements Variable, Tableable {

    /**
     * The default regex for SQL receptor names
     */
    private static final @NotNull @Language("RegExp") String SQL_VARIABLE_ID_REGEX = ".";

    private final @NotNull SqlTable table;
    private final @NotNull SqlVariableType type;
    protected @NotNull @Pattern(SQL_VARIABLE_ID_REGEX) @Subst(SQL_VARIABLE_ID_REGEX) String id;
    protected @Nullable SqlVariableConfiguration configuration;
    private final @Nullable Object d;

    protected boolean loaded;

    public SqlVariable(@NotNull SqlTable table, @NotNull SqlVariableType type, @Nullable Object d, @NotNull @Pattern(SQL_VARIABLE_ID_REGEX) @Subst(SQL_VARIABLE_ID_REGEX) String id) {
        this.table = table;
        this.type = type;
        this.id = id;
        this.d = d;

        this.configuration = null;

        loaded = false;
    }

    /**
     * This will return the variable's default value, it could be anything :)
     * @return the variable's default value
     */
    public @Nullable Object getDefault() {
        return d;
    }

    public @Nullable SqlVariableConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(@NotNull SqlVariableConfiguration configuration) {
        if (isLoaded()) {
            throw new UnsupportedOperationException("The variable needs to be unloaded to change the variable configuration");
        }
        this.configuration = configuration;
    }

    @Override
    @Pattern(SQL_VARIABLE_ID_REGEX)
    public @NotNull String getId() {
        return id;
    }

    @Override
    public void setId(@NotNull @Pattern(SQL_VARIABLE_ID_REGEX) @Subst(SQL_VARIABLE_ID_REGEX) String id) {
        this.id = id;
    }

    @Override
    @Contract(pure = true)
    public @NotNull SqlDatabase getDatabase() {
        return getTable().getDatabase();
    }
    @Override
    @Contract(pure = true)
    public @NotNull SqlTable getTable() {
        return table;
    }

    @Override
    @Contract(pure = true)
    public @NotNull SqlVariableType getType() {
        return type;
    }

    @Override
    public void load() {
        getDatabase().getManager().getVariablesManager().load(this);
        getTable().variables.add(this);
        loaded = true;
    }

    @Override
    public void unload() {
        getDatabase().getManager().getVariablesManager().unload(this);
        getTable().variables.remove(this);
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

}
