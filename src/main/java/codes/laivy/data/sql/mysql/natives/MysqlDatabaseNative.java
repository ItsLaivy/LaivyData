package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.MysqlManager;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

import static java.util.regex.Pattern.matches;

/**
 * <p>
 *     The native MySQL Database of LaivyData.
 *     This native database autoload when created at the constructor.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class MysqlDatabaseNative implements MysqlDatabase {

    private final @NotNull MysqlManager<MysqlReceptor, MysqlVariable, MysqlDatabase, MysqlTable> manager;
    private final @NotNull String id;

    private final @NotNull Set<SqlTable> tables = new LinkedHashSet<>();

    private boolean loaded = false;

    public MysqlDatabaseNative(@NotNull MysqlManager<MysqlReceptor, MysqlVariable, MysqlDatabase, MysqlTable> manager, @NotNull String id) {
        this(manager, id, true);
    }
    public MysqlDatabaseNative(@NotNull MysqlManager<MysqlReceptor, MysqlVariable, MysqlDatabase, MysqlTable> manager, @NotNull String id, boolean autoLoad) {
        this.manager = manager;

        if (!matches("^[a-zA-Z_][a-zA-Z0-9_]{0,63}$", id)) {
            throw new IllegalArgumentException("The native mysql database id must follow the regex '^[a-zA-Z_][a-zA-Z0-9_]{0,63}$'");
        }

        this.id = id;

        if (autoLoad) {
            load();
        }
    }

    @Override
    public void load() {
        getManager().load(this);
        loaded = true;
    }

    @Override
    public void unload() {
        for (SqlTable table : new LinkedHashSet<>(getLoadedTables())) {
            table.unload();
        }

        getManager().unload(this);
        loaded = false;
    }

    @Override
    public void delete() {
        unload();
        getManager().delete(this);
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }

    @Override
    public @NotNull MysqlConnection getConnection() {
        return getManager().getConnection();
    }

    @Override
    public @NotNull Set<SqlTable> getLoadedTables() {
        return tables;
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull MysqlManager<MysqlReceptor, MysqlVariable, MysqlDatabase, MysqlTable> getManager() {
        return manager;
    }


}
