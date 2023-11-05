package codes.laivy.data.mysql.data;

import codes.laivy.data.Main;
import codes.laivy.data.data.Data;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import codes.laivy.data.mysql.variable.MysqlVariable;
import codes.laivy.data.mysql.variable.Parameter;
import codes.laivy.data.mysql.variable.type.Type;
import org.jetbrains.annotations.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public final class MysqlData extends Data {

    // Static methods

    public static @NotNull CompletableFuture<Boolean> exists(@NotNull MysqlTable table, final long row) {
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                boolean tableExists = table.exists().join();

                if (tableExists) {
                    try (PreparedStatement statement = connection.prepareStatement("SELECT `row` FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` WHERE `row` = " + row)) {
                        ResultSet set = statement.executeQuery();
                        future.complete(set.next());
                        return;
                    }
                }

                future.complete(false);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }
    public static @NotNull CompletableFuture<Integer> exists(@NotNull MysqlTable table, final @NotNull Condition<?> @NotNull ... conditions) {
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();

        if (conditions.length == 0) {
            throw new IllegalStateException("The conditions array cannot be empty");
        } else if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().getTable().equals(table))) {
            throw new IllegalStateException("There's conditions with variables that aren't from the table '" + table.getId() + "'");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().isLoaded())) {
            throw new IllegalStateException("There's conditions with variables that hasn't loaded");
        }

        final @NotNull Condition<?>[] finalConditions = Stream.of(conditions).distinct().toArray(Condition[]::new);
        final @NotNull CompletableFuture<Integer> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!table.exists().join()) {
                    future.complete(-1);
                    return;
                }

                int amount = 0;

                @NotNull Set<Long> excluded = new HashSet<>();

                for (MysqlData data : table.getDatas()) {
                    if (data.isLoaded()) {
                        excluded.add(data.getRow());

                        if (data.matches(finalConditions)) {
                            amount++;
                        }
                    }
                }

                // Retrieving on database

                try (PreparedStatement statement = connection.prepareStatement("SELECT `row` FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` " + SqlUtils.buildWhereCondition(excluded, finalConditions))) {
                    int index = 0;
                    for (@NotNull Condition<?> condition : finalConditions) {
                        //noinspection rawtypes
                        @NotNull Type type = condition.getVariable().getType();
                        //noinspection unchecked
                        type.set(Parameter.of(statement, type.isNullSupported(), index), condition.getValue());
                        index++;
                    }

                    @NotNull ResultSet set = statement.executeQuery();
                    while (set.next()) amount++;
                }

                future.complete(amount);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }

    public static @NotNull CompletableFuture<MysqlData> create(@NotNull MysqlTable table) {
        @NotNull CompletableFuture<MysqlData> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                long increment = table.getAutoIncrement().getAndIncrement(1).join();
                future.complete(retrieve(table, increment));
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }
    public static @NotNull MysqlData retrieve(@NotNull MysqlTable table, final long row) {
        @NotNull Optional<MysqlData> optional = table.getDatas().stream().filter(data -> data.getRow() == row).findFirst();

        if (optional.isPresent()) {
            return optional.get();
        }

        @NotNull MysqlData data = new MysqlData(table, row);
        table.getDatas().add(data);
        return data;
    }
    public static @NotNull CompletableFuture<MysqlData[]> retrieve(@NotNull MysqlTable table, final @NotNull Condition<?> @NotNull ... conditions) {
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();

        if (conditions.length == 0) {
            throw new IllegalStateException("The conditions array cannot be empty");
        } else if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().getTable().equals(table))) {
            throw new IllegalStateException("There's conditions with variables that aren't from the table '" + table.getId() + "'");
        } else if (Arrays.stream(conditions).anyMatch(c -> !c.getVariable().isLoaded())) {
            throw new IllegalStateException("There's conditions with variables that hasn't loaded");
        }

        final @NotNull Condition<?>[] finalConditions = Stream.of(conditions).distinct().toArray(Condition[]::new);
        final @NotNull CompletableFuture<MysqlData[]> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @NotNull Set<Long> excluded = new HashSet<>();
                @NotNull Map<Long, MysqlData> datas = new TreeMap<>(Long::compare);

                for (MysqlData data : table.getDatas()) {
                    if (data.isLoaded()) {
                        excluded.add(data.getRow());

                        if (data.matches(finalConditions)) {
                            datas.put(data.getRow(), data);
                        }
                    }
                }

                // Retrieving on database

                try (PreparedStatement statement = connection.prepareStatement("SELECT `row` FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` " + SqlUtils.buildWhereCondition(excluded, finalConditions))) {
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
                        table.getDatas().add(data);

                        if (!datas.containsKey(row)) {
                            datas.put(row, data);
                        }
                    }
                }

                future.complete(datas.values().toArray(new MysqlData[0]));
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }

    // Object

    private final @NotNull Map<@NotNull MysqlVariable<?>, @Nullable Object> data = new HashMap<>();

    private final @NotNull Map<@NotNull String, @Nullable Object> cache = new HashMap<>();
    private final @NotNull Set<@NotNull String> changed = new HashSet<>();

    private final @NotNull MysqlTable table;
    private final long row;

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
        @NotNull Optional<MysqlVariable<?>> optional = getTable().getVariables().getById(id);

        if (!optional.isPresent() || !data.containsKey(optional.get())) {
            throw new IllegalStateException("There's no variable with id '" + id + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        }

        return data.get(optional.get());
    }
    public <T> @Nullable T get(@NotNull MysqlVariable<T> variable) {
        if (!data.containsKey(variable)) {
            throw new IllegalStateException("There's no variable with id '" + variable.getId() + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        }

        @UnknownNullability Object value = get(variable.getId());

        if (value == null && !variable.isNullable()) {
            throw new IllegalStateException("The variable value of '" + variable.getId() + "' is null, but variable doesn't supports null values");
        }

        //noinspection unchecked
        return (T) value;
    }

    @Override
    public void set(@NotNull String id, @Nullable Object object) {
        @Nullable MysqlVariable<?> variable = getTable().getVariables().getById(id).orElse(null);

        if (variable == null || !data.containsKey(variable)) {
            throw new IllegalStateException("There's no variable with id '" + id + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        } else if (object == null && !variable.isNullable()) {
            throw new IllegalStateException("The variable value of '" + variable.getId() + "' is null, but variable doesn't supports null values");
        } else if (Objects.equals(get(variable), object)) {
            return;
        }

        data.put(variable, object);
        changed.add(id);
    }
    public <T> void set(@NotNull MysqlVariable<T> variable, @Nullable T object) {
        if (!data.containsKey(variable)) {
            throw new IllegalStateException("There's no variable with id '" + variable.getId() + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        }

        set(variable.getId(), object);
    }

    public boolean hasChanges() {
        return !changed.isEmpty();
    }

    public @NotNull CompletableFuture<Void> start() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        } else if (isLoaded()) {
            throw new IllegalStateException("You cannot start the data because it's already start");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!getTable().isLoaded() || !getTable().exists().join()) {
                    throw new IllegalStateException("The table of this data aren't loaded or created");
                }

                if (exists().join()) {
                    try (@NotNull PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + getDatabase().getId() + "`.`" + getTable().getId() + "` WHERE `row` = " + getRow())) {
                        ResultSet set = statement.executeQuery();
                        set.next();

                        for (int row = 1; row <= set.getMetaData().getColumnCount(); row++) {
                            @NotNull String columnName = set.getMetaData().getColumnName(row);
                            @Nullable Object object = set.getObject(row);

                            @NotNull Optional<MysqlVariable<?>> variableOptional = getTable().getVariables().getById(columnName);
                            if (variableOptional.isPresent()) {
                                @NotNull MysqlVariable<?> variable = variableOptional.get();
                                getData().put(variableOptional.get(), variable.getType().get(object));
                            } else {
                                getCache().put(columnName, object);
                            }
                        }

                        if (set.next()) {
                            throw new IllegalStateException("Multiples datas with the same id '" + getRow() + "' on table '" + getTable() + "'");
                        }
                    }

                    isNew = false;
                } else {
                    isNew = true;
                }

                for (MysqlVariable<?> variable : getTable().getVariables()) {
                    if (!data.containsKey(variable)) {
                        data.put(variable, variable.getDefaultValue());
                    }
                }

                loaded = true;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }

    public @NotNull CompletableFuture<Void> stop(boolean save) {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        } else if (!isLoaded()) {
            throw new IllegalStateException("You cannot stop the data because it's already stopped");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (save) save().join();

                getData().clear();
                getCache().clear();

                loaded = false;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }

    @Override
    public @NotNull CompletableFuture<Void> save() {
        // TODO: 03/11/2023 Not mandatory be loaded to save a data

        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        } else if (!isLoaded()) {
            throw new IllegalStateException("The data must be loaded to be saved!");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!getTable().isLoaded() || !getTable().exists().join()) {
                    throw new IllegalStateException("The table of this data aren't loaded or created");
                }

                @NotNull Set<MysqlVariable<?>> variables = new LinkedHashSet<>(getTable().getVariables().toCollection());

                if (!exists().join()) {
                    @NotNull StringBuilder variableString = new StringBuilder();
                    @NotNull StringBuilder valueString = new StringBuilder();

                    for (MysqlVariable<?> variable : variables) {
                        variableString.append(",");
                        variableString.append("`").append(variable.getId()).append("`");
                        valueString.append(",?");
                    }

                    try (@NotNull PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + getDatabase().getId() + "`.`" + getTable().getId() + "` (`row`" + variableString + ") VALUES (" + getRow() + valueString + ")")) {
                        int row = 0;
                        //noinspection rawtypes
                        for (MysqlVariable variable : variables) {
                            //noinspection unchecked
                            variable.getType().set(Parameter.of(statement, variable.getType().isNullSupported(), row), get(variable));
                            row++;
                        }

                        statement.execute();
                    }
                } else {
                    variables.removeIf(variable -> changed.stream().noneMatch(id -> id.equalsIgnoreCase(variable.getId())));

                    if (!variables.isEmpty()) {
                        @NotNull StringBuilder builder = new StringBuilder("UPDATE `" + getDatabase().getId() + "`.`" + getTable().getId() + "` SET ");

                        int row = 0;
                        for (MysqlVariable<?> variable : variables) {
                            if (row > 0) builder.append(",");
                            builder.append("`").append(variable.getId()).append("` = ?");
                            row++;
                        }

                        try (@NotNull PreparedStatement statement = connection.prepareStatement(builder.toString())) {
                            row = 0;
                            //noinspection rawtypes
                            for (MysqlVariable variable : variables) {
                                //noinspection unchecked
                                variable.getType().set(Parameter.of(statement, variable.getType().isNullSupported(), row), get(variable));
                                row++;
                            }

                            statement.execute();
                        }
                    }
                }

                changed.clear();
                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }

    public @NotNull CompletableFuture<Boolean> exists() {
        return exists(getTable(), getRow());
    }

    public @NotNull CompletableFuture<Boolean> delete() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                stop(false).join();

                if (getTable().exists().join()) {
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + getDatabase().getId() + "`.`" + getTable().getId() + "` WHERE `row` = " + getRow())) {
                        statement.execute();
                        future.complete(true);
                        return;
                    }
                }

                future.complete(false);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.MAIN_EXECUTOR);

        return future;
    }

    public boolean matches(@NotNull Condition<?> @NotNull ... conditions) {
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
                "row=" + row + "," +
                "is new=" + isNew +
                '}';
    }
}
