package codes.laivy.data.sqlite.database;

import codes.laivy.data.content.Content;
import codes.laivy.data.sqlite.table.SqliteTable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

public class Tables extends Content.SetProvider<SqliteTable> {

    private final @NotNull SqliteDatabase database;

    public Tables(@NotNull SqliteDatabase table) {
        super(new HashSet<>());
        this.database = table;
    }

    @Contract(pure = true)
    public @NotNull SqliteDatabase getDatabase() {
        return database;
    }

    @Override
    public boolean add(@NotNull SqliteTable object) {
        if (!getDatabase().isLoaded()) {
            throw new IllegalStateException("The database aren't loaded");
        }

        synchronized (this) {
            return super.add(object);
        }
    }

    @Override
    public boolean remove(@NotNull SqliteTable object) {
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
    public @NotNull Optional<SqliteTable> get(@NotNull String id) {
        return stream().filter(table -> table.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public @NotNull Iterator<SqliteTable> iterator() {
        synchronized (this) {
            return super.iterator();
        }
    }

    @Override
    public @Unmodifiable @NotNull Collection<SqliteTable> toCollection() {
        synchronized (this) {
            return super.toCollection();
        }
    }
}
