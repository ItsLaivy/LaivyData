package codes.laivy.data.sql.mysql.natives.manager;

import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;

public class MysqlVariablesManagerNative implements SqlVariablesManager<MysqlVariable> {

    private final @NotNull MysqlConnection connection;

    public MysqlVariablesManagerNative(@NotNull MysqlConnection connection) {
        this.connection = connection;
    }

    public @NotNull MysqlConnection getConnection() {
        return connection;
    }

    @Override
    public void setType(@NotNull MysqlVariable variable, @NotNull SQLType type) {
        MysqlResultStatement statement = getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` MODIFY `" + variable.getId() + "` " + type.getVendor() + ";");
        statement.execute();
        statement.close();
    }

    @Override
    public void delete(@NotNull MysqlVariable variable) {
        MysqlResultStatement statement = getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` DROP COLUMN `" + variable.getId() + "`");
        statement.execute();
        statement.close();
    }

    @Override
    public void load(MysqlVariable variable) {
        MysqlResultStatement statement = getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` ADD COLUMN IF NOT EXISTS `" + variable.getId() + "` " + variable.getType().getSqlType().getName() + " DEFAULT ?;");
        variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
        statement.execute();
        statement.close();

        statement = getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` MODIFY `" + variable.getId() + "` " + variable.getType().getSqlType().getName() + " DEFAULT ?;");
        variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
        statement.execute();
        statement.close();
    }

    @Override
    public void unload(MysqlVariable variable) {
        // TODO: 01/03/2023 Variable unloading system
    }

    @Override
    public boolean isLoaded(MysqlVariable variable) {
        return variable.isLoaded();
    }
}
