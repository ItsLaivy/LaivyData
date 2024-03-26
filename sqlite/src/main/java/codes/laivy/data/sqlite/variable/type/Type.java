package codes.laivy.data.sqlite.variable.type;

import codes.laivy.data.sqlite.variable.Parameter;
import codes.laivy.data.sqlite.variable.SqliteVariable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface Type<T> {

    /**
     * You can change the query for the variable here.
     * @param value the object that will be stored
     * @param parameter the parameter of a variable
     */
    void set(@NotNull Parameter parameter, @Nullable T value);

    /**
     * Here, you will convert the param object into the variable object again.
     * @param object the object stored at the database
     * @return the variable final object
     */
    @Nullable T get(@Nullable Object object);

    boolean isNullSupported();

    /**
     * Configures a {@link SqliteVariable} for the use of this variable type.
     * Changes the variable type for the required type, changes variable configurations in the database, and everything it needs.
     *
     * @param variable the sql variable
     */
    @NotNull CompletableFuture<Boolean> configure(@NotNull SqliteVariable<T> variable);

}
