package codes.laivy.data.mysql.data;

import codes.laivy.data.data.Data;
import codes.laivy.data.mysql.MysqlDatabase;
import codes.laivy.data.mysql.MysqlTable;
import codes.laivy.data.mysql.utils.SqlUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class MysqlData extends Data {

    // Static methods

    private static final @NotNull Map<@NotNull MysqlTable, @NotNull List<@NotNull MysqlData>> CACHED_LOADED_DATAS = new HashMap<>();

    public static @NotNull CompletableFuture<Data> retrieve(@NotNull MysqlTable table, long row) {

    }
    public static @NotNull CompletableFuture<Data[]> retrieve(@NotNull MysqlTable table, @NotNull Condition<?> @NotNull ... conditions) {
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

        // TODO: 01/11/2023 Verificar se há dois conditions com a mesma variável

        @NotNull CompletableFuture<Data[]> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            try {
                Map<Long, MysqlData> datas = new TreeMap<>(Long::compare);

                if (CACHED_LOADED_DATAS.containsKey(table)) {
                    for (MysqlData data : CACHED_LOADED_DATAS.get(table).stream().filter(data -> data.isLoaded() && data.matches(conditions)).collect(Collectors.toList())) {
                        datas.put(data.getRow(), data);
                    }
                }

                // Query

                // Retrieving on database

                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + table.getDatabase().getId() + "`.`" + table.getName() + "` " + SqlUtils.buildWhereCondition(datas.keySet(), conditions))) {

                    long row = 0;
                    @NotNull MysqlData data = new MysqlData(table) {
                        @Override
                        protected @NotNull CompletableFuture<Void> load() {
                            return null;
                        }

                        @Override
                        protected @NotNull CompletableFuture<Void> unload(boolean save) {
                            return null;
                        }
                    };

                    if (!datas.containsKey(row)) {
                        datas.put(row, data);
                    }
                }

                future.complete(datas.values().toArray(new Data[0]));
            } catch (@NotNull Throwable throwable) {
                future.completeExceptionally(throwable);
            }
        });

        CACHED_LOADED_DATAS.computeIfAbsent(table, k -> new ArrayList<>()).add(data);
        return future;
    }

    // Object

    private final @NotNull MysqlTable table;
    private long row;

    private MysqlData(@NotNull MysqlTable table) {
        this.table = table;
    }

    public final long getRow() {
        return row;
    }

    // TODO: 01/11/2023 setRow

    @Contract(pure = true)
    public final @NotNull MysqlDatabase getDatabase() {
        return getTable().getDatabase();
    }

    @Contract(pure = true)
    public final @NotNull MysqlTable getTable() {
        return table;
    }

    @Override
    public <T> @Nullable T get(@NotNull String id) {
        return null;
    }

    @Override
    public void set(@NotNull String id, @Nullable Object object) {

    }

    @Override
    public @NotNull CompletableFuture<Void> save() {
        return null;
    }

    public boolean matches(@NotNull Condition<?> @NotNull [] conditions) {

    }

}
