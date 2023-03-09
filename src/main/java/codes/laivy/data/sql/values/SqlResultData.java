package codes.laivy.data.sql.values;

import codes.laivy.data.api.values.ResultData;

/**
 * The native result data for SQL systems
 *
 * @author Laivy
 * @since 1.0
 */
public interface SqlResultData extends ResultData {

    /**
     * Closes the result data
     *
     * @author Laivy
     * @since 1.0
     */
    void close();

    /**
     * Checks if the result data is closed or not
     * @return true if the result data is closed
     */
    boolean isClosed();

}
