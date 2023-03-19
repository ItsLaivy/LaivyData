package codes.laivy.data.sql.utils;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLSyntaxErrorException;

public class SqlErrorUtils {

    /**
     * Throws the exception if the throwable error code isn't the code
     * @param throwable the throwable
     * @param code the code
     */
    public static void t(@NotNull Throwable throwable, int code) {
        if (throwable instanceof SQLSyntaxErrorException) {
            SQLSyntaxErrorException s = (SQLSyntaxErrorException) throwable;
            if (s.getErrorCode() == code) return;
        } else if (throwable.getCause() instanceof SQLSyntaxErrorException) {
            SQLSyntaxErrorException s = (SQLSyntaxErrorException) throwable.getCause();
            if (s.getErrorCode() == code) return;
        }

        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else {
            throw new RuntimeException(throwable);
        }
    }

}
