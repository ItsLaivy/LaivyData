package codes.laivy.data.mysql.data;

import codes.laivy.data.Main;
import codes.laivy.data.data.Data;
import codes.laivy.data.mysql.database.MysqlDatabase;
import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.table.Variables;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class MysqlData extends Data {

    // Static methods

    public static @NotNull CompletableFuture<Boolean> exists(@NotNull MysqlTable table, final int row) {
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
        }, Main.getExecutor(MysqlData.class));

        return future;
    }
    public static @NotNull CompletableFuture<Integer> exists(@NotNull MysqlTable table, final @NotNull Condition<?> @NotNull ... conditions) {
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
        }

        final @NotNull CompletableFuture<Integer> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!table.exists().join()) {
                    future.complete(-1);
                    return;
                }

                int amount = 0;

                @NotNull Set<Integer> excluded = new HashSet<>();

                for (MysqlData data : table.getDataContent()) {
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
        }, Main.getExecutor(MysqlData.class));

        return future;
    }

    public static @NotNull CompletableFuture<Void> delete(@NotNull MysqlTable table, final @NotNull Condition<?> @NotNull ... conditions) {
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
        }

        final @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @NotNull Set<Integer> excluded = new HashSet<>();
                for (MysqlData data : table.getDataContent()) {
                    if (!data.matches(finalConditions)) {
                        excluded.add(data.row);
                    } else {
                        data.stop(false).join();
                    }
                }

                // Retrieving on database

                try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` " + SqlUtils.buildWhereCondition(excluded, finalConditions))) {
                    int index = 0;
                    for (@NotNull Condition<?> condition : finalConditions) {
                        //noinspection rawtypes
                        @NotNull Type type = condition.getVariable().getType();
                        //noinspection unchecked
                        type.set(Parameter.of(statement, type.isNullSupported(), index), condition.getValue());
                        index++;
                    }

                    statement.execute();
                }

                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(MysqlData.class));

        return future;
    }
    public static @NotNull CompletableFuture<Void> delete(@NotNull MysqlTable table, int row) {
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @NotNull Optional<MysqlData> optional = table.getDataContent().stream().filter(data -> data.getRow() == row).findFirst();
                optional.ifPresent(data -> {
                    if (data.isLoaded()) {
                        data.stop(false).join();
                    }
                });

                if (table.exists().join()) {
                    try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` WHERE `row` = " + row)) {
                        statement.execute();
                    }
                }

                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(MysqlData.class));

        return future;
    }
    public static @NotNull CompletableFuture<MysqlData> create(@NotNull MysqlTable table) {
        @NotNull CompletableFuture<MysqlData> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                int row = table.getAutoIncrement().getAndIncrement(1).join();

                if (table.getDataContent().stream().anyMatch(data -> data.getRow() == row && !data.exists().join())) {
                    throw new IllegalStateException("cannot create date because this table was illegally modified");
                }

                future.complete(retrieve(table, row));
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(MysqlData.class));

        return future;
    }
    public static @NotNull MysqlData retrieve(@NotNull MysqlTable table, final int row) {
        @NotNull Optional<MysqlData> optional = table.getDataContent().stream().filter(data -> data.getRow() == row).findFirst();

        if (optional.isPresent()) {
            return optional.get();
        }

        @NotNull MysqlData data = new MysqlData(table, row);
        table.getDataContent().add(data);
        return data;
    }
    public static @NotNull CompletableFuture<MysqlData[]> retrieve(@NotNull MysqlTable table) {
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();

        if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        }

        @NotNull CompletableFuture<MysqlData[]> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!table.exists().join()) {
                    throw new IllegalStateException("This table doesn't exists");
                }

                @NotNull Set<MysqlData> datas = new HashSet<>();

                for (MysqlData data : table.getDataContent()) {
                    if (data.isLoaded()) {
                        datas.add(data);
                    }
                }

                try (PreparedStatement statement = connection.prepareStatement("SELECT `row` FROM `" + table.getDatabase().getId() + "`.`" + table.getId() + "` WHERE " + SqlUtils.rowNotIn(datas.stream().map(MysqlData::getRow).collect(Collectors.toSet())))) {
                    @NotNull ResultSet set = statement.executeQuery();
                    while (set.next()) {
                        int row = set.getInt("row");
                        @NotNull MysqlData data = new MysqlData(table, row);
                        datas.add(data);
                    }
                }

                future.complete(datas.toArray(new MysqlData[0]));
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        return future;
    }

    public static @NotNull CompletableFuture<MysqlData[]> retrieve(@NotNull MysqlTable table, final @NotNull Condition<?> @NotNull ... conditions) {
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
        }

        final @NotNull CompletableFuture<MysqlData[]> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @NotNull Set<Integer> excluded = new HashSet<>();
                @NotNull Map<Integer, MysqlData> datas = new TreeMap<>(Integer::compare);

                for (MysqlData data : table.getDataContent()) {
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
                        int row = set.getInt("row");
                        @NotNull MysqlData data = new MysqlData(table, row);
                        table.getDataContent().add(data);

                        if (!datas.containsKey(row)) {
                            datas.put(row, data);
                        }
                    }
                }

                future.complete(datas.values().toArray(new MysqlData[0]));
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(MysqlData.class));

        return future;
    }

    public static <T> @NotNull CompletableFuture<Void> set(@NotNull MysqlVariable<T> variable, @UnknownNullability T value, final int row) {
        @NotNull MysqlTable table = variable.getTable();
        @Nullable Connection connection = table.getDatabase().getAuthentication().getConnection();

        if (connection == null) {
            throw new IllegalStateException("The table's authentication aren't connected");
        } else if (!table.isLoaded() || !table.getDatabase().isLoaded()) {
            throw new IllegalStateException("This table or database aren't loaded");
        }

        final @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!variable.exists().join()) {
                    throw new IllegalStateException("This variable doesn't exists");
                }

                for (MysqlData data : variable.getTable().getDataContent().stream().filter(data -> data.isLoaded() && data.getRow() == row).collect(Collectors.toList())) {
                    data.set(variable, value);
                }

                try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` SET `" + variable.getId() + "` = ? WHERE `row` = " + row)) {
                    variable.getType().set(Parameter.of(statement, variable.getType().isNullSupported(), 0), value);
                    statement.execute();
                }

                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(MysqlData.class));

        return future;
    }
    public static <T> @NotNull CompletableFuture<Void> set(@NotNull MysqlVariable<T> variable, @UnknownNullability T value, final @NotNull Condition<?> @NotNull ... conditions) {
        @NotNull MysqlTable table = variable.getTable();
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
        }

        final @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                for (MysqlData data : variable.getTable().getDataContent().stream().filter(data -> data.isLoaded() && data.matches(conditions)).collect(Collectors.toList())) {
                    data.set(variable, value);
                }

                try (PreparedStatement statement = connection.prepareStatement("UPDATE `" + variable.getDatabase().getId() + "`.`" + variable.getTable().getId() + "` SET `" + variable.getId() + "` = ? " + SqlUtils.buildWhereCondition(new LinkedHashSet<>(), finalConditions))) {
                    variable.getType().set(Parameter.of(statement, variable.getType().isNullSupported(), 0), value);

                    int index = 1;
                    for (@NotNull Condition<?> condition : finalConditions) {
                        //noinspection rawtypes
                        @NotNull Type type = condition.getVariable().getType();
                        //noinspection unchecked
                        type.set(Parameter.of(statement, type.isNullSupported(), index), condition.getValue());
                        index++;
                    }

                    statement.execute();
                }

                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(MysqlData.class));

        return future;
    }

    // Object

    private final @NotNull Map<@NotNull MysqlVariable<?>, @Nullable Object> data = new HashMap<>();

    private final @NotNull Map<@NotNull String, @Nullable Object> cache = new HashMap<>();
    private final @NotNull Set<@NotNull String> changed = new HashSet<>();

    private final @NotNull MysqlTable table;
    private final int row;

    private MysqlData(@NotNull MysqlTable table, int row) {
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

    public int getRow() {
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
    public @UnknownNullability Object get(@NotNull String id) {
        if (!isLoaded()) {
            throw new IllegalStateException("You cannot retrieve values of a unloaded data");
        }

        @NotNull Optional<MysqlVariable<?>> optional = getTable().getVariables().getById(id);

        if (!optional.isPresent() || !getData().containsKey(optional.get())) {
            throw new IllegalStateException("There's no variable with id '" + id + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        }

        @NotNull MysqlVariable<?> variable = optional.get();
        return getData().get(variable);
    }
    public <T> @UnknownNullability T get(@NotNull MysqlVariable<T> variable) {
        if (!isLoaded()) {
            throw new IllegalStateException("This data aren't loaded");
        } else if (!getData().containsKey(variable)) {
            throw new IllegalStateException("There's no variable with id '" + variable.getId() + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        }

        //noinspection unchecked
        return (T) get(variable.getId());
    }

    @Override
    public void set(@NotNull String id, @UnknownNullability Object object) {
        if (!isLoaded()) {
            throw new IllegalStateException("You cannot change values of a unloaded data");
        }

        @Nullable MysqlVariable<?> variable = getTable().getVariables().getById(id).orElse(null);

        if (variable == null || !getData().containsKey(variable)) {
            throw new IllegalStateException("There's no variable with id '" + id + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        } else if (object == null && !variable.isNullable()) {
            throw new IllegalStateException("The variable value of '" + variable.getId() + "' is null, but variable doesn't supports null values");
        }

        synchronized (this) {
            getData().put(variable, variable.getType().get(object));
            setChanges(variable, true);
        }
    }
    public <T> void set(@NotNull MysqlVariable<T> variable, @UnknownNullability T object) {
        if (!isLoaded()) {
            throw new IllegalStateException("This data aren't loaded");
        } else if (!getData().containsKey(variable)) {
            throw new IllegalStateException("There's no variable with id '" + variable.getId() + "' at data '" + getRow() + "' from table '" + getTable().getId() + "'");
        }

        set(variable.getId(), object);
    }

    public boolean hasChanges() {
        return !changed.isEmpty();
    }
    public void setChanges(@NotNull MysqlVariable<?> variable, boolean flag) {
        if (!getTable().getVariables().contains(variable)) {
            throw new IllegalStateException("The table of that data doesn't contains that variable");
        } else if (flag) {
            changed.add(variable.getId().toLowerCase());
        } else {
            changed.remove(variable.getId().toLowerCase());
        }
    }

    public @NotNull CompletableFuture<Void> start() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        } else if (isLoaded()) {
            throw new IllegalStateException("You cannot start the data because it's already started");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                if (!getTable().isLoaded() || !getTable().exists().join()) {
                    throw new IllegalStateException("The table of this data aren't loaded or created");
                }

                getData().clear();
                getCache().clear();

                if (exists().join()) {
                    try (@NotNull PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + getDatabase().getId() + "`.`" + getTable().getId() + "` WHERE `row` = " + getRow())) {
                        ResultSet set = statement.executeQuery();
                        set.next();

                        for (int row = 1; row <= set.getMetaData().getColumnCount(); row++) {
                            @NotNull String columnName = set.getMetaData().getColumnName(row);
                            @Nullable Object object = set.getObject(row);

                            if (columnName.equalsIgnoreCase("row")) {
                                continue;
                            }

                            @NotNull Optional<MysqlVariable<?>> variableOptional = getTable().getVariables().getById(columnName);
                            if (variableOptional.isPresent()) {
                                @NotNull MysqlVariable<?> variable = variableOptional.get();
                                getData().put(variable, variable.getType().get(object));
                            } else {
                                getCache().put(columnName.toLowerCase(), object);
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
                    if (variable.isLoaded()) {
                        if (!getData().containsKey(variable)) {
                            getData().put(variable, variable.getDefaultValue());
                        }
                    }
                }

                loaded = true;
                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

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
                loaded = false;

                if (save) save().join();
                changed.clear();

                getData().clear();
                getCache().clear();

                future.complete(null);
            } catch (Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        }, Main.getExecutor(getClass()));

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
                if (!getTable().isLoaded() || !getTable().exists().join()) {
                    throw new IllegalStateException("The table of this data aren't loaded or created");
                }

                if (!exists().join()) {
                    create().join();
                } else {
                    @NotNull Set<MysqlVariable<?>> variables = new LinkedHashSet<>(getData().keySet());
                    variables.removeIf(variable -> !changed.contains(variable.getId().toLowerCase()) || !variable.exists().join());

                    if (!variables.isEmpty()) {
                        @NotNull StringBuilder builder = new StringBuilder("UPDATE `" + getDatabase().getId() + "`.`" + getTable().getId() + "` SET ");

                        int row = 0;
                        for (MysqlVariable<?> variable : variables) {
                            if (row > 0) builder.append(",");
                            builder.append("`").append(variable.getId()).append("` = ?");
                            row++;
                        }

                        builder.append(" WHERE `row` = ").append(getRow());

                        try (@NotNull PreparedStatement statement = connection.prepareStatement(builder.toString())) {
                            row = 0;
                            //noinspection rawtypes
                            for (MysqlVariable variable : variables) {
                                //noinspection unchecked
                                variable.getType().set(Parameter.of(statement, variable.getType().isNullSupported(), row), getData().get(variable));
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
        }, Main.getExecutor(getClass()));

        return future;
    }

    public @NotNull CompletableFuture<Void> create() {
        @Nullable Connection connection = getDatabase().getAuthentication().getConnection();
        if (connection == null) {
            throw new IllegalStateException("The database's authentication aren't connected");
        }

        @NotNull CompletableFuture<Void> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                @NotNull StringBuilder variableString = new StringBuilder();
                @NotNull StringBuilder valueString = new StringBuilder();
                @NotNull Variables variables = getTable().getVariables();

                for (MysqlVariable<?> variable : variables) {
                    variableString.append(",");
                    variableString.append("`").append(variable.getId()).append("`");
                    valueString.append(",?");
                }

                try (@NotNull PreparedStatement statement = connection.prepareStatement("INSERT INTO `" + getDatabase().getId() + "`.`" + getTable().getId() + "` (`row`" + variableString + ") VALUES (" + getRow() + valueString + ")")) {
                    int row = 0;
                    //noinspection rawtypes
                    for (MysqlVariable variable : variables) {
                        @NotNull Object object = variable.getDefaultValue();

                        if (getData().containsKey(variable)) {
                            object = getData().get(variable);
                        } else if (getCache().containsKey(variable.getId().toLowerCase())) {
                            object = getCache().get(variable.getId().toLowerCase());
                        }

                        //noinspection unchecked
                        variable.getType().set(Parameter.of(statement, variable.getType().isNullSupported(), row), object);

                        row++;
                    }

                    statement.execute();
                }

                changed.clear();
                future.complete(null);
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

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
                if (isLoaded()) {
                    stop(false).join();
                }

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
        }, Main.getExecutor(getClass()));

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
            if (!Objects.equals(get(condition.getVariable()), condition.getValue())) {
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

    @Override
    @Blocking
    public void flush() {
        if (isLoaded()) {
            stop(true);
        }

        getTable().getDataContent().remove(this);
    }

}
