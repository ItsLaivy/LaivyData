package codes.laivy.data.sql.sqlite.natives.manager;

import codes.laivy.data.api.receptor.Receptor;
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
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

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
        } catch (Throwable e) {
            if (!(e.getCause() instanceof SQLiteException && e.getCause().getMessage().contains("duplicate column name:"))) {
                throw e;
            }
        }

        variable.getType().configure(variable);

        // Load inactive containers
        Set<Receptor> nonAddedReceptors = new LinkedHashSet<>(variable.getTable().getLoadedReceptors());
        for (SqlReceptor receptor : variable.getTable().getLoadedReceptors()) {
            for (InactiveVariableContainer container : new LinkedList<>(receptor.getInactiveContainers())) {
                if (container.getVariable().equals(variable.getId())) {
                    receptor.getInactiveContainers().remove(container);
                    receptor.getActiveContainers().add(new SqlActiveVariableContainerImpl(variable, receptor, variable.getType().get(container.get())));
                    nonAddedReceptors.remove(receptor);
                }
            }
        }

        for (Receptor receptor : nonAddedReceptors) {
            receptor.getActiveContainers().add(new SqlActiveVariableContainerImpl(variable, (SqlReceptor) receptor, variable.getDefault()));
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
