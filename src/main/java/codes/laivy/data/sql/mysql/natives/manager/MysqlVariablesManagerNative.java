package codes.laivy.data.sql.mysql.natives.manager;

import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainerImpl;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLType;
import java.util.LinkedList;

/**
 * @author Laivy
 * @since 1.0
 */
public class MysqlVariablesManagerNative implements SqlVariablesManager<MysqlVariable> {

    public MysqlVariablesManagerNative() {
    }

    @Override
    public void setType(@NotNull SqlVariable variable, @NotNull SQLType type) {
        MysqlResultStatement statement = (MysqlResultStatement) variable.getDatabase().getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` MODIFY COLUMN `" + variable.getId() + "` " + type.getName() + ";");
        statement.execute();
        statement.close();
    }

    @Override
    public void delete(@NotNull MysqlVariable variable) {
        MysqlResultStatement statement = variable.getDatabase().getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` DROP COLUMN `" + variable.getId() + "`");
        statement.execute();
        statement.close();
    }

    @Override
    public void load(@NotNull MysqlVariable variable) {
        MysqlResultStatement statement = variable.getDatabase().getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` ADD COLUMN IF NOT EXISTS `" + variable.getId() + "` " + variable.getType().getSqlType().getName() + " DEFAULT ?;");
        variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
        statement.execute();
        statement.close();

        variable.getType().configure(variable);

        // Load inactive containers
        for (SqlReceptor receptor : variable.getTable().getLoadedReceptors()) {
            for (InactiveVariableContainer container : new LinkedList<>(receptor.getInactiveContainers())) {
                if (container.getVariable().equals(variable.getId())) {
                    receptor.getInactiveContainers().remove(container);
                    receptor.getActiveContainers().add(new SqlActiveVariableContainerImpl(variable, receptor, variable.getType().get(container.get())));
                }
            }
        }
    }

    @Override
    public void unload(@NotNull MysqlVariable variable) {
        // TODO: 01/03/2023 Variable unloading system
    }

    @Override
    public boolean isLoaded(@NotNull MysqlVariable variable) {
        return variable.isLoaded();
    }
}
