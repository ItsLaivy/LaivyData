package codes.laivy.data.mysql.table;

import codes.laivy.data.content.Content;
import codes.laivy.data.mysql.variable.MysqlVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;

public final class Variables extends Content.SetProvider<MysqlVariable<?>> {

    private final @NotNull Content<MysqlVariable<?>> defaultVariables = new Content.SetProvider<MysqlVariable<?>>(new HashSet<>()) {
        @Override
        public boolean add(@NotNull MysqlVariable<?> object) {
            if (!object.getTable().equals(getTable())) {
                throw new IllegalStateException("Illegal default variable table");
            }
            return super.add(object);
        }
    };

    private final @NotNull MysqlTable table;

    public Variables(@NotNull MysqlTable table) {
        super(new HashSet<>());
        this.table = table;
    }

    public @NotNull Content<MysqlVariable<?>> getDefault() {
        return defaultVariables;
    }

    @Contract(pure = true)
    public @NotNull MysqlTable getTable() {
        return table;
    }

    public @NotNull Optional<MysqlVariable<?>> getById(@NotNull String id) {
        return stream().filter(v -> v.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public boolean add(@NotNull MysqlVariable<?> object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The table aren't loaded");
        } else if (stream().anyMatch(var -> var.getId().equalsIgnoreCase(object.getId()))) {
            throw new IllegalStateException("A variable with id '" + object.getId() + "' already are added at table '" + getTable().getId() + "'");
        } else if (!object.getTable().equals(getTable())) {
            throw new IllegalStateException("Illegal variable table '" + object.getId() + "'");
        }

        synchronized (this) {
            return super.add(object);
        }
    }

    @Override
    public boolean remove(@NotNull MysqlVariable<?> object) {
        if (!getTable().isLoaded()) {
            throw new IllegalStateException("The variable aren't loaded");
        } else if (!object.getTable().equals(getTable())) {
            throw new IllegalStateException("Illegal variable table '" + object.getId() + "'");
        }

        synchronized (this) {
            return super.remove(object);
        }
    }

    public boolean contains(@NotNull String id) {
        return stream().anyMatch(variable -> variable.getId().equalsIgnoreCase(id));
    }

    public @NotNull Optional<MysqlVariable<?>> get(@NotNull String id) {
        return stream().filter(variable -> variable.getId().equalsIgnoreCase(id)).findFirst();
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
