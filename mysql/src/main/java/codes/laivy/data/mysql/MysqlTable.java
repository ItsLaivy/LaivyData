package codes.laivy.data.mysql;

import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class MysqlTable {

    private final @NotNull String name;
    private final @NotNull MysqlDatabase database;

    private @Nullable Set<MysqlVariable<?>> variables;

    public MysqlTable(@NotNull String name, @NotNull MysqlDatabase database) {
        this.name = name;
        this.database = database;

        if (!name.matches("^[a-zA-Z0-9_]{0,63}$")) {
            throw new IllegalStateException("This table name '" + name + "' doesn't follows the regex '^[a-zA-Z0-9_]{0,63}$'");
        }
    }

    @Contract(pure = true)
    public final @NotNull String getName() {
        return name;
    }

    @Contract(pure = true)
    public final @NotNull MysqlDatabase getDatabase() {
        return database;
    }

    @Override
    @Contract(pure = true)
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlTable)) return false;
        MysqlTable that = (MysqlTable) object;
        return Objects.equals(getName(), that.getName()) && Objects.equals(getDatabase(), that.getDatabase());
    }

    @Override
    public final int hashCode() {
        return Objects.hash(getName(), getDatabase());
    }

    @Override
    public @NotNull String toString() {
        return "MysqlTable{" +
                "name='" + name + '\'' +
                ", database=" + database +
                ", variables=" + variables +
                '}';
    }
}
