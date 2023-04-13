package codes.laivy.data.sql.sqlite.natives.manager;

import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.manager.SqlReceptorsManager;
import codes.laivy.data.sql.manager.SqlTablesManager;
import codes.laivy.data.sql.manager.SqlVariablesManager;
import codes.laivy.data.sql.sqlite.*;
import codes.laivy.data.sql.sqlite.natives.SqliteReceptorNative;
import codes.laivy.data.sql.sqlite.values.SqliteResultData;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *     The native SQLite Manager of LaivyData.
 *     You can use this as an example if you will create your own ;)
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqliteManagerNative implements SqliteManager<SqliteReceptor, SqliteVariable, SqliteDatabase, SqliteTable> {

    private final @NotNull File path;

    protected @NotNull SqlReceptorsManager<SqliteReceptor> receptorsManager;
    protected @NotNull SqlTablesManager<SqliteTable> tablesManager;
    protected @NotNull SqlVariablesManager<SqliteVariable> variablesManager;

    public SqliteManagerNative(@NotNull File path) {
        this.path = path;

        this.receptorsManager = new SqliteReceptorsManagerNative();
        this.variablesManager = new SqliteVariablesManagerNative();
        this.tablesManager = new SqliteTablesManagerNative();
    }

    public @NotNull File getPath() {
        return path;
    }

    @Override
    public @NotNull String getName() {
        return "LaivyData native - SQLite 1.0";
    }

    @Override
    public @NotNull SqliteReceptor[] getStored(@NotNull SqliteDatabase database) {
        Set<SqliteReceptor> receptors = new LinkedHashSet<>();
        for (SqlTable table : database.getLoadedTables()) {
            receptors.addAll(Arrays.asList(getStored((SqliteDatabase) table)));
        }
        return receptors.toArray(new SqliteReceptor[0]);
    }

    @Override
    public @NotNull SqliteReceptor[] getStored(@NotNull SqliteTable table) {
        Set<SqliteReceptor> receptors = new LinkedHashSet<>();

        SqliteResultStatement statement = table.getDatabase().getConnection().createStatement("SELECT `id` FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "`");
        SqliteResultData query = statement.execute();
        statement.close();

        if (query == null) {
            throw new NullPointerException("Couldn't get query results");
        }

        Set<Map<String, Object>> data = query.getValues();
        query.close();

        f1:
        for (Map<String, Object> map : data) {
            String receptorId = (String) map.get("id");

            if (!receptorId.matches("^.{0,128}$")) {
                throw new IllegalArgumentException("The receptor id must follow the regex '^.{0,128}$'");
            }

            for (SqlReceptor receptor : table.getLoadedReceptors()) {
                if (receptor.getId().equals(receptorId)) {
                    receptors.add((SqliteReceptor) receptor);
                    continue f1;
                }
            }
            receptors.add(new SqliteReceptorNative(table, receptorId));
        }

        return receptors.toArray(new SqliteReceptor[0]);
    }

    @Override
    public void load(@NotNull SqliteDatabase database) {
    }

    @Override
    public void unload(@NotNull SqliteDatabase database) {
        database.getLoadedTables().forEach(SqlTable::unload);
    }

    @Override
    public void delete(@NotNull SqliteDatabase database) {
        unload(database);
    }

    @Override
    public boolean isLoaded(@NotNull SqliteDatabase database) {
        return database.isLoaded();
    }

    @Override
    public @NotNull SqlVariablesManager<SqliteVariable> getVariablesManager() {
        return variablesManager;
    }

    @Override
    public @NotNull SqlReceptorsManager<SqliteReceptor> getReceptorsManager() {
        return receptorsManager;
    }

    @Override
    public @NotNull SqlTablesManager<SqliteTable> getTablesManager() {
        return tablesManager;
    }

}
