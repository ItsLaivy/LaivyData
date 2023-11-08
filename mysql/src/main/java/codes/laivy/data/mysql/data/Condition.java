package codes.laivy.data.mysql.data;

import codes.laivy.data.mysql.table.MysqlTable;
import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Objects;

public final class Condition {

    public static <E> @NotNull Condition of(@NotNull MysqlVariable<E> variable, @UnknownNullability E value) {
        return new Condition(variable.getTable(), variable.getId(), value);
    }
    public static @NotNull Condition of(@NotNull MysqlTable table, @NotNull String variableId, @UnknownNullability Object value) {
        return new Condition(table, variableId, value);
    }

    private final @NotNull MysqlTable table;
    private final @NotNull String variableId;

    private final @UnknownNullability Object value;

    private Condition(@NotNull MysqlTable table, @NotNull String variableId, @UnknownNullability Object value) {
        this.table = table;
        this.variableId = variableId;

        this.value = value;
    }

    public @NotNull String getVariableId() {
        return variableId;
    }

    public @NotNull MysqlTable getTable() {
        return table;
    }

    public @NotNull MysqlVariable<?> getVariable() {
        return getTable().getVariables().get(getVariableId()).orElseThrow(() -> new NullPointerException("There's no variable with id '" + getVariableId() + "' at table '" + getTable().getId() + "'"));
    }

    public @UnknownNullability Object getValue() {
        return value;
    }

}
