package codes.laivy.data.sql.mysql.natives.manager;

import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.api.variable.container.ActiveVariableContainerImpl;
import codes.laivy.data.api.variable.container.InactiveVariableContainerImpl;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlReceptorsManager;
import codes.laivy.data.sql.mysql.MysqlReceptor;
import codes.laivy.data.sql.mysql.natives.MysqlReceptorNative;
import codes.laivy.data.sql.mysql.values.MysqlResultData;
import codes.laivy.data.sql.mysql.values.MysqlResultStatement;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

@ApiStatus.Internal
public class MysqlReceptorsManagerNative implements SqlReceptorsManager<MysqlReceptor> {

    public MysqlReceptorsManagerNative() {
    }

    @Override
    public @Nullable MysqlResultData getData(@NotNull MysqlReceptor receptor) {
        MysqlResultStatement statement = receptor.getDatabase().getConnection().createStatement("SELECT * FROM `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` WHERE id = ?");
        statement.getParameters(0).setString(receptor.getId());
        MysqlResultData query = statement.execute();
        statement.close();

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
    public void unload(@NotNull MysqlReceptor receptor, boolean save) {
        if (save) {
            save(receptor);
        }
    }

    @Override
    public void save(@NotNull MysqlReceptor receptor) {
        StringBuilder query = new StringBuilder();

        Map<Integer, ActiveVariableContainer> indexVariables = new LinkedHashMap<>();

        int row = 1;
        for (ActiveVariableContainer activeVar : receptor.getActiveContainers()) {
            if (row != 1) query.append(",");
            query.append("`").append(activeVar.getVariable().getId()).append("`=?");
            indexVariables.put(row, activeVar);
            row++;
        }

        MysqlResultStatement statement = receptor.getDatabase().getConnection().createStatement("UPDATE `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` SET `index`=?," + query + " WHERE id = ?");
        statement.getParameters(0).setInt(receptor.getIndex());
        statement.getParameters(row).setString(receptor.getId());

        for (Map.Entry<Integer, ActiveVariableContainer> map : indexVariables.entrySet()) {
            ((SqlVariable) map.getValue().getVariable()).getType().set(
                    map.getValue().get(),
                    statement.getParameters(map.getKey()),
                    statement.getMetaData()
            );
        }
        statement.execute();
        statement.close();
    }

    @Override
    public void delete(@NotNull MysqlReceptor receptor) {
        MysqlResultStatement statement = receptor.getDatabase().getConnection().createStatement("DELETE FROM `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` WHERE id = ?");
        statement.getParameters(0).setString(receptor.getId());
        statement.execute();
        statement.close();
    }

    @Override
    public void load(@NotNull MysqlReceptor receptor) {
        @Nullable MysqlResultData result = getData(receptor);
        Map<String, Object> data = new LinkedHashMap<>();
        if (result != null) {
            data = new LinkedList<>(result.getValues()).getFirst();
            result.close();
        }

        receptor.setNew(data.isEmpty());

        if (data.isEmpty()) {
            // Execute
            MysqlResultStatement statement = receptor.getDatabase().getConnection().createStatement("INSERT INTO `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` (id) VALUES (?)");
            statement.getParameters(0).setString(receptor.getId());
            statement.execute();
            statement.close();
            // Data query (again)
            result = getData(receptor);
            if (result != null) {
                data = new LinkedList<>(result.getValues()).getFirst();
                result.close();
            } else {
                throw new NullPointerException("Couldn't create receptor '" + receptor.getId() + "' due to an unknown error.");
            }
        }

        int row = 0;
        for (Map.Entry<String, Object> map : data.entrySet()) {
            if (map.getKey().equals("index")) {
                receptor.setIndex((int) map.getValue());
            } else if (row > 1) { // After index and id columns
                SqlVariable variable = receptor.getTable().getLoadedVariable(map.getKey());
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
    public void setId(@NotNull MysqlReceptor receptor, @NotNull @Pattern("^.{0,128}$") @Subst("receptor id") String id) {
        if (!id.matches("^.{0,128}$")) {
            throw new IllegalArgumentException("The receptor id must follow the regex '^.{0,128}$'");
        }

        MysqlResultData data = getData(new MysqlReceptorNative(receptor.getTable(), id));
        if (data != null) {
            data.close();
            throw new IllegalArgumentException("A receptor with that id '" + id + "' already exists on the table '" + receptor.getTable() + "'");
        }

        MysqlResultStatement statement = receptor.getDatabase().getConnection().createStatement("UPDATE `" + receptor.getDatabase().getId() + "`.`" + receptor.getTable().getId() + "` SET id = ? WHERE id = ?");

        statement.getParameters(0).setString(id);
        statement.getParameters(1).setString(receptor.getId());

        statement.execute();
        statement.close();
    }

    @Override
    public void unload(@NotNull MysqlReceptor receptor) {
        this.unload(receptor, true);
    }

    @Override
    public boolean isLoaded(@NotNull MysqlReceptor receptor) {
        return receptor.isLoaded();
    }
}
