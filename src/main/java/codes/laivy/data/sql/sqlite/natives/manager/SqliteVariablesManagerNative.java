package codes.laivy.data.sql.sqlite.natives.manager;

import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainerImpl;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteException;

import java.sql.SQLType;
import java.util.LinkedList;

/**
 * @author Laivy
 * @since 1.0
 */
public class SqliteVariablesManagerNative implements SqlVariablesManager<SqliteVariable> {

    public SqliteVariablesManagerNative() {
    }

    @Override
    public void setType(@NotNull SqlVariable variable, @NotNull SQLType type) {
    }

    @Override
    public void delete(@NotNull SqliteVariable variable) {
        SqliteResultStatement statement = variable.getDatabase().getConnection().createStatement("ALTER TABLE '" + variable.getTable().getId() + "' DROP COLUMN '" + variable.getId() + "'");
        statement.execute();
        statement.close();
    }

    @Override
    public void load(@NotNull SqliteVariable variable) {
        try {
            SqliteResultStatement statement = variable.getDatabase().getConnection().createStatement("ALTER TABLE '" + variable.getTable().getId() + "' ADD COLUMN '" + variable.getId() + "' " + variable.getType().getSqlType().getName() + ";");
            //variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
            statement.execute();
            statement.close();
        } catch (RuntimeException e) {
            if (!(e.getCause() instanceof SQLiteException && e.getCause().getMessage().contains("duplicate column name:"))) {
                throw e;
            }
        }

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
    public void unload(@NotNull SqliteVariable variable) {
        // TODO: 01/03/2023 Variable unloading system
    }

    @Override
    public boolean isLoaded(@NotNull SqliteVariable variable) {
        return variable.isLoaded();
    }
}
