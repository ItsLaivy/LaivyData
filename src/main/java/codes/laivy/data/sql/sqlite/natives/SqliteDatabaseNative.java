package codes.laivy.data.sql.sqlite.natives;

import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.sqlite.*;
import codes.laivy.data.sql.sqlite.connection.SqliteConnection;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.sql.DriverManager;
import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.regex.Pattern.matches;

/**
 * <p>
 *     The native SQLite Database of LaivyData.
 *     This native database autoload when created at the constructor.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqliteDatabaseNative implements SqliteDatabase {

    private final @NotNull SqliteManager<SqliteReceptor, SqliteVariable, SqliteDatabase, SqliteTable> manager;
    private final @NotNull @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$") @Subst("database") String id;

    private final @NotNull Set<SqlTable> tables = new LinkedHashSet<>();

    private boolean loaded = false;

    protected @Nullable SqliteConnection connection;

    public SqliteDatabaseNative(@NotNull SqliteManager<SqliteReceptor, SqliteVariable, SqliteDatabase, SqliteTable> manager, @NotNull @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$") @Subst("database") String id) {
        this.manager = manager;

        if (!matches("^([a-zA-Z][a-zA-Z0-9_]{0,64})$", id)) {
            throw new IllegalArgumentException("The native sqlite database id must follow the regex '^([a-zA-Z][a-zA-Z0-9_]{0,64})$'");
        }

        this.id = id;

        load();
    }

    @Override
    public @NotNull File getFile() {
        return new File(getManager().getPath() + File.separator + getId() + ".db");
    }

    @Override
    public void load() {
        // Load the connection
        try {
            if (!getFile().createNewFile() && !getFile().exists()) {
                throw new RuntimeException("Couldn't create native sqlite file at '" + getFile() + "'");
            }

            Class.forName("org.sqlite.JDBC");
            this.connection = new SqliteConnectionNative(DriverManager.getConnection("jdbc:sqlite:" + getFile()));
        } catch (Exception e) {
            if (e.getClass().equals(ClassNotFoundException.class)) {
                throw new RuntimeException("Couldn't get the JDBC Drivers for the native sqlite connection!", e);
            } else {
                throw new RuntimeException("Couldn't load native sqlite database '" + getId() + "'. File '" + getFile() + "'", e);
            }
        }
        //
        getManager().load(this);
        loaded = true;
    }

    @Override
    public void unload() {
        getManager().unload(this);
        this.connection = null;
        loaded = false;
    }

    @Override
    public void delete() {
        getManager().delete(this);
        //noinspection ResultOfMethodCallIgnored
        getFile().delete();
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public @NotNull SqliteConnection getConnection() {
        if (!isLoaded()) {
            throw new IllegalStateException("The database isn't loaded!");
        } else if (connection == null) {
            throw new NullPointerException("The connection instance is null!");
        }
        return connection;
    }

    @Override
    public @NotNull Set<SqlTable> getLoadedTables() {
        return tables;
    }

    @Override
    @Pattern("^([a-zA-Z][a-zA-Z0-9_]{0,64})$")
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull SqliteManager<SqliteReceptor, SqliteVariable, SqliteDatabase, SqliteTable> getManager() {
        return manager;
    }


}
