package codes.laivy.data.sql.sqlite.natives;

import codes.laivy.data.sql.sqlite.connection.SqliteConnection;
import codes.laivy.data.sql.sqlite.values.SqliteResultData;
import codes.laivy.data.sql.sqlite.values.SqliteResultStatement;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.SqlParametersImpl;
import codes.laivy.data.sql.values.metadata.SqlColumnsMetadataImpl;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * <p>
 *     This implementation is for internal use of native LaivyData SQLite support!
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class SqliteResultStatementNative implements SqliteResultStatement {

    private final @NotNull SqliteConnection connection;
    private final @NotNull String query;

    private final @NotNull PreparedStatement statement;

    public SqliteResultStatementNative(@NotNull SqliteConnectionNative connection, @NotNull String query) {
        this.connection = connection;
        this.query = query;

        try {
            statement = connection.getConnection().prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException("Sqlite result statement instance", e);
        }
    }

    /**
     * Gets the java's prepared statement instance of this native statement.
     * @return the prepared statement of this native statement.
     */
    public @NotNull PreparedStatement getPreparedStatement() {
        return statement;
    }

    @Override
    public @NotNull SqliteConnection getConnection() {
        return connection;
    }

    @Override
    public @Nullable SqliteResultData execute() {
        try {
            boolean results = statement.execute();

            if (results) {
                ResultSet set = statement.getResultSet();

                if (!set.isClosed()) {
                    return new SqliteResultDataNative(set);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Query: '" + getStatementQuery() + "'", e);
        }
    }

    @Override
    public @NotNull String getQuery() {
        return query;
    }

    public @NotNull String getStatementQuery() {
        String statementStr = getPreparedStatement().toString();
        return statementStr.split(" \n")[0];
    }

    @Override
    public @Nullable SqlMetadata getMetaData() {
        try {
            ResultSetMetaData metadata = statement.getMetaData();

            if (metadata == null) {
                return null;
            } else {
                return new SqlColumnsMetadataImpl(statement.getMetaData());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query: '" + getStatementQuery() + "'", e);
        }
    }

    @Override
    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException("Query: '" + getStatementQuery() + "'", e);
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return statement.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException("Query: '" + getStatementQuery() + "'", e);
        }
    }

    @Override
    public @NotNull SqlParameters getParameters(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return new SqlParametersImpl(statement, index);
    }
}
