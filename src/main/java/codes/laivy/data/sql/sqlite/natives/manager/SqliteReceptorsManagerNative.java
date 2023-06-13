package codes.laivy.data.sql.sqlite.natives.manager;

import codes.laivy.data.api.receptor.Receptor;
import codes.laivy.data.api.variable.Variable;
import codes.laivy.data.api.variable.container.ActiveVariableContainer;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.manager.SqlReceptorsManager;
import codes.laivy.data.sql.sqlite.SqliteReceptor;
import codes.laivy.data.sql.sqlite.SqliteTable;
import codes.laivy.data.sql.sqlite.natives.SqliteReceptorNative;
import codes.laivy.data.sql.sqlite.values.SqliteResultData;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainer;
import codes.laivy.data.sql.variable.container.SqlActiveVariableContainerImpl;
import codes.laivy.data.sql.variable.container.SqlInactiveVariableContainerImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Laivy
 * @since 1.0
 */
public class SqliteReceptorsManagerNative implements SqlReceptorsManager<SqliteReceptor> {

    /**
     * Contains the loaded receptors, if a receptor id is inside this map, the receptor is loaded.
     * Key = Table id, Value = Receptor id
     */
    private final @NotNull Map<String, Set<String>> loadedReceptors = new HashMap<>();

    public SqliteReceptorsManagerNative() {
    }

    @Override
    public @Nullable SqliteResultData getData(@NotNull SqliteReceptor receptor) {
        SqliteResultStatement statement = receptor.getDatabase().getConnection().createStatement("SELECT * FROM \"" + receptor.getTable().getId() + "\" WHERE \"id\" = ?");
        statement.getParameters(0).setString(receptor.getId());
        SqliteResultData query = statement.execute();
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
    public void unload(@NotNull SqliteReceptor receptor, boolean save) {
        this.unload(new SqliteReceptor[] { receptor }, save);
    }

    @Override
    public void save(@NotNull SqliteReceptor receptor) {
        save(new SqliteReceptor[] { receptor });
    }

    @Override
    public void delete(@NotNull SqliteReceptor receptor) {
        if (isLoaded(receptor)) {
            unload(receptor, false);
        }

        SqliteResultStatement statement = receptor.getDatabase().getConnection().createStatement("DELETE FROM \"" + receptor.getTable().getId() + "\" WHERE \"id\" = ?");
        statement.getParameters(0).setString(receptor.getId());
        statement.execute();
        statement.close();
    }

    @Override
    public void load(@NotNull SqliteReceptor receptor) {
        if (isLoaded(receptor)) {
            throw new IllegalStateException("The receptor '" + receptor.getId() + "' already are loaded.");
        } else if (!receptor.getTable().isLoaded()) {
            throw new IllegalStateException("This table isn't loaded!");
        }

        receptor.getActiveContainers().clear();
        receptor.getInactiveContainers().clear();

        loadData(receptor);
        receptor.getTable().getLoadedReceptors().add(receptor);
        setLoaded(receptor, true);
    }

    protected void loadData(@NotNull SqliteReceptor receptor) {
        @Nullable SqliteResultData result = getData(receptor);
        Map<String, Object> data = new LinkedHashMap<>();
        if (result != null) {
            data = new LinkedList<>(result.getValues()).getFirst();
            result.close();
        }

        receptor.setNew(data.isEmpty());

        if (data.isEmpty()) {
            // Execute
            SqliteResultStatement statement = receptor.getDatabase().getConnection().createStatement("INSERT INTO \"" + receptor.getTable().getId() + "\" (id) VALUES (?)");
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
                    receptor.getActiveContainers().add(new SqlActiveVariableContainerImpl(variable, receptor, variable.getType().get(map.getValue())));
                } else {
                    receptor.getInactiveContainers().add(new SqlInactiveVariableContainerImpl(map.getKey(), receptor, map.getValue()));
                }
            }
            row++;
        }
    }

    @Override
    public void setId(@NotNull SqliteReceptor receptor, @NotNull String id) {
        if (!id.matches("^.{0,128}$")) {
            throw new IllegalArgumentException("The receptor id must follow the regex '^.{0,128}$'");
        }

        SqliteResultData data = getData(new SqliteReceptorNative(receptor.getTable(), id));
        if (data != null) {
            data.close();
            throw new IllegalArgumentException("A receptor with that id '" + id + "' already exists on the table '" + receptor.getTable() + "'");
        }

        SqliteResultStatement statement = receptor.getDatabase().getConnection().createStatement("UPDATE \"" + receptor.getTable().getId() + "\" SET \"id\" = ? WHERE \"id\" = ?");

        statement.getParameters(0).setString(id);
        statement.getParameters(1).setString(receptor.getId());

        statement.execute();
        statement.close();
    }

    @Override
    public void save(@NotNull SqliteReceptor[] receptors) {
        if (receptors.length > 0) {
            SqliteTable table = receptors[0].getTable();
            if (!Arrays.stream(receptors).allMatch(r -> r.getTable().equals(table))) {
                throw new IllegalArgumentException("All receptors needs to have the same tables");
            }

            if (Arrays.stream(receptors).anyMatch(r -> !isLoaded(r))) {
                StringBuilder receptorIds = new StringBuilder();

                int row = 0;
                for (SqliteReceptor receptor : Arrays.stream(receptors).filter(r -> !isLoaded(r)).collect(Collectors.toSet())) {
                    if (row > 0) {
                        receptorIds.append(", ");
                    }
                    receptorIds.append("'").append(receptor.getId()).append("'");
                    row++;
                }

                throw new IllegalStateException("These receptor(s) " + receptorIds + " aren't loaded.");
            }

            LinkedHashMap<Variable, List<SqlActiveVariableContainer>> map = new LinkedHashMap<>();
            Set<SqliteReceptor> receptorsList = new HashSet<>();

            for (SqliteReceptor receptor : receptors) {
                for (ActiveVariableContainer activeVar : receptor.getActiveContainers()) {
                    if (activeVar instanceof SqlActiveVariableContainer) {
                        SqlActiveVariableContainer container = (SqlActiveVariableContainer) activeVar;
                        Variable variable = container.getVariable();

                        if (variable != null) {
                            map.putIfAbsent(variable, new LinkedList<>());
                            map.get(variable).add(container);

                            receptorsList.add(receptor);
                        } else {
                            throw new NullPointerException("The active containers of a receptor needs to have a variable!");
                        }
                    } else {
                        throw new IllegalArgumentException("This receptor contains illegal container types");
                    }
                }
            }

            if (!map.isEmpty()) {
                StringBuilder queries = new StringBuilder();
                queries.append("UPDATE `").append(table.getId()).append("` SET ");

                int varRow = 1;
                for (Map.Entry<Variable, List<SqlActiveVariableContainer>> entry : map.entrySet()) {
                    Variable variable = entry.getKey();
                    List<SqlActiveVariableContainer> list = entry.getValue();

                    queries.append("`").append(variable.getId()).append("` = CASE ");
                    for (SqlActiveVariableContainer container : list) {
                        @NotNull Receptor receptor = Objects.requireNonNull(container.getReceptor());
                        queries.append("WHEN `id`='").append(receptor.getId()).append("' THEN ? ");
                    }
                    queries.append("END");
                    if (map.size() > varRow) queries.append(", ");
                    varRow++;
                }

                StringBuilder receptorsString = new StringBuilder();
                int row = 0;
                for (Receptor receptor : receptorsList) {
                    if (row > 0) {
                        receptorsString.append(", ");
                    }
                    receptorsString.append("'").append(receptor.getId()).append("'");
                    row++;
                }
                queries.append(" WHERE `id` IN (").append(receptorsString).append(")");

                SqliteResultStatement statement = table.getDatabase().getConnection().createStatement(queries.toString());

                row = 0;
                for (List<SqlActiveVariableContainer> list : map.values()) {
                    for (SqlActiveVariableContainer container : list) {
                        container.getType().set(
                                container.get(),
                                statement.getParameters(row),
                                statement.getMetaData()
                        );
                        row++;
                    }
                }

                statement.execute();
                statement.close();
            }
        }
    }

    @Override
    public void unload(@NotNull SqliteReceptor[] receptors, boolean save) {
        if (Arrays.stream(receptors).anyMatch(r -> !isLoaded(r))) {
            StringBuilder receptorIds = new StringBuilder();

            int row = 0;
            for (SqliteReceptor receptor : Arrays.stream(receptors).filter(r -> !isLoaded(r)).collect(Collectors.toSet())) {
                if (row > 0) {
                    receptorIds.append(", ");
                }
                receptorIds.append("'").append(receptor.getId()).append("'");
                row++;
            }

            throw new IllegalStateException("These receptor(s) " + receptorIds + " aren't loaded.");
        }

        Map<SqliteTable, Set<SqliteReceptor>> separated = new HashMap<>();
        for (SqliteReceptor receptor : receptors) {
            separated.putIfAbsent(receptor.getTable(), new LinkedHashSet<>());
            separated.get(receptor.getTable()).add(receptor);
        }

        for (Map.Entry<SqliteTable, Set<SqliteReceptor>> entry : separated.entrySet()) {
            SqliteTable table = entry.getKey();
            Set<SqliteReceptor> set = entry.getValue();

            save(set.toArray(new SqliteReceptor[0]));

            table.getLoadedReceptors().removeAll(set);

            entry.getValue().forEach(r -> {
                r.getActiveContainers().clear();
                r.getInactiveContainers().clear();
                setLoaded(r, false);
            });
        }
    }

    @Override
    public void unload(@NotNull SqliteReceptor receptor) {
        this.unload(receptor, true);
    }

    @Override
    public boolean isLoaded(@NotNull SqliteReceptor receptor) {
        for (Set<String> set : loadedReceptors.values()) {
            if (set.contains(receptor.getId())) {
                return true;
            }
        }
        return false;
    }

    public void setLoaded(@NotNull SqliteReceptor receptor, boolean loaded) {
        String tableId = receptor.getTable().getId();
        loadedReceptors.putIfAbsent(tableId, new LinkedHashSet<>());

        if (loaded) {
            loadedReceptors.get(tableId).add(receptor.getId());
        } else {
            loadedReceptors.get(tableId).remove(receptor.getId());
        }
    }
}
