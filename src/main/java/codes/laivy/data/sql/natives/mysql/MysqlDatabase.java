package codes.laivy.data.sql.natives.mysql;

import codes.laivy.data.sql.SqlDatabase;
import codes.laivy.data.sql.SqlReceptor;
import codes.laivy.data.sql.SqlTable;
import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.natives.mysql.connection.MysqlConnection;
import codes.laivy.data.sql.natives.mysql.types.MysqlManager;
import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.Pattern;
import org.jetbrains.annotations.NotNull;

import static java.util.regex.Pattern.matches;

public class MysqlDatabase extends SqlDatabase {

    private final @NotNull @Language("RegExp") String NAME_REGEX = "^[a-zA-Z_][a-zA-Z0-9_-]{0,63}$";

    @SuppressWarnings({"rawtypes", "unchecked"})
    public MysqlDatabase(@NotNull MysqlManager manager, @NotNull @Pattern(NAME_REGEX) String id) {
        //noinspection PatternValidation
        super(manager, id);

        if (!matches(NAME_REGEX, id)) {
            throw new IllegalArgumentException("The database id must follow the regex '" + NAME_REGEX + "'");
        }
    }

    @Override
    public @NotNull MysqlConnection getConnection() {
        return getManager().getConnection();
    }

    @Override
    public @NotNull MysqlManager<SqlReceptor, SqlVariable, SqlDatabase, SqlTable> getManager() {
        return (MysqlManager<SqlReceptor, SqlVariable, SqlDatabase, SqlTable>) super.getManager();
    }
}
