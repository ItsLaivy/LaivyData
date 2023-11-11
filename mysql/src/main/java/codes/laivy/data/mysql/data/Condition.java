package codes.laivy.data.mysql.data;

import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public final class Condition<T> {

    public static <T> @NotNull Condition<T> of(@NotNull MysqlVariable<T> variable, @UnknownNullability T value) {
        return new Condition<>(variable, value);
    }

    private final @NotNull MysqlVariable<T> variable;
    private final @UnknownNullability T value;

    private Condition(@NotNull MysqlVariable<T> variable, @UnknownNullability T value) {
        this.variable = variable;
        this.value = value;
    }

    public @NotNull MysqlVariable<T> getVariable() {
        return variable;
    }

    public @UnknownNullability T getValue() {
        return value;
    }

}
