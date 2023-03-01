package codes.laivy.data.sql.mysql.variable.type;

import codes.laivy.data.sql.mysql.MysqlDatabase;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.variable.type.SqlTextVariableType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.sql.JDBCType;
import java.sql.SQLType;

public class MysqlTextVariableType implements SqlTextVariableType<MysqlVariable> {

    private final @NotNull MysqlDatabase database;
    private final @NotNull Size size;
    protected @NotNull SQLType type;

    public MysqlTextVariableType(@NotNull MysqlDatabase database, @NotNull Size size) {
        this.database = database;
        this.size = size;

        type = new SQLType() {
            @Override
            public String getName() {
                return size.getName();
            }

            @Override
            public String getVendor() {
                return "codes.laivy.data.sql.natives.mysql.variable.type";
            }

            @Override
            public Integer getVendorTypeNumber() {
                return JDBCType.BLOB.getVendorTypeNumber();
            }
        };
    }

    public @NotNull Size getSize() {
        return size;
    }

    @Override
    public @NotNull MysqlDatabase getDatabase() {
        return database;
    }

    @Override
    public @NotNull SQLType getSqlType() {
        return type;
    }

    @Override
    public void configure(@NotNull MysqlVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }

    /**
     * The {@link MysqlTextVariableType} saves the objects as TEXT, you can select here what size do you prefer to use.
     * @author Laivy
     * @since 1.0
     */
    public enum Size {
        /**
         * <p>
         *     This size could store up to 255 bytes. It's recommended for variables that will save simple bytes of data.
         *     Has a performance higher than {@link #TEXT}
         * </p>
         *
         * @since 1.0
         */
        TINYTEXT(255L, "TINYTEXT"),

        /**
         * <p>
         *     This size could store up to 65,535 bytes. It's recommended for normal use.
         *     Has a performance higher than {@link #MEDIUMTEXT}, lower than {@link #TINYTEXT}
         * </p>
         *
         * @since 1.0
         */
        TEXT(65535L, "TEXT"),

        /**
         * <p>
         *     This size could store up to 16,777,215 bytes. It's recommended for large variables, with a big data storing
         *     Has a performance higher than {@link #TEXT}, lower than {@link #TEXT}
         * </p>
         *
         * @since 1.0
         */
        MEDIUMTEXT(16777215L, "MEDIUMTEXT"),

        /**
         * <p>
         *     <b>Note:</b> This variable size can cause a huge performance issue, only use that if you are absolutely convinced what you are doing.
         * </p>
         *
         * <p>
         *     This size could store up to 4,294,967,295L bytes. It's recommended for massive variables.
         *     Has a performance lower than {@link #MEDIUMTEXT}
         * </p>
         *
         * @since 1.0
         */
        LONGTEXT(4294967295L, "LONGTEXT"),
        ;

        private final @Range(from = 1, to = Long.MAX_VALUE) long capacity;
        private final @NotNull String name;

        Size(@Range(from = 1, to = Long.MAX_VALUE) long capacity, @NotNull String name) {
            this.capacity = capacity;
            this.name = name;
        }

        public @Range(from = 1, to = Long.MAX_VALUE) long getCapacity() {
            return capacity;
        }

        public @NotNull String getName() {
            return name;
        }
    }

}
