package com.heliorm.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
interface SetEnum {

    void apply(PreparedStatement stmt, Integer position, String value) throws SQLException;

}
