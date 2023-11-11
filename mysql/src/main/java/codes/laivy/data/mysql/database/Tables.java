package codes.laivy.data.mysql.database;

import codes.laivy.data.content.Content;
import codes.laivy.data.mysql.table.MysqlTable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

public class Tables extends Content.SetProvider<MysqlTable> {

    private final @NotNull MysqlDatabase database;

    public Tables(@NotNull MysqlDatabase table) {
        super(new HashSet<>());
        this.database = table;
    }

    @Contract(pure = true)
    public @NotNull MysqlDatabase getDatabase() {
        return database;
    }

    @Override
    public boolean add(@NotNull MysqlTable object) {
        if (!getDatabase().isLoaded()) {
            throw new IllegalStateException("The database aren't loaded");
        }

        synchronized (this) {
            return super.add(object);
        }
    }

    @Override
    public boolean remove(@NotNull MysqlTable object) {
        if (!getDatabase().isLoaded()) {
            throw new IllegalStateException("The table aren't loaded");
        }

        synchronized (this) {
            return super.remove(object);
        }
    }

    public boolean contains(@NotNull String id) {
        return stream().anyMatch(table -> table.getId().equalsIgnoreCase(id));
    }
    public @NotNull Optional<MysqlTable> get(@NotNull String id) {
        return stream().filter(table -> table.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public @NotNull Iterator<MysqlTable> iterator() {
        synchronized (this) {
            return super.iterator();
        }
    }

    @Override
    public @Unmodifiable @NotNull Collection<MysqlTable> toCollection() {
        synchronized (this) {
            return super.toCollection();
        }
    }
}
