package codes.laivy.data.mysql.utils;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;

public final class SqlUtils {

    private SqlUtils() {
        throw new UnsupportedOperationException();
    }

    public static int getErrorCode(@NotNull Throwable throwable) {
        if (throwable instanceof SQLException) {
            return ((SQLException) throwable).getErrorCode();
        }
        return -1;
    }

}
