package codes.laivy.data.sql.values;

import org.jetbrains.annotations.NotNull;

/**
 * This interface is the connection driver of the SQL server.
 */
public interface SqlConnection {

    @NotNull SqlResultStatement createStatement(@NotNull String query);

}
