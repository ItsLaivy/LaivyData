package codes.laivy.data.mysql.authentication;

import codes.laivy.data.content.Content;
import codes.laivy.data.mysql.database.MysqlDatabase;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

public class Databases extends Content.SetProvider<MysqlDatabase> {

    private final @NotNull MysqlAuthentication authentication;

    public Databases(@NotNull MysqlAuthentication table) {
        super(new HashSet<>());
        this.authentication = table;
    }

    @Contract(pure = true)
    public @NotNull MysqlAuthentication getAuthentication() {
        return authentication;
    }

    @Override
    public boolean add(@NotNull MysqlDatabase object) {
        if (!getAuthentication().isConnected()) {
            throw new IllegalStateException("The authentication aren't connected");
        }

        synchronized (this) {
            return super.add(object);
        }
    }

    @Override
    public boolean remove(@NotNull MysqlDatabase object) {
        if (!getAuthentication().isConnected()) {
            throw new IllegalStateException("The authentication aren't connected");
        }

        synchronized (this) {
            return super.remove(object);
        }
    }

    @Override
    public @NotNull Iterator<MysqlDatabase> iterator() {
        synchronized (this) {
            return super.iterator();
        }
    }

    @Override
    public @Unmodifiable @NotNull Collection<MysqlDatabase> toCollection() {
        synchronized (this) {
            return super.toCollection();
        }
    }
}
