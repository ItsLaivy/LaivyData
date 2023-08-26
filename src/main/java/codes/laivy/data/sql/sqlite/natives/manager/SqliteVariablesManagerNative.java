package codes.laivy.data.sql.sqlite.natives.manager;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.InactiveVariableContainer;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.sqlite.SqliteVariable;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainer;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainerImpl;
import codes.laivy.data.sql.variable.container.SqlInactiveVariableContainerImpl;
import org.jetbrains.annotations.NotNull;
import org.sqlite.SQLiteException;

import java.sql.SQLType;
import java.util.*;

/**
 * @author Laivy
 * @since 1.0
 */
public class SqliteVariablesManagerNative implements SqlVariablesManager<SqliteVariable> {

    public SqliteVariablesManagerNative() {
    }

    @Override
    public void setType(@NotNull SqlVariable variable, @NotNull SQLType type) {
        // Sqlite doesn't support this.
    }

    @Override
    public void delete(@NotNull SqliteVariable variable) {
        SqliteResultStatement statement = variable.getDatabase().getConnection().createStatement("ALTER TABLE \"" + variable.getTable().getId() + "\" DROP COLUMN \"" + variable.getId() + "\"");
        statement.execute();
        statement.close();
    }

    @Override
    public void load(@NotNull SqliteVariable variable) {
        try {
            SqliteResultStatement statement = variable.getDatabase().getConnection().createStatement("ALTER TABLE \"" + variable.getTable().getId() + "\" ADD COLUMN \"" + variable.getId() + "\" " + variable.getType().getSqlType().getName() + " default_scheme '" + variable.getDefault() + "';");
            statement.execute();
            statement.close();

            variable.setNew(true);
        } catch (Throwable e) {
            if (!(e.getCause() instanceof SQLiteException && e.getCause().getMessage().contains("duplicate column name:"))) {
                throw e;
            } else {
                variable.setNew(false);
            }
        }

        variable.getType().configure(variable);

        // Load inactive containers
        for (SqlReceptor receptor : variable.getTable().getLoadedReceptors()) {
            @NotNull Optional<InactiveVariableContainer> containerOptional = receptor.getInactiveContainers().stream().filter(i -> i.getVariable().equals(variable.getId())).findFirst();
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
    public void unload(@NotNull SqliteVariable variable) {
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
    public boolean isLoaded(@NotNull SqliteVariable variable) {
        return variable.isLoaded();
    }
}
