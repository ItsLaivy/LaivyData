package codes.laivy.data.sql.sqlite.natives.manager;

import codes.laivy.data.sql.manager.SqlTablesManager;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import org.jetbrains.annotations.NotNull;

/**
 * @author Laivy
 * @since 1.0
 */
public class SqliteTablesManagerNative implements SqlTablesManager<SqliteTable> {

    public SqliteTablesManagerNative() {
    }

    @Override
    public void load(@NotNull SqliteTable table) {
        SqliteResultStatement statement = table.getDatabase().getConnection().createStatement("CREATE TABLE IF NOT EXISTS \"" + table.getId() + "\" (\"index\" INTEGER PRIMARY KEY AUTOINCREMENT, \"id\" VARCHAR(128));");
        statement.execute();
        statement.close();
    }

    @Override
    public void unload(@NotNull SqliteTable table) {
    }

    @Override
    public void delete(@NotNull SqliteTable table) {
        SqliteResultStatement statement = table.getDatabase().getConnection().createStatement("DROP TABLE \"" + table.getId() + "\"");
        statement.execute();
        statement.close();
    }

    @Override
    public boolean isLoaded(@NotNull SqliteTable table) {
        return table.isLoaded();
    }
}
