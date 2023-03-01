package codes.laivy.data.sql;

import codes.laivy.data.api.table.Table;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.regex.Pattern.matches;

public class SqlTable implements Table {

    /**
     * The default regex for SQL receptor names
     */
    private static final @NotNull @Language("RegExp") String ID_REGEX = "^.$";

    private final @NotNull SqlDatabase database;
    protected @NotNull @Pattern(ID_REGEX) String id;

    protected final @NotNull Set<SqlReceptor> receptors;
    protected final @NotNull Set<SqlVariable> variables;

    protected boolean loaded;

    public SqlTable(@NotNull SqlDatabase database, @NotNull @Pattern("^.$") String id) {
        this.database = database;
        //noinspection PatternValidation
        this.id = id;

        receptors = new LinkedHashSet<>();
        variables = new LinkedHashSet<>();

        loaded = false;
    }

    /**
     * This collection is unmodifiable.
     * @return all loaded receptors at this table
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @Unmodifiable
    public @NotNull Set<SqlReceptor> getReceptors() {
        return Collections.unmodifiableSet(receptors);
    }

    /**
     * This collection is unmodifiable.
     * @return all loaded variables at this table
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @Unmodifiable
    public @NotNull Set<SqlVariable> getVariables() {
        return Collections.unmodifiableSet(variables);
    }

    /**
     * Gets a loaded variable at this table
     * @param id the variable id
     * @return a loaded variable or null if a variable with that id doesn't exist at this table
     */
    public @Nullable SqlVariable getVariable(@NotNull String id) {
        for (SqlVariable variable : getVariables()) {
            if (variable.getId().equals(id)) {
                return variable;
            }
        }
        return null;
    }

    @Override
    @Contract(pure = true)
    public @NotNull SqlDatabase getDatabase() {
        return database;
    }

    @Override
    @Pattern(ID_REGEX)
    public @NotNull String getId() {
        //noinspection PatternValidation
        return id;
    }

    @Override
    public void setId(@NotNull @Pattern(ID_REGEX) String id) {
        if (!matches(ID_REGEX, id)) {
            throw new IllegalArgumentException("The receptor id must follow the regex '" + ID_REGEX + "'");
        }
        //noinspection PatternValidation
        this.id = id;
    }

    @Override
    public void load() {
        getDatabase().getManager().getTablesManager().load(this);
        database.tables.add(this);
        loaded = true;
    }

    @Override
    public void unload() {
        getDatabase().getManager().getTablesManager().unload(this);
        database.tables.remove(this);
        loaded = false;
    }

    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public void delete() {
        unload();
        getDatabase().getManager().getTablesManager().delete(this);
    }
}
