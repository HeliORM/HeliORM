package com.heliorm.sql;

import com.heliorm.Field;
import com.heliorm.OrmException;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

import static java.lang.String.format;

/**
 * A helper class that provides common operations on prepared statements.
 *
 * @author gideon
 */
final class PreparedStatementHelper {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private final PojoHelper pojoHelper;
    private final SetEnum setEnum;

    public PreparedStatementHelper(PojoHelper pojoHelper, SetEnum setEnum) {
        this.pojoHelper = pojoHelper;
        this.setEnum = setEnum;
    }

    /**
     * Set the value in a prepared statement to the value of the given field
     * from the given POJO
     *
     * @param stmt  The prepared statement in which to set the value
     * @param pojo  The POJO from which to obtain the value
     * @param field The field for which to get the value from the POJO
     * @param par   The position in the prepared statement for the value
     */
    void setValueInStatement(PreparedStatement stmt, Object pojo, Field<?, ?> field, int par) throws OrmException {
        try {
            switch (field.getFieldType()) {
                case LONG, INTEGER, SHORT, BYTE, DOUBLE, FLOAT, BOOLEAN, BYTE_ARRAY ->
                    stmt.setObject(par, pojoHelper.getValueFromPojo(pojo, field));
                case ENUM ->
                    setEnum.apply(stmt, par, pojoHelper.getStringFromPojo(pojo, field));
                case STRING ->
                    stmt.setString(par, pojoHelper.getStringFromPojo(pojo, field));
                case DATE, LOCAL_DATE ->
                    stmt.setDate(par, pojoHelper.getDateFromPojo(pojo, field));
                case INSTANT ->
                    stmt.setTimestamp(par, pojoHelper.getTimestampFromPojo(pojo, field), Calendar.getInstance(UTC));
                case LOCAL_DATE_TIME ->
                    stmt.setTimestamp(par, pojoHelper.getTimestampFromPojo(pojo, field));
                default ->
                    throw new OrmException(format("Field type '%s' is unsupported. BUG!", field.getFieldType()));
            };
        } catch (SQLException ex) {
            throw new OrmSqlException(ex.getMessage(), ex);
        }
    }

}
