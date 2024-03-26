package codes.laivy.data.sqlite.utils;

import codes.laivy.data.sqlite.variable.SqliteVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class Condition<T> {

    public static <T> @NotNull Condition<T> of(@NotNull SqliteVariable<T> variable, @UnknownNullability T value) {
        return new Condition<>(variable, value);
    }

    private final @NotNull SqliteVariable<T> variable;
    private final @UnknownNullability T value;

    private Condition(@NotNull SqliteVariable<T> variable, @UnknownNullability T value) {
        this.variable = variable;
        this.value = value;
    }

    public @NotNull SqliteVariable<T> getVariable() {
        return variable;
    }

    public @UnknownNullability T getValue() {
        return value;
    }

    @Override
    public @NotNull String toString() {
        return "Condition{" +
                "variable=" + variable +
                ", value=" + value +
                '}';
    }
}
