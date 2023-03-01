package codes.laivy.data.sql.natives.mysql.types;

import codes.laivy.data.api.values.ResultData;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.ActiveVariableContainerImpl;
import codes.laivy.data.api.variable.container.InactiveVariableContainerImpl;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlReceptorsManager;
import codes.laivy.data.sql.manager.SqlTablesManager;
import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.natives.mysql.MysqlDatabase;
import codes.laivy.data.sql.natives.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.natives.mysql.connection.MysqlConnectionImpl;
import codes.laivy.data.sql.natives.mysql.values.data.MysqlResultData;
import codes.laivy.data.sql.natives.mysql.values.statement.MysqlResultStatement;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLType;
import java.util.*;

/**
 * The native MySQL Manager of LaivyData.
 * You can use this as a example if you will create your own ;)
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
@ApiStatus.Internal
public class MysqlManagerImpl implements MysqlManager<SqlReceptor, SqlVariable, MysqlDatabase, SqlTable> {

    private final @NotNull MysqlConnection connection;
    private final @NotNull Set<MysqlDatabase> loadedDatabases = new LinkedHashSet<>();

    public MysqlManagerImpl(@NotNull String address, @NotNull String user, @NotNull String password, int port) throws SQLException {
        this(new MysqlConnectionImpl(DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/?autoReconnect=true&failOverReadOnly=false&verifyServerCertificate=false", user, password)));
    }
    public MysqlManagerImpl(@NotNull MysqlConnectionImpl connection) {
        this.connection = connection;
    }

    @Override
    public @NotNull MysqlConnection getConnection() {
        return connection;
    }

    @Override
    @Pattern("^.+")
    public @NotNull String getName() {
        return "LaivyData native - MySQL 1.0";
    }

    @Override
    public @NotNull SqlReceptor[] getStored(@NotNull MysqlDatabase database) {
        Set<SqlReceptor> receptors = new LinkedHashSet<>();
        for (SqlTable table : database.getTables()) {
            MysqlResultData query = database.getConnection().createStatement("SELECT `id` FROM `" + database.getId() + "`.`" + table.getId() + "`").execute();
            if (query == null) {
                throw new NullPointerException("Couldn't get query results");
            }

            Set<Map<String, Object>> data = query.getValues();
            query.close();

            f1:
            for (Map<String, Object> map : data) {
                String receptorId = (String) map.get("id");

                for (SqlReceptor receptor : table.getReceptors()) {
                    if (receptor.getId().equals(receptorId)) {
                        receptors.add(receptor);
                        continue f1;
                    }
                }
                receptors.add(new SqlReceptor(table, receptorId));
            }
        }
        return receptors.toArray(new SqlReceptor[0]);
    }

    @Override
    public void load(@NotNull MysqlDatabase database) {
        getConnection().createStatement("CREATE DATABASE IF NOT EXISTS `" + database.getId() + "`").execute();
        loadedDatabases.add(database);
    }

    @Override
    public void unload(@NotNull MysqlDatabase database) {
        database.getTables().forEach(SqlTable::unload);
        loadedDatabases.remove(database);
    }

    @Override
    public void delete(@NotNull MysqlDatabase database) {
        unload(database);
        getConnection().createStatement("DROP DATABASE `" + database.getId() + "`").execute();
    }

    @Override
    public boolean isLoaded(@NotNull MysqlDatabase database) {
        return loadedDatabases.contains(database);
    }

    @Override
    public @NotNull SqlVariablesManager<SqlVariable> getVariablesManager() {
        return new SqlVariablesManager<SqlVariable>() {
            @Override
            public void setType(@NotNull SqlVariable variable, @NotNull SQLType type) {
                getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` MODIFY `" + variable.getId() + "` " + type.getVendor() + ";").execute();
            }

            @Override
            public void delete(@NotNull SqlVariable variable) {
                getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` DROP COLUMN `" + variable.getId() + "`").execute();
            }

            @Override
            public void load(SqlVariable variable) {
                MysqlResultStatement statement = getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` ADD COLUMN IF NOT EXISTS `" + variable.getId() + "` " + variable.getType().getSqlType().getName() + " DEFAULT ?;");
                variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
                statement.execute();

                statement = getConnection().createStatement("ALTER TABLE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` MODIFY `" + variable.getId() + "` " + variable.getType().getSqlType().getName() + " DEFAULT ?;");
                variable.getType().set(variable.getDefault(), statement.getParameters(0), statement.getMetaData());
                statement.execute();
            }

            @Override
            public void unload(SqlVariable variable) {
                // TODO: 01/03/2023 Variable unloading system
            }

            @Override
            public boolean isLoaded(SqlVariable variable) {
                return variable.isLoaded();
            }
        };
    }

    @Override
    public @NotNull SqlReceptorsManager<SqlReceptor> getReceptorsManager() {
        return new SqlReceptorsManager<SqlReceptor>() {
            @Override
            public @Nullable ResultData getData(@NotNull SqlReceptor receptor) {
                MysqlResultData query = getConnection().createStatement("SELECT * FROM `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` WHERE id = '" + receptor.getId() + "'").execute();

                if (query == null) {
                    throw new NullPointerException("This result data doesn't have results");
                }

                @NotNull Set<Map<String, Object>> results = query.getValues();
                query.close();

                if (results.isEmpty()) {
                    return null;
                } else if (results.size() == 1) {
                    return query;
                } else {
                    throw new UnsupportedOperationException("Multiples receptors with same id '" + receptor.getId() + "' founded inside table '" + receptor.getTable().getId() + "' at database '" + receptor.getDatabase().getId() + "'");
                }
            }

            @Override
            public void unload(@NotNull SqlReceptor receptor, boolean save) {
                if (save) {
                    save(receptor);
                }
            }

            @Override
            public void save(@NotNull SqlReceptor receptor) {
                StringBuilder query = new StringBuilder();

                Map<Integer, ActiveVariableContainer> indexVariables = new LinkedHashMap<>();

                int row = 0;
                for (ActiveVariableContainer activeVar : receptor.getActiveContainers()) {
                    if (row != 0) query.append(",");
                    query.append("``=?");
                    indexVariables.put(row, activeVar);
                    row++;
                }

                MysqlResultStatement statement = getConnection().createStatement("UPDATE `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` SET " + query + " WHERE id = '" + receptor.getId() + "'");
                for (Map.Entry<Integer, ActiveVariableContainer> map : indexVariables.entrySet()) {
                    ((SqlVariable) map.getValue().getVariable()).getType().set(
                            map.getValue().get(),
                            statement.getParameters(map.getKey()),
                            statement.getMetaData()
                    );
                }
                statement.execute();
            }

            @Override
            public void delete(@NotNull SqlReceptor receptor) {
                getConnection().createStatement("DELETE FROM `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` WHERE id = '" + receptor.getId() + "'");
            }

            @Override
            public void load(SqlReceptor receptor) {
                @Nullable ResultData result = getData(receptor);
                Map<String, Object> data = new LinkedHashMap<>();
                if (result != null) {
                    data = new LinkedList<>(result.getValues()).getFirst();
                }

                receptor.setNew(data.isEmpty());

                if (data.isEmpty()) {
                    getConnection().createStatement("INSERT INTO `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` (id) VALUES ('" + receptor.getId() + "')").execute();

                    result = getData(receptor);
                    if (result != null) {
                        data = new LinkedList<>(result.getValues()).getFirst();
                    } else {
                        throw new NullPointerException("Couldn't create receptor '" + receptor.getId() + "' due to an unknown error.");
                    }
                }

                int row = 0;
                for (Map.Entry<String, Object> map : data.entrySet()) {
                    if (map.getKey().equals("index")) {
                        receptor.setIndex((int) map.getValue());
                    } else if (row > 1) { // After index and id columns
                        SqlVariable variable = receptor.getTable().getVariable(map.getKey());
                        if (variable != null && variable.isLoaded()) {
                            receptor.getActiveContainers().add(new ActiveVariableContainerImpl(variable, receptor, variable.getType().get(map.getValue())));
                        } else {
                            receptor.getInactiveContainers().add(new InactiveVariableContainerImpl(map.getKey(), receptor, map.getValue()));
                        }
                    }
                    row++;
                }
            }

            @Override
            public void unload(SqlReceptor receptor) {
                this.unload(receptor, true);
            }

            @Override
            public boolean isLoaded(SqlReceptor object) {
                return object.isLoaded();
            }
        };
    }

    @Override
    public @NotNull SqlTablesManager<SqlTable> getTablesManager() {
        return new SqlTablesManager<SqlTable>() {
            @Override
            public void load(SqlTable table) {
                getConnection().createStatement("CREATE TABLE IF NOT EXISTS `" + table.getDatabase().getId() + "`.`" + table.getId() + "` (`index` INT AUTO_INCREMENT PRIMARY KEY, `id` VARCHAR(128));").execute();
            }

            @Override
            public void unload(SqlTable table) {
            }

            @Override
            public void delete(SqlTable table) {
                getConnection().createStatement("DROP TABLE `" + table.getDatabase().getId() + "`.`" + table.getId() + "`").execute();
            }

            @Override
            public boolean isLoaded(SqlTable table) {
                return false;
            }
        };
    }

}
