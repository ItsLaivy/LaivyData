package codes.laivy.data.sql;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.sql.manager.SqlManager;
import codes.laivy.data.sql.values.SqlConnection;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class SqlDatabase implements Database {

    /**
     * The default regex for SQL database names
     */
    private static final @NotNull @Language("RegExp") String SQL_DATABASE_NAME_REGEX = ".";

    private final @NotNull SqlManager<SqlReceptor, SqlVariable, SqlDatabase, SqlTable> manager;
    private final @NotNull @Pattern(SQL_DATABASE_NAME_REGEX) @Subst(SQL_DATABASE_NAME_REGEX) String id;

    protected final @NotNull Set<SqlTable> tables;

    public SqlDatabase(@NotNull SqlManager<SqlReceptor, SqlVariable, SqlDatabase, SqlTable> manager, @NotNull @Pattern(SQL_DATABASE_NAME_REGEX) @Subst(SQL_DATABASE_NAME_REGEX) String id) {
        this.manager = manager;
        this.id = id;

        tables = new LinkedHashSet<>();
    }

    @Contract(pure = true)
    public abstract @NotNull SqlConnection getConnection();

    /**
     * This collection is unmodifiable.
     * @return all loaded tables at this database
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @Unmodifiable
    public @NotNull Set<SqlTable> getTables() {
        return Collections.unmodifiableSet(tables);
    }

    @Override
    @Pattern(SQL_DATABASE_NAME_REGEX)
    @Contract(pure = true)
    public @NotNull String getId() {
        return id;
    }

    @Override
    @Contract(pure = true)
    public @NotNull SqlManager<SqlReceptor, SqlVariable, SqlDatabase, SqlTable> getManager() {
        return manager;
    }

    @Override
    public void load() {
        getManager().load(this);
    }

    @Override
    public void unload() {
        getManager().unload(this);
    }

    @Override
    public void delete() {
        getManager().delete(this);
    }
}
