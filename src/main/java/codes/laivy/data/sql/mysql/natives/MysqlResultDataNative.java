package codes.laivy.data.sql.mysql.natives;

import codes.laivy.data.sql.mysql.values.MysqlResultData;
import org.jetbrains.annotations.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * <p>
 *     The native MySQL ResultData of LaivyData.
 *     This has made to been used on native MySQL systems.
 * </p>
 *
 * @author Laivy
 * @since 1.0
 *
 * @version 1.0 - (01/03/2023)
 */
public class MysqlResultDataNative implements MysqlResultData {

    private final @NotNull ResultSet result;

    private final @NotNull Map<@NotNull String, @Nullable Object>[] array;
    private @Range(from = 0, to = Integer.MAX_VALUE) int index;

    public MysqlResultDataNative(@NotNull ResultSet result) {
        this.result = result;

        try {
            if (result.isClosed()) {
                throw new IllegalStateException("The result set is closed!");
            }

            Set<Map<String, Object>> set = new LinkedHashSet<>();
            while (result.next()) {
                Map<String, Object> map = new LinkedHashMap<>();

                if (result.getObject(1) == null) {
                    continue;
                }

                for (int row = 1; row <= result.getMetaData().getColumnCount(); row++) {
                    map.put(result.getMetaData().getColumnName(row), result.getObject(row));
                }

                set.add(map);
            }

            //noinspection unchecked
            array = set.toArray(new Map[0]);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The java's {@link ResultSet} instance of this native result data
     * @return this result data's {@link ResultSet} instance
     */
    public @NotNull ResultSet getResultSet() {
        return result;
    }

    @Override
    public boolean hasNext() {
        return array.length > (index + 1);
    }

    @Override
    public Map<String, Object> next() {
        Map<String, Object> map = array[index];
        index++;
        return map;
    }

    @Override
    public void first() {
        index = 0;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    @Unmodifiable
    public @NotNull Set<@NotNull Map<@NotNull String, @Nullable Object>> getValues() {
        return new LinkedHashSet<>(Arrays.asList(array));
    }

    @Override
    public void close() {
        try {
            result.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isClosed() {
        try {
            return result.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
