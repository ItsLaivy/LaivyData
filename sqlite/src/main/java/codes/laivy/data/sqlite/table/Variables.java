package codes.laivy.data.sqlite.table;

import codes.laivy.data.content.Content;
import codes.laivy.data.sqlite.variable.SqliteVariable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.*;

public final class Variables extends Content.SetProvider<SqliteVariable<?>> {

    private final @NotNull Default defaultVariables = new Default();
    private final @NotNull SqliteTable table;

    public Variables(@NotNull SqliteTable table) {
        super(new HashSet<>());
        this.table = table;
    }

    public @NotNull Default getDefault() {
        return defaultVariables;
    }

    @Contract(pure = true)
    public @NotNull SqliteTable getTable() {
        return table;
    }

    public @NotNull Optional<SqliteVariable<?>> getById(@NotNull String id) {
        return stream().filter(v -> v.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public boolean add(@NotNull SqliteVariable<?> object) {
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
    public boolean remove(@NotNull SqliteVariable<?> object) {
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

    public @NotNull Optional<SqliteVariable<?>> get(@NotNull String id) {
        return stream().filter(variable -> variable.getId().equalsIgnoreCase(id)).findFirst();
    }

    @Override
    public @NotNull Iterator<SqliteVariable<?>> iterator() {
        synchronized (this) {
            return super.iterator();
        }
    }

    @Override
    public @Unmodifiable @NotNull Collection<SqliteVariable<?>> toCollection() {
        synchronized (this) {
            return super.toCollection();
        }
    }

    // Classes

    public final class Default implements Iterable<SqliteVariable<?>> {

        private final @NotNull Set<SqliteVariable<?>> variables = new HashSet<>();

        public void addAll(@NotNull SqliteVariable<?>... variables) {
            for (SqliteVariable<?> variable : variables) {
                if (!variable.getTable().equals(getTable())) {
                    throw new IllegalStateException("Illegal default variable table");
                }
                this.variables.add(variable);
            }
        }
        public boolean add(@NotNull SqliteVariable<?> object) {
            if (!object.getTable().equals(getTable())) {
                throw new IllegalStateException("Illegal default variable table");
            }
            return variables.add(object);
        }
        public boolean remove(@NotNull SqliteVariable<?> object) {
            return variables.remove(object);
        }

        @NotNull
        @Override
        public Iterator<SqliteVariable<?>> iterator() {
            return variables.iterator();
        }
    }

}
