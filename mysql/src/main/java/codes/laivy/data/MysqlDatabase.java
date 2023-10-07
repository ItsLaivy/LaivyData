package codes.laivy.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class MysqlDatabase extends Database {

    private final @NotNull MysqlAuthentication authentication;

    /**
     * Constructs a MysqlDatabase instance with the specified id.
     *
     * @param authentication The authentication of this database
     * @param id The unique id of the database
     * @throws UnsupportedOperationException If the id does not match the pattern
     * @since 1.0
     */
    protected MysqlDatabase(@NotNull MysqlAuthentication authentication, @NotNull String id) {
        super(id);
        this.authentication = authentication;
    }

    @Contract(pure = true)
    public final @NotNull MysqlAuthentication getAuthentication() {
        return authentication;
    }

    @Override
    protected final @NotNull Pattern getPattern() {
        return Pattern.compile("^[a-zA-Z_][a-zA-Z0-9_]*$");
    }

    @Override
    protected @NotNull CompletableFuture<Void> load() {
        if (!getAuthentication().isConnected()) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        return CompletableFuture.runAsync(() -> {
            getAuthentication().load();
        });
    }

    @Override
    protected @NotNull CompletableFuture<Void> unload() {
        if (!getAuthentication().isConnected()) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        return CompletableFuture.runAsync(() -> {

        });
    }

    @Override
    public @NotNull CompletableFuture<Void> delete() {
        if (!getAuthentication().isConnected()) {
            throw new IllegalStateException("This authentication aren't connected");
        }

        return CompletableFuture.runAsync(() -> {

        });
    }

    @Override
    public boolean equals(@Nullable Object object) {
        if (this == object) return true;
        if (!(object instanceof MysqlDatabase)) return false;
        if (!super.equals(object)) return false;
        MysqlDatabase that = (MysqlDatabase) object;
        return Objects.equals(getAuthentication(), that.getAuthentication());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAuthentication());
    }

    @Override
    public @NotNull String toString() {
        return "MysqlDatabase{" +
                "authentication=" + authentication +
                '}';
    }
}
