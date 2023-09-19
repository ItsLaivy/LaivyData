package codes.laivy.data.sql.mysql.natives.manager;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import codes.laivy.data.sql.utils.SqlErrorUtils;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainer;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainerImpl;
import codes.laivy.data.sql.variable.container.SqlInactiveVariableContainerImpl;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLSyntaxErrorException;
import java.sql.SQLType;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Optional;

/**
 * @author Laivy
 * @since 1.0
 */
public class MysqlVariablesManagerNative implements SqlVariablesManager<MysqlVariable> {

    public MysqlVariablesManagerNative() {
    }

    @Override
    public void setType(@NotNull SqlVariable variable, @NotNull SQLType type) {
        MysqlResultStatement statement = (MysqlResultStatement) variable.getDatabase().getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` MODIFY COLUMN `" + variable.getId() + "` " + type.getName() + " DEFAULT ?;");
        variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
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
        MysqlResultStatement statement = variable.getDatabase().getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` ADD COLUMN `" + variable.getId() + "` " + variable.getType().getSqlType().getName() + " DEFAULT ?;");
        variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
        try {
            statement.execute();
            variable.setNew(true);
        } catch (Throwable e) {
            if (SqlErrorUtils.t(e, 1060)) {
                variable.setNew(false);
            }
        }
        statement.close();

        variable.getType().configure(variable);

        // Load inactive containers
        for (SqlReceptor receptor : variable.getTable().getLoadedReceptors()) {
            @NotNull Optional<InactiveVariableContainer> containerOptional = receptor.getInactiveContainers().stream().filter(i -> i.getVariable().equalsIgnoreCase(variable.getId())).findFirst();
            if (containerOptional.isPresent()) {
                @NotNull InactiveVariableContainer container = containerOptional.get();
                receptor.getInactiveContainers().remove(container);
                receptor.getActiveContainers().add(new SqlActiveVariableContainerImpl(variable, receptor, variable.getType().get(container.get())));
            } else {
                receptor.getActiveContainers().add(new SqlActiveVariableContainerImpl(variable, receptor, variable.getDefault()));
            }
        }
    }

    @Override
    public void unload(@NotNull MysqlVariable variable) {
        // Unload active containers
        for (SqlReceptor receptor : variable.getTable().getLoadedReceptors()) {
            for (ActiveVariableContainer container : new LinkedList<>(receptor.getActiveContainers())) {
                if (container instanceof SqlActiveVariableContainer) {
                    SqlActiveVariableContainer sqlContainer = (SqlActiveVariableContainer) container;

                    if (Objects.equals(sqlContainer.getVariable(), variable)) {
                        receptor.getActiveContainers().remove(container);
                        receptor.getInactiveContainers().add(new SqlInactiveVariableContainerImpl(variable.getId(), receptor, sqlContainer.get()));
                    }
                }
            }
        }
    }

    @Override
    public boolean isLoaded(@NotNull MysqlVariable variable) {
        return variable.isLoaded();
    }
}
