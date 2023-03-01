package codes.laivy.data.sql.natives.mysql.values.statement;

import codes.laivy.data.sql.natives.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.natives.mysql.connection.MysqlConnectionImpl;
import codes.laivy.data.sql.natives.mysql.values.data.MysqlResultData;
import codes.laivy.data.sql.natives.mysql.values.data.MysqlResultDataImpl;
import codes.laivy.data.sql.values.SqlParameters;
import codes.laivy.data.sql.values.SqlParametersImpl;
import codes.laivy.data.sql.values.metadata.SqlColumnsMetadataImpl;
import codes.laivy.data.sql.values.metadata.SqlMetadata;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * This implementation is for internal use of native LaivyData MySQL support!
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
@ApiStatus.Internal
public class MysqlResultStatementImpl implements MysqlResultStatement {

    private final @NotNull MysqlConnection connection;
    private final @NotNull String query;

    private final @NotNull PreparedStatement statement;

    public MysqlResultStatementImpl(@NotNull MysqlConnectionImpl connection, @NotNull String query) {
        this.connection = connection;
        this.query = query;

        try {
            statement = connection.getConnection().prepareStatement(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public @NotNull PreparedStatement getPreparedStatement() {
        return statement;
    }

    @Override
    public @NotNull MysqlConnection getConnection() {
        return connection;
    }

    @Override
    public @Nullable MysqlResultData execute() {
        try {
            boolean execute = statement.execute();

            if (execute) {
                return new MysqlResultDataImpl(statement.getResultSet());
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull String getQuery() {
        return query;
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
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return statement.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull SqlParameters getParameters(@Range(from = 0, to = Integer.MAX_VALUE) int index) {
        return new SqlParametersImpl(statement, index);
    }
}
