package codes.laivy.data.sql.utils;

import org.jetbrains.annotations.NotNull;

import java.sql.SQLSyntaxErrorException;

public class SqlErrorUtils {

    /**
     * Throws the exception if the throwable error code isn't the code
     * @param throwable the throwable
     * @param code the code
     */
    public static boolean t(@NotNull Throwable throwable, int code) {
        if (throwable instanceof SQLSyntaxErrorException) {
            SQLSyntaxErrorException s = (SQLSyntaxErrorException) throwable;
            if (s.getErrorCode() == code) return true;
        } else if (throwable.getCause() instanceof SQLSyntaxErrorException) {
            SQLSyntaxErrorException s = (SQLSyntaxErrorException) throwable.getCause();
            if (s.getErrorCode() == code) return true;
        }

        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        } else {
            throw new RuntimeException(throwable);
        }
    }

}
