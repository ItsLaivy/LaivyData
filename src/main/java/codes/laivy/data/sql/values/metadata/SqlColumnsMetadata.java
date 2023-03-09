package codes.laivy.data.sql.values.metadata;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.sql.SQLType;

/**
 * The columns metadata contains information about the columns.
 *
 * @author Laivy
 * @since 1.0
 */
public interface SqlColumnsMetadata extends SqlMetadata {

    /**
     * Returns the column amount of this metadata
     * @return the amount of columns
     */
    @Contract(pure = true)
    @Range(from = 0, to = Integer.MAX_VALUE) int getColumnCount();

    /**
     * Checks if a column is auto incremented or not
     * @param column the column index
     * @return true if so; false otherwise
     */
    @Contract(pure = true)
    boolean isAutoIncrement(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Checks if a column is case-sensitive
     * @param column the column index
     * @return true if so; false otherwise
     */
    @Contract(pure = true)
    boolean isCaseSensitive(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Checks if a column can be used in a where cause
     * @param column the column index
     * @return true if so; false otherwise
     */
    boolean isSearchable(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Gets the nullability state of a column
     * @param column the column index
     * @return the nullability type
     */
    @NotNull ColumnNullability getNullability(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Checks if a column is signed
     * @param column the column index
     * @return true if so; false otherwise
     */
    boolean isSigned(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Gets a column name
     * @param column the column index
     * @return the column name
     */
    @NotNull String getColumnName(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * The maximum bytes/length the column can store, the method will return zero if the column doesn't have a data size
     * @param column the column index
     * @return the precision
     */
    @Range(from = 0, to = Long.MAX_VALUE)
    long getPrecision(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * The column decimal digits to right of the decimal point. 0 is returned if the column's data type isn't applicable for scales
     * @param column the column index
     * @return zero if the column isn't applicable for scaling
     */
    int getScale(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Gets the data type of column
     * @param column the column index
     * @return the column data type
     */
    @NotNull SQLType getColumnType(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Checks if a column is read-only
     * @param column the column index
     * @return true if so; false otherwise
     */
    boolean isReadOnly(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    /**
     * Checks if a column is write-only
     * @param column the column index
     * @return true if so; false otherwise
     */
    boolean isWriteOnly(@Range(from = 0, to = Integer.MAX_VALUE) int column);

    enum ColumnNullability {

        /**
         * The column doesn't allows null values
         */
        NOT_NULLABLE,

        /**
         * The column allows null values
         */
        NULLABLE,

        /**
         * The nullability of a column is unknown
         */
        UNKNOWN

    }

}
