package codes.laivy.data.sql.mysql.natives.manager;

import codes.laivy.data.sql.manager.SqlTablesManager;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.jetbrains.annotations.NotNull;

public class MysqlTablesManagerNative implements SqlTablesManager<MysqlTable> {

    private final @NotNull MysqlConnection connection;

    public MysqlTablesManagerNative(@NotNull MysqlConnection connection) {
        this.connection = connection;
    }

    public @NotNull MysqlConnection getConnection() {
        return connection;
    }

    @Override
    public void load(MysqlTable table) {
        MysqlResultStatement statement = getConnection().createStatement("CREATE TABLE IF NOT EXISTS `" + table.getDatabase().getId() + "`.`" + table.getId() + "` (`index` INT AUTO_INCREMENT PRIMARY KEY, `id` VARCHAR(128));");
        statement.execute();
        statement.close();
    }

    @Override
    public void unload(MysqlTable table) {
    }

    @Override
    public void delete(MysqlTable table) {
        MysqlResultStatement statement = getConnection().createStatement("DROP TABLE `" + table.getDatabase().getId() + "`.`" + table.getId() + "`");
        statement.execute();
        statement.close();
    }

    @Override
    public boolean isLoaded(MysqlTable table) {
        return table.isLoaded();
    }
}
