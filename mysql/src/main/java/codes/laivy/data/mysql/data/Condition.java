package codes.laivy.data.mysql.data;

import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Condition<T> {

    public static <E> @NotNull Condition<E> of(@NotNull MysqlVariable<E> variable, @Nullable E value) {
        return new Condition<>(variable, value);
    }

    private final @NotNull MysqlVariable<T> variable;
    private final @Nullable T value;

    private Condition(@NotNull MysqlVariable<T> variable, @Nullable T value) {
        this.variable = variable;
        this.value = value;
    }

    public @NotNull MysqlVariable<T> getVariable() {
        return variable;
    }

    public @Nullable T getValue() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof Condition)) return false;
        Condition<?> condition = (Condition<?>) object;
        return Objects.equals(getVariable(), condition.getVariable()) && Objects.equals(getValue(), condition.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getVariable(), getValue());
    }

    @Override
    public @NotNull String toString() {
        return "Condition{" +
                "variable=" + variable +
                ", value=" + value +
                '}';
    }
}
