package codes.laivy.data.sql.mysql.natives.manager;

import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.manager.SqlReceptorsManager;
import codes.laivy.data.sql.manager.SqlTablesManager;
import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.MysqlTable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.mysql.natives.MysqlConnectionNative;
import codes.laivy.data.sql.mysql.natives.MysqlReceptorNative;
import codes.laivy.data.sql.mysql.MysqlManager;
import codes.laivy.data.sql.mysql.values.MysqlResultData;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 *     The native MySQL Manager of LaivyData.
 *     You can use this as an example if you will create your own ;)
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class MysqlManagerNative implements MysqlManager<MysqlReceptor, MysqlVariable, MysqlDatabase, MysqlTable> {

    private final @NotNull MysqlConnection connection;

    protected @NotNull SqlReceptorsManager<MysqlReceptor> receptorsManager;
    protected @NotNull SqlTablesManager<MysqlTable> tablesManager;
    protected @NotNull SqlVariablesManager<MysqlVariable> variablesManager;

    public MysqlManagerNative(@NotNull String address, @NotNull String user, @NotNull String password, int port) throws SQLException {
        this(new MysqlConnectionNative(DriverManager.getConnection("jdbc:mysql://" + address + ":" + port + "/?autoReconnect=true&failOverReadOnly=false&verifyServerCertificate=false", user, password)));
    }
    public MysqlManagerNative(@NotNull MysqlConnection connection) {
        this.connection = connection;

        this.receptorsManager = new MysqlReceptorsManagerNative();
        this.variablesManager = new MysqlVariablesManagerNative();
        this.tablesManager = new MysqlTablesManagerNative();
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
    public @NotNull MysqlReceptor[] getStored(@NotNull MysqlDatabase database) {
        Set<MysqlReceptor> receptors = new LinkedHashSet<>();
        for (SqlTable table : database.getLoadedTables()) {
            MysqlResultStatement statement = database.getConnection().createStatement("SELECT `id` FROM `" + database.getId() + "`.`" + table.getId() + "`");
            MysqlResultData query = statement.execute();
            statement.close();

            if (query == null) {
                throw new NullPointerException("Couldn't get query results");
            }

            Set<Map<String, Object>> data = query.getValues();
            query.close();

            f1:
            for (Map<String, Object> map : data) {
                //noinspection PatternValidation
                @Subst("receptor id") @Pattern("^.{0,128}$") String receptorId =  (String) map.get("id");

                if (!receptorId.matches("^.{0,128}$")) {
                    throw new IllegalArgumentException("The receptor id must follow the regex '^.{0,128}$'");
                }

                for (SqlReceptor receptor : table.getLoadedReceptors()) {
                    if (receptor.getId().equals(receptorId)) {
                        receptors.add((MysqlReceptor) receptor);
                        continue f1;
                    }
                }
                receptors.add(new MysqlReceptorNative((MysqlTable) table, receptorId));
            }
        }
        return receptors.toArray(new MysqlReceptor[0]);
    }

    @Override
    public void load(@NotNull MysqlDatabase database) {
        getConnection().createStatement("CREATE DATABASE IF NOT EXISTS `" + database.getId() + "`").execute();
    }

    @Override
    public void unload(@NotNull MysqlDatabase database) {
        database.getLoadedTables().forEach(SqlTable::unload);
    }

    @Override
    public void delete(@NotNull MysqlDatabase database) {
        unload(database);
        getConnection().createStatement("DROP DATABASE `" + database.getId() + "`").execute();
    }

    @Override
    public boolean isLoaded(@NotNull MysqlDatabase database) {
        return database.isLoaded();
    }

    @Override
    public @NotNull SqlVariablesManager<MysqlVariable> getVariablesManager() {
        return variablesManager;
    }

    @Override
    public @NotNull SqlReceptorsManager<MysqlReceptor> getReceptorsManager() {
        return receptorsManager;
    }

    @Override
    public @NotNull SqlTablesManager<MysqlTable> getTablesManager() {
        return tablesManager;
    }

}
