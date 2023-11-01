package codes.laivy.data.mysql.data;

import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public abstract class Condition<T> {

    public static <E> @NotNull Condition<E> of(@NotNull MysqlVariable<E> variable, @NotNull Predicate<E> predicate) {
        return new Condition<E>(variable) {
            @Override
            public boolean validate(@Nullable E value) {
                return predicate.test(value);
            }
        };
    }

    private final @NotNull MysqlVariable<T> variable;

    private Condition(@NotNull MysqlVariable<T> variable) {
        this.variable = variable;
    }

    public @NotNull MysqlVariable<T> getVariable() {
        return variable;
    }

    public abstract boolean validate(@Nullable T value);

}
