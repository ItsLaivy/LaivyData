package codes.laivy.data.mysql.data;

import codes.laivy.data.data.Data;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.Parameter;
import codes.laivy.data.mysql.variable.type.Type;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class MysqlData extends Data {

    // Static methods

    private static final @NotNull Map<@NotNull MysqlTable, @NotNull List<@NotNull MysqlData>> CACHED_LOADED_DATAS = new HashMap<>();

    public static @NotNull MysqlData retrieve(@NotNull MysqlTable table, long row) {
        if (CACHED_LOADED_DATAS.containsKey(table)) {
            @NotNull Optional<MysqlData> optional = CACHED_LOADED_DATAS.get(table).stream().filter(data -> data.getRow() == row).findFirst();

            if (optional.isPresent()) {
                return optional.get();
            }
        }

        @NotNull MysqlData data = new MysqlData(table, row);
        CACHED_LOADED_DATAS.computeIfAbsent(table, k -> new ArrayList<>()).add(data);
        return data;
    }
    public static @NotNull CompletableFuture<MysqlData[]> retrieve(@NotNull MysqlTable table, @NotNull Condition<?> @NotNull ... conditions) {
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();

        if (conditions.length == 0) {
            throw new IllegalStateException("The conditions array cannot be empty");
        } else if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().getTable().equals(table))) {
            throw new IllegalStateException("There's conditions with variables that aren't from the table '" + table.getName() + "'");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().isLoaded())) {
            throw new IllegalStateException("There's conditions with variables that hasn't loaded");
        }

        final @NotNull Condition<?>[] finalConditions = Stream.of(conditions).distinct().toArray(Condition[]::new);
        final @NotNull CompletableFuture<MysqlData[]> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @NotNull Set<Long> excluded = new HashSet<>();
                @NotNull Map<Long, MysqlData> datas = new TreeMap<>(Long::compare);

                if (CACHED_LOADED_DATAS.containsKey(table)) {

                    for (MysqlData data : CACHED_LOADED_DATAS.get(table)) {
                        if (data.isLoaded()) {
                            excluded.add(data.getRow());

                            if (data.matches(finalConditions)) {
                                datas.put(data.getRow(), data);
                            }
                        }
                    }
                }

                // Retrieving on database

                try (PreparedStatement statement = connection.prepareStatement("SELECT `row` FROM `" + table.getDatabase().getId() + "`.`" + table.getName() + "` " + SqlUtils.buildWhereCondition(excluded, finalConditions))) {
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
                        @NotNull MysqlData data = new MysqlData(table, row);
                        CACHED_LOADED_DATAS.computeIfAbsent(table, k -> new ArrayList<>()).add(data);

                        if (!datas.containsKey(row)) {
                            datas.put(row, data);
                        }
                    }
                }

                future.complete(datas.values().toArray(new MysqlData[0]));
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    // Object

    private final @NotNull Map<@NotNull MysqlVariable<?>, @Nullable Object> data = new HashMap<>();
    private final @NotNull Map<@NotNull String, @Nullable Object> cache = new HashMap<>();

    private final @NotNull MysqlTable table;
    private long row;

    private MysqlData(@NotNull MysqlTable table, long row) {
        this.table = table;
        this.row = row;
    }

    @ApiStatus.Internal
    public @NotNull Map<MysqlVariable<?>, Object> getData() {
        return data;
    }
    @ApiStatus.Internal
    public @NotNull Map<String, Object> getCache() {
        return cache;
    }

    public long getRow() {
        return row;
    }

    // TODO: 01/11/2023 setRow

    @Contract(pure = true)
    public @NotNull MysqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Contract(pure = true)
    public @NotNull MysqlTable getTable() {
        return table;
    }

    @Override
    public @Nullable Object get(@NotNull String id) {
        return null;
    }

    @Override
    public void set(@NotNull String id, @Nullable Object object) {

    }

    @Override
    protected @NotNull CompletableFuture<Void> load() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (exists().join()) {
                    try (@NotNull PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + getDatabase().getId() + "`.`" + getTable().getName() + "` WHERE `row` = " + getRow())) {
                        ResultSet set = statement.executeQuery();
                        set.next();

                        for (int row = 1; row <= set.getMetaData().getColumnCount(); row++) {
                            @NotNull String columnName = set.getMetaData().getColumnName(row);
                            @Nullable Object object = set.getObject(row);

                            @NotNull Optional<MysqlVariable<?>> variableOptional = getTable().getVariables().getById(columnName);
                            if (variableOptional.isPresent()) {
                                data.put(variableOptional.get(), object);
                            } else {
                                cache.put(columnName, object);
                            }
                        }

                        if (set.next()) {
                            throw new IllegalStateException("Multiples datas with the same id '" + getRow() + "' on table '" + getTable() + "'");
                        }
                    }
                }

                for (MysqlVariable<?> variable : getTable().getVariables()) {
                    if (!data.containsKey(variable)) {
                        data.put(variable, variable.getDefaultValue());
                    }
                }

                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @Override
    protected @NotNull CompletableFuture<Void> unload(boolean save) {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {


                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> save() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                boolean exists = exists().join();

                if (!exists) {
                    try (@NotNull PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + getDatabase().getId() + "`.`" + getTable().getName() + "` (id) VALUES (" + getRow() + ")")) {
                        statement.execute();
                    }
                }

                isNew = !exists;
                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public @NotNull CompletableFuture<Boolean> exists() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                boolean tableExists = getTable().exists().join();

                if (tableExists) {
                    try (PreparedStatement statement = connection.prepareStatement("SELECT `row` FROM `" + getDatabase().getId() + "`.`" + getTable().getName() + "` WHERE `row` = " + getRow())) {
                        ResultSet set = statement.executeQuery();
                        future.complete(set.next());
                        return;
                    }
                }

                future.complete(false);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public boolean matches(@NotNull Condition<?> @NotNull [] conditions) {
        if (!isLoaded()) {
            throw new IllegalStateException("The mysql data must be loaded to use the #matches");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().getTable().equals(getTable()))) {
            throw new IllegalStateException("There's conditions with variables that aren't from this data");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().isLoaded())) {
            throw new IllegalStateException("There's conditions with variables that hasn't loaded");
        } else for (@NotNull Condition<?> condition : conditions) {
            if (!Objects.equals(get(condition.getVariable().getId()), condition.getValue())) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlData)) return false;
        MysqlData data = (MysqlData) object;
        return getRow() == data.getRow() && Objects.equals(getTable(), data.getTable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTable(), getRow());
    }

    @Override
    public @NotNull String toString() {
        return "MysqlData{" +
                "table=" + table +
                ", row=" + row +
                '}';
    }
}
