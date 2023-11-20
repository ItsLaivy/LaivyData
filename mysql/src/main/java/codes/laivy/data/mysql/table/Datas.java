package codes.laivy.data.mysql.table;

import codes.laivy.data.content.Content;
import codes.laivy.data.mysql.data.Condition;
import codes.laivy.data.mysql.data.MysqlData;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class Datas extends Content.SetProvider<MysqlData> {

    private final @NotNull MysqlTable table;

    public Datas(@NotNull MysqlTable table) {
        super(new HashSet<>());
        this.table = table;
    }

    @Contract(pure = true)
    public @NotNull MysqlTable getTable() {
        return table;
    }

    @Override
    public boolean add(@NotNull MysqlData object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The table aren't loaded");
        }

        synchronized (this) {
            return super.add(object);
        }
    }

    public boolean contains(long row) {
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
    public boolean remove(@NotNull MysqlData object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The variable aren't loaded");
        }

        synchronized (this) {
            return super.remove(object);
        }
    }

    @Override
    public @NotNull Iterator<MysqlData> iterator() {
        synchronized (this) {
            return super.iterator();
        }
    }

    @Override
    public @Unmodifiable @NotNull Collection<MysqlData> toCollection() {
        synchronized (this) {
            return super.toCollection();
        }
    }
}
