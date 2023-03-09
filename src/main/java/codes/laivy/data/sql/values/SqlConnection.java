package codes.laivy.data.sql.values;

import org.jetbrains.annotations.NotNull;

/**
 * This interface is the connection driver of the SQL server.
 */
public interface SqlConnection {

    /**
     * Creates a new statement using the query parameter.
     * 
     * @param query the query
     * @return the sql statement
     * @author Laivy
     * @since 1.0
     */
    @NotNull SqlResultStatement createStatement(@NotNull String query);

}
