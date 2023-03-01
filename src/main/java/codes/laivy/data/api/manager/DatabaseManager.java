package codes.laivy.data.api.manager;

import codes.laivy.data.api.database.Database;
import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.table.Table;
import codes.laivy.data.api.variable.Variable;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

/**
 * The DatabaseManager is responsible for managing the connections, structure, variables, tables, and all other requirements of the system.
 * @param <R> The receptor class of this manager
 * @param <V> The variable class of this manager
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface DatabaseManager<R extends Receptor, V extends Variable, D extends Database, T extends Table> extends Manager<D> {

    /**
     * The name of the manager is useful for some metrics and debugging.
     * Example: For MySQL managers, you can use "MYSQL_MANAGER_YOUR_PROJECT_NAME" or something similar, like an ID.
     * @return the name of manager type
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @Pattern("^[a-zA-Z_]+$")
    @NotNull String getName();

    /**
     * The variables manager is responsible to manage the variables
     * @return the variables manager
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull VariablesManager<V> getVariablesManager();

    /**
     * The receptors manager is responsible to manage the receptors
     * @return the receptors manager
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull ReceptorsManager<R> getReceptorsManager();

    /**
     * The tables manager is responsible to manage the tables
     * @return the tables manager
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull TablesManager<T> getTablesManager();

    /**
     * This retrieves all stored receptors (whether unloaded or loaded) from a database.
     * @param database the database
     * @return all receptors stored on the database, if a receptor is already loaded, it will retrieve this loaded instance, if not, will create a unloaded one.
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull R[] getStored(@NotNull D database);

}
