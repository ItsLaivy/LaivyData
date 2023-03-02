package codes.laivy.data.sql.mysql.variable.type;

import codes.laivy.data.sql.SqlVariable;
import codes.laivy.data.sql.mysql.MysqlVariable;
import codes.laivy.data.sql.mysql.variable.MysqlVariableType;
import codes.laivy.data.sql.variable.type.SqlByteVariableType;
import com.mysql.cj.MysqlType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.sql.SQLType;

public class MysqlByteVariableType implements SqlByteVariableType<MysqlVariable>, MysqlVariableType<MysqlVariable> {

    private final @NotNull Size size;
    protected @NotNull SQLType type;

    public MysqlByteVariableType(@NotNull Size size) {
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
                return size.getVendorTypeNumber();
            }
        };
    }

    public @NotNull Size getSize() {
        return size;
    }

    @Override
    public @NotNull SQLType getSqlType() {
        return type;
    }

    @Override
    public void configure(@NotNull SqlVariable variable) {
        variable.getDatabase().getManager().getVariablesManager().setType(variable, getSqlType());
    }

    /**
     * The {@link MysqlByteVariableType} saves the objects as Blob, you can select here what size do you prefer to use.
     * @author Laivy
     * @since 1.0
     */
    public enum Size {
        /**
         * <p>
         *     This size could store up to 255 bytes. It's recommended for variables that will save simple bytes of data.
         *     Has a performance higher than {@link #BLOB}
         * </p>
         *
         * @since 1.0
         */
        TINYBLOB(255L, "TINYBLOB", MysqlType.TINYBLOB.getVendorTypeNumber()),

        /**
         * <p>
         *     This size could store up to 65,535 bytes. It's recommended for normal use.
         *     Has a performance higher than {@link #MEDIUMBLOB}, lower than {@link #TINYBLOB}
         * </p>
         *
         * @since 1.0
         */
        BLOB(65535L, "BLOB", MysqlType.BLOB.getVendorTypeNumber()),

        /**
         * <p>
         *     This size could store up to 16,777,215 bytes. It's recommended for large variables, with a big data storing
         *     Has a performance higher than {@link #LONGBLOB}, lower than {@link #BLOB}
         * </p>
         *
         * @since 1.0
         */
        MEDIUMBLOB(16777215L, "MEDIUMBLOB", MysqlType.MEDIUMBLOB.getVendorTypeNumber()),

        /**
         * <p>
         *     <b>Note:</b> This variable size can cause a huge performance issue, only use that if you are absolutely convinced what you are doing.
         * </p>
         *
         * <p>
         *     This size could store up to 4,294,967,295L bytes. It's recommended for massive variables.
         *     Has a performance lower than {@link #MEDIUMBLOB}
         * </p>
         *
         * @since 1.0
         */
        LONGBLOB(4294967295L, "LONGBLOB", MysqlType.LONGBLOB.getVendorTypeNumber()),
        ;

        private final @Range(from = 1, to = Long.MAX_VALUE) long capacity;
        private final @NotNull String name;
        private final @Range(from = 1, to = Integer.MAX_VALUE) int vendorTypeNumber;

        Size(@Range(from = 1, to = Long.MAX_VALUE) long capacity, @NotNull String name, @Range(from = 1, to = Integer.MAX_VALUE) int vendorTypeNumber) {
            this.capacity = capacity;
            this.name = name;
            this.vendorTypeNumber = vendorTypeNumber;
        }

        public @Range(from = 1, to = Long.MAX_VALUE) long getCapacity() {
            return capacity;
        }

        public @NotNull String getName() {
            return name;
        }

        public @Range(from = 1, to = Integer.MAX_VALUE) int getVendorTypeNumber() {
            return vendorTypeNumber;
        }
    }

}
