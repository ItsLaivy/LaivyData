package codes.laivy.data.sqlite;

import codes.laivy.data.Main;
import codes.laivy.data.sqlite.table.SqliteTable;
import codes.laivy.data.sqlite.utils.Condition;
import codes.laivy.data.sqlite.utils.SqlUtils;
import codes.laivy.data.sqlite.variable.Parameter;
import codes.laivy.data.sqlite.variable.SqliteVariable;
import codes.laivy.data.sqlite.variable.type.Type;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class SqliteDataCache {

    // Static initializers

    public static <T> @UnknownNullability CompletableFuture<T> get(@NotNull SqliteVariable<T> variable, int row) {
        @NotNull SqliteTable table = variable.getTable();
        @Nullable Connection connection = table.getDatabase().getConnection();

        if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        }

        @NotNull CompletableFuture<T> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!variable.exists().join()) {
                    throw new IllegalStateException("This variable doesn't exists");
                }

                for (SqliteData data : variable.getTable().getDataContent().stream().filter(data -> data.isLoaded() && data.getRow() == row).collect(Collectors.toList())) {
                    future.complete(data.get(variable));
                    return;
                }

                try (PreparedStatement statement = connection.prepareStatement("SELECT `" + variable.getId() + "` FROM `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` WHERE `row` = " + row)) {
                    @NotNull ResultSet set = statement.executeQuery();

                    if (set.next()) {
                        future.complete(variable.getType().get(set.getObject(1)));
                    } else {
                        throw new IllegalStateException("There's no data with row '" + row + "' to retrieve");
                    }
                }
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public static @NotNull SqliteDataCache copy(@NotNull SqliteData data) {
        if (!data.isLoaded()) {
            throw new IllegalStateException("To create a data cache copy the data needs to be loaded");
        }

        Map<String, Object> map = new HashMap<>();

        for (Map.Entry<SqliteVariable<?>, Object> entry : data.getData().entrySet()) {
            map.put(entry.getKey().getId().toLowerCase(), entry.getValue());
        }

        return new SqliteDataCache(data.getTable(), data.getRow(), map);
    }

    // TODO: 08/11/2023 Add a retrieve method that limits the columns

    public static @NotNull CompletableFuture<@Nullable SqliteDataCache> retrieve(@NotNull SqliteTable table, int row) {
        @Nullable Connection connection = table.getDatabase().getConnection();
        @NotNull CompletableFuture<SqliteDataCache> future = new CompletableFuture<>();

        if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        }

        CompletableFuture.runAsync(() -> {
            try {
                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` WHERE `row` = " + row)) {
                    @NotNull Map<String, Object> datas = new HashMap<>();
                    @NotNull ResultSet set = statement.executeQuery();

                    if (set.next()) {
                        for (int columnRow = 2; columnRow <= set.getMetaData().getColumnCount(); columnRow++) {
                            @NotNull String columnName = set.getMetaData().getColumnName(columnRow);
                            @Nullable Object object = set.getObject(columnRow);

                            datas.put(columnName, object);
                        }

                        future.complete(new SqliteDataCache(table, row, datas));
                    } else {
                        future.complete(null);
                    }
                }
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }
    public static @NotNull CompletableFuture<SqliteDataCache[]> retrieve(@NotNull SqliteTable table, @NotNull Condition<?> @NotNull ... conditions) {
        @Nullable Connection connection = table.getDatabase().getConnection();
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
            final @NotNull CompletableFuture<SqliteDataCache[]> future = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                try {
                    @NotNull Set<Integer> excluded = new HashSet<>();
                    @NotNull Map<Integer, Map<String, Object>> datas = new HashMap<>();

                    for (SqliteData data : table.getDataContent()) {
                        if (data.isLoaded()) {
                            excluded.add(data.getRow());

                            if (data.matches(finalConditions)) {
                                for (Map.Entry<SqliteVariable<?>, Object> entry : data.getData().entrySet()) {
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
                            int row = set.getInt("row");

                            if (!datas.containsKey(row)) {
                                datas.put(row, new HashMap<>());

                                for (int columnRow = 2; columnRow <= set.getMetaData().getColumnCount(); columnRow++) {
                                    @NotNull String columnName = set.getMetaData().getColumnName(columnRow);
                                    @Nullable Object object = set.getObject(columnRow);

                                    datas.get(row).put(columnName, object);
                                }
                            }
                        }
                    }

                    @NotNull Set<SqliteDataCache> caches = new HashSet<>();
                    for (Map.Entry<Integer, Map<String, Object>> entry : datas.entrySet()) {
                        caches.add(new SqliteDataCache(table, entry.getKey(), entry.getValue()));
                    }
                    future.complete(caches.toArray(new SqliteDataCache[0]));
                } catch (@NotNull Throwable throwable) {
                    future.completeExceptionally(throwable);
                }
            }, Main.getExecutor(SqliteData.class));

            return future;
        }
    }
    public static @NotNull CompletableFuture<SqliteDataCache[]> retrieve(@NotNull SqliteTable table) {
        @Nullable Connection connection = table.getDatabase().getConnection();

        if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        } else {
            final @NotNull CompletableFuture<SqliteDataCache[]> future = new CompletableFuture<>();

            CompletableFuture.runAsync(() -> {
                try {
                    @NotNull Map<Integer, Map<String, Object>> datas = new HashMap<>();

                    for (SqliteData data : table.getDataContent()) {
                        if (data.isLoaded()) {
                            datas.put(data.getRow(), data.getCache());
                        }
                    }

                    // Retrieving on database

                    try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` WHERE " + SqlUtils.rowNotIn(datas.keySet()))) {
                        @NotNull ResultSet set = statement.executeQuery();
                        while (set.next()) {
                            int row = set.getInt("row");

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

                    @NotNull Set<SqliteDataCache> caches = new HashSet<>();
                    for (Map.Entry<Integer, Map<String, Object>> entry : datas.entrySet()) {
                        caches.add(new SqliteDataCache(table, entry.getKey(), entry.getValue()));
                    }
                    future.complete(caches.toArray(new SqliteDataCache[0]));
                } catch (@NotNull Throwable throwable) {
                    future.completeExceptionally(throwable);
                }
            }, Main.getExecutor(SqliteData.class));

            return future;
        }
    }

    // Object

    private final @NotNull SqliteTable table;
    private final int row;

    private final @NotNull Map<@NotNull String, @Nullable Object> data;

    SqliteDataCache(@NotNull SqliteTable table, int row, @NotNull Map<@NotNull String, @Nullable Object> data) {
        this.table = table;
        this.row = row;
        this.data = new HashMap<>();

        for (Map.Entry<@NotNull String, @Nullable Object> entry : data.entrySet()) {
            this.data.put(entry.getKey().toLowerCase(), entry.getValue());
        }
    }

    public @NotNull SqliteTable getTable() {
        return table;
    }

    public int getRow() {
        return row;
    }

    @Contract(pure = true)
    public @NotNull Map<String, Object> getData() {
        return new HashMap<>(data);
    }

    public <T> @UnknownNullability T get(@NotNull SqliteVariable<T> variable) {
        if (!getTable().getVariables().contains(variable)) {
            throw new IllegalStateException("The table of that data doesn't contains that variable");
        } else if (!getData().containsKey(variable.getId().toLowerCase())) {
            throw new IllegalStateException("This data cache doesn't have information about the variable '" + variable.getId() + "'");
        } else {
            return variable.getType().get(getData().get(variable.getId().toLowerCase()));
        }
    }
    public <T> @NotNull CompletableFuture<Void> set(@NotNull SqliteVariable<T> variable, @UnknownNullability T value) throws SQLException {
        return SqliteData.set(variable, value, getRow());
    }

}
