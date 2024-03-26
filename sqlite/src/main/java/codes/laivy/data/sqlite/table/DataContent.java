package codes.laivy.data.sqlite.table;

import codes.laivy.data.content.Content;
import codes.laivy.data.sqlite.SqliteData;
import codes.laivy.data.sqlite.utils.Condition;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class DataContent extends Content.SetProvider<SqliteData> {

    private final @NotNull SqliteTable table;

    public DataContent(@NotNull SqliteTable table) {
        super(new HashSet<>());
        this.table = table;
    }

    @Contract(pure = true)
    public @NotNull SqliteTable getTable() {
        return table;
    }

    @Override
    public boolean add(@NotNull SqliteData object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The table aren't loaded");
        }

        synchronized (this) {
            return super.add(object);
        }
    }

    public boolean contains(int row) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The table aren't loaded");
        }

        synchronized (this) {
            return stream().anyMatch(d -> d.getRow() == row);
        }
    }
    public boolean contains(@NotNull Condition<?> @NotNull ... conditions) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The table aren't loaded");
        }

        synchronized (this) {
            return stream().anyMatch(d -> d.isLoaded() && d.matches(conditions));
        }
    }

    @Override
    public boolean remove(@NotNull SqliteData object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The variable aren't loaded");
        }

        synchronized (this) {
            return super.remove(object);
        }
    }

    @Override
    public @NotNull Iterator<SqliteData> iterator() {
        synchronized (this) {
            return super.iterator();
        }
    }

    @Override
    public @Unmodifiable @NotNull Collection<SqliteData> toCollection() {
        synchronized (this) {
            return super.toCollection();
        }
    }
}
