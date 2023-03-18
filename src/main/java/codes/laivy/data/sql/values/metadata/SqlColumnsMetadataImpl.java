package codes.laivy.data.sql.values.metadata;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.sql.JDBCType;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLType;

/**
 * The native Columns Metadata for the SQL databases of LaivyData.
 * This uses the JDBC's ResultSetMetaData.
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqlColumnsMetadataImpl implements SqlColumnsMetadata {

    private final @NotNull ResultSetMetaData metadata;

    public SqlColumnsMetadataImpl(@NotNull ResultSetMetaData metadata) {
        this.metadata = metadata;
    }

    @Override
    public @Range(from = 0, to = Integer.MAX_VALUE) int getColumnCount() {
        try {
            return metadata.getColumnCount();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAutoIncrement(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return metadata.isAutoIncrement(column + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isCaseSensitive(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return metadata.isCaseSensitive(column + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSearchable(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return metadata.isSearchable(column + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull ColumnNullability getNullability(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            int nullability = metadata.isNullable(column + 1);

            if (nullability == 0) {
                return ColumnNullability.NOT_NULLABLE;
            } else if (nullability == 1) {
                return ColumnNullability.NULLABLE;
            } else if (nullability == 2) {
                return ColumnNullability.UNKNOWN;
            } else {
                throw new IllegalStateException("Couldn't find this column nullability type '" + nullability + "'");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isSigned(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return metadata.isSigned(column + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull String getColumnName(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return metadata.getColumnName(column + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @Range(from = 0, to = Long.MAX_VALUE) long getPrecision(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return metadata.getPrecision(column + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getScale(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return metadata.getScale(column + 1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull SQLType getColumnType(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        try {
            return JDBCType.valueOf(metadata.getColumnType(column + 1));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isReadOnly(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        return false;
    }

    @Override
    public boolean isWriteOnly(@Range(from = 0, to = Integer.MAX_VALUE) int column) {
        return false;
    }
}
