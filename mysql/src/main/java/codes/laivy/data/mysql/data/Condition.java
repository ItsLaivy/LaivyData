package codes.laivy.data.mysql.data;

import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Condition<T> {

    public static <E> @NotNull Condition<E> of(@NotNull MysqlVariable<E> variable, @Nullable E value) {
        return new Condition<E>(variable, value);
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
}
