package codes.laivy.data.sql.variable;

import codes.laivy.data.api.variable.VariableType;
import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLType;

public interface SqlVariableType<V extends SqlVariable> extends VariableType {

    @Override
    @Contract(pure = true)
    @NotNull SqlDatabase getDatabase();

    /**
     * You can change the query for the variable here.
     * @param object the object that will be stored
     * @param parameters the parameter manager of a variable
     * @param metadata the statement metadata
     */
    void set(@Nullable Object object, @NotNull SqlParameters parameters, @Nullable SqlMetadata metadata);

    /**
     * Here, you will convert the param object into the variable object again.
     * @param object the object stored at the database
     * @return the variable final object
     */
    @Nullable Object get(@Nullable Object object);

    /**
     * This will return the variable type's sql type, the database manager will work with it.
     * @return the sql type
     */
    @NotNull SQLType getSqlType();

    /**
     * Configures a {@link SqlVariable} for the use of this variable type.
     * Changes the variable type for the required type, changes variable configurations at the database, and everything it needs.
     * @param variable the sql variable
     */
    void configure(@NotNull V variable);

}
