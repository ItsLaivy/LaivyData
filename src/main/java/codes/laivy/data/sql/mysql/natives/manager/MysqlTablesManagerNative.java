package codes.laivy.data.sql.mysql.natives.manager;

import codes.laivy.data.sql.manager.SqlTablesManager;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.jetbrains.annotations.NotNull;

public class MysqlTablesManagerNative implements SqlTablesManager<MysqlTable> {

    public MysqlTablesManagerNative() {
    }

    @Override
    public void load(@NotNull MysqlTable table) {
        MysqlResultStatement statement = table.getDatabase().getConnection().createStatement("CREATE TABLE IF NOT EXISTS `" + table.getDatabase().getId() + "`.`" + table.getId() + "` (`index` INT AUTO_INCREMENT PRIMARY KEY, `id` VARCHAR(128));");
        statement.execute();
        statement.close();
    }

    @Override
    public void unload(@NotNull MysqlTable table) {
    }

    @Override
    public void delete(@NotNull MysqlTable table) {
        MysqlResultStatement statement = table.getDatabase().getConnection().createStatement("DROP TABLE `" + table.getDatabase().getId() + "`.`" + table.getId() + "`");
        statement.execute();
        statement.close();
    }

    @Override
    public boolean isLoaded(@NotNull MysqlTable table) {
        return table.isLoaded();
    }
}
