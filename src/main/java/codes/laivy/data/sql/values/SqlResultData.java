package codes.laivy.data.sql.values;

import codes.laivy.data.api.values.ResultData;

public interface SqlResultData extends ResultData {

    /**
     * Closes the result data
     *
     * @author ItsLaivy
     * @since 1.0
     */
    void close();

    /**
     * Checks if the result data is closed or not
     * @return true if the result data is closed
     */
    boolean isClosed();

}
