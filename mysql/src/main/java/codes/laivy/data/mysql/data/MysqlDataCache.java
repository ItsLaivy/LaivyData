package codes.laivy.data.mysql.data;

import codes.laivy.data.Main;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.Parameter;
import codes.laivy.data.mysql.variable.type.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class MysqlDataCache {

    // Static initializers

    public static @NotNull MysqlDataCache copy(@NotNull MysqlData data) {
        if (!data.isLoaded()) {
            throw new IllegalStateException("To create a data cache copy the data needs to be loaded");
        }

        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<MysqlVariable<?>, Object> entry : data.getData().entrySet()) {
            map.put(entry.getKey().getId().toLowerCase(), entry.getValue());
        }

        return new MysqlDataCache(data.getTable(), data.getRow(), map);
    }

    // TODO: 08/11/2023 Add a retrieve method that limits the columns

    public static @NotNull CompletableFuture<MysqlDataCache[]> retrieve(@NotNull MysqlTable table, @NotNull Condition<?> @NotNull ... conditions) {
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();
        final @NotNull Condition<?>[] finalConditions = Stream.of(conditions).distinct().toArray(Condition[]::new);

        if (finalConditions.length == 0) {
            throw new IllegalStateException("The conditions array cannot be empty");
        } else if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        } else if (Arrays.stream(finalConditions).anyMatch(c -> !c.getVariable().getTable().equals(table))) {
            throw new IllegalStateException("There's conditions with variables that aren't from the table '" + table.getId() + "'");
        } else if (Arrays.stream(finalConditions).anyMatch(c -> !c.getVariable().isLoaded())) {
            throw new IllegalStateException("There's conditions with variables that hasn't loaded");
        } else {
            final @NotNull CompletableFuture<MysqlDataCache[]> future = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                try {
                    @NotNull Set<Long> excluded = new HashSet<>();
                    @NotNull Map<Long, Map<String, Object>> datas = new HashMap<>();

                    for (MysqlData data : table.getDatas()) {
                        if (data.isLoaded()) {
                            excluded.add(data.getRow());

                            if (data.matches(finalConditions)) {
                                for (Map.Entry<MysqlVariable<?>, Object> entry : data.getData().entrySet()) {
                                    datas.computeIfAbsent(data.getRow(), k -> new HashMap<>()).put(entry.getKey().getId().toLowerCase(), entry.getValue());
                                }
                            }
                        }
                    }

                    // Retrieving on database

                    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` " + SqlUtils.buildWhereCondition(excluded, finalConditions))) {
                        int index = 0;
                        for (@NotNull Condition<?> condition : finalConditions) {
                            //noinspection rawtypes
                            @NotNull Type type = condition.getVariable().getType();
                            //noinspection unchecked
                            type.set(Parameter.of(statement, type.isNullSupported(), index), condition.getValue());
                            index++;
                        }

                        @NotNull ResultSet set = statement.executeQuery();
                        while (set.next()) {
                            long row = set.getInt("row");

                            if (!datas.containsKey(row)) {
                                datas.put(row, new HashMap<>());

                                for (int columnRow = 2; columnRow <= set.getMetaData().getColumnCount(); columnRow++) {
                                    @NotNull String columnName = set.getMetaData().getColumnName(columnRow);
                                    @Nullable Object object = set.getObject(columnRow);

                                    datas.get(row).put(columnName.toLowerCase(), object);
                                }
                            }
                        }
                    }

                    @NotNull Set<MysqlDataCache> caches = new HashSet<>();
                    for (Map.Entry<Long, Map<String, Object>> entry : datas.entrySet()) {
                        caches.add(new MysqlDataCache(table, entry.getKey(), entry.getValue()));
                    }
                    future.complete(caches.toArray(new MysqlDataCache[0]));
                } catch (@NotNull Throwable throwable) {
                    future.completeExceptionally(throwable);
                }
            }, Main.getExecutor(MysqlData.class));

            return future;
        }
    }

    // Object

    private final @NotNull MysqlTable table;
    private final long row;

    private final @NotNull Map<@NotNull String, @Nullable Object> data;

    MysqlDataCache(@NotNull MysqlTable table, long row, @NotNull Map<@NotNull String, @Nullable Object> data) {
        this.table = table;
        this.row = row;
        this.data = new HashMap<>(data);
    }

    public @NotNull MysqlTable getTable() {
        return table;
    }

    public long getRow() {
        return row;
    }

    @Contract(pure = true)
    public @NotNull Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    public <T> @UnknownNullability T get(@NotNull MysqlVariable<T> variable) {
        if (!getTable().getVariables().contains(variable)) {
            throw new IllegalStateException("The table of that data doesn't contains that variable");
        } else if (!getData().containsKey(variable.getId().toLowerCase())) {
            throw new IllegalStateException("This data cache doesn't have information about the variable '" + variable.getId() + "'");
        } else {
            //noinspection unchecked
            return (T) getData().get(variable.getId().toLowerCase());
        }
    }
}
