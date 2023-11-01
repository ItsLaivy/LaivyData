package codes.laivy.data.mysql.table;

import codes.laivy.data.content.Content;
import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public final class Variables extends Content.SetProvider<MysqlVariable<?>> {

    private final @NotNull MysqlTable table;

    public Variables(@NotNull MysqlTable table) {
        super(new HashSet<>());
        this.table = table;
    }

    @Contract(pure = true)
    public @NotNull MysqlTable getTable() {
        return table;
    }

    @Override
    public boolean add(@NotNull MysqlVariable<?> object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The table aren't loaded");
        }

        synchronized (this) {
            return super.add(object);
        }
    }

    @Override
    public boolean remove(@NotNull MysqlVariable<?> object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The variable aren't loaded");
        }

        synchronized (this) {
            return super.remove(object);
        }
    }

    @Override
    public @NotNull Iterator<MysqlVariable<?>> iterator() {
        synchronized (this) {
            return super.iterator();
        }
    }

    @Override
    public @Unmodifiable @NotNull Collection<MysqlVariable<?>> toCollection() {
        synchronized (this) {
            return super.toCollection();
        }
    }
}
