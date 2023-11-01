package codes.laivy.data.mysql.variable;

import codes.laivy.data.variable.Variable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.concurrent.CompletableFuture;

public interface Type<T> {

    /**
     * You can change the query for the variable here.
     * @param object the object that will be stored
     * @param parameter the parameter of a variable
     */
    void set(@NotNull Parameter parameter, @Nullable T object);

    /**
     * Here, you will convert the param object into the variable object again.
     * @param object the object stored at the database
     * @return the variable final object
     */
    @Nullable T get(@Nullable Object object);

    boolean isNullSupported();

    /**
     * Configures a {@link Variable} for the use of this variable type.
     * Changes the variable type for the required type, changes variable configurations in the database, and everything it needs.
     * @param variable the sql variable
     */
    @NotNull CompletableFuture<Void> configure(@NotNull Variable<T> variable);

}
