package codes.laivy.data.sql.values;

import codes.laivy.data.api.values.ResultData;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * The statement of a sql result are the previous state of a {@link ResultData}, you can configure everything before execute and retrieve the result data.
 *
 * @author ItsLaivy
 * @since 1.0
 */
public interface SqlResultStatement {

    /**
     * The connection's manager of this statement
     * @return the connection
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @NotNull SqlConnection getConnection();

    /**
     * Execute the statement with the configurations and gets the result data
     * @return the result data of this statement or null if the statement isn't a query
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @Nullable SqlResultData execute();

    /**
     * Gets the query of the statement
     * @return the query
     */
    @NotNull String getQuery();

    /**
     * Gets the metadata of the statement.
     *
     * @author ItsLaivy
     * @since 1.0
     */
    @Nullable SqlMetadata getMetaData();

    /**
     * Closes the statement
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void close();

    /**
     * Checks if the statement is closed or not
     * @return true if the statement is closed
     */
    boolean isClosed();

    /**
     * Gets the parameter manager for an index
     * @param index the replacement index
     * @return the parameter manager for that index
     */
    @NotNull SqlParameters getParameters(@Range(from = 0, to = Integer.MAX_VALUE) int index);

}
