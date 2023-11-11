package codes.laivy.data.mysql.utils;

import codes.laivy.data.mysql.data.Condition;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Set;

public final class SqlUtils {

    @ApiStatus.Internal
    private SqlUtils() {
        throw new UnsupportedOperationException();
    }

    public static int getErrorCode(@NotNull Throwable throwable) {
        if (throwable instanceof SQLException) {
            return ((SQLException) throwable).getErrorCode();
        }
        return -1;
    }

    @ApiStatus.Internal
    public static @NotNull String buildWhereCondition(@NotNull Set<Long> excluded, @NotNull Condition<?> @NotNull ... conditions) {
        @NotNull StringBuilder builder = new StringBuilder("WHERE");

        int index = 0;
        for (@NotNull Condition<?> condition : conditions) {
            if (index > 0) builder.append(" && ");
            builder.append("`").append(condition.getVariable().getId()).append("` = ?");
            index++;
        }

        if (!excluded.isEmpty()) {
            builder.append(" && `row` NOT IN (");

            index = 0;
            for (@NotNull Long row : excluded) {
                builder.append(row);

                if (index + 1 < excluded.size()) {
                    builder.append(",");
                }

                index++;
            }
            builder.append(")");
        }

        return builder.toString();
    }

}
