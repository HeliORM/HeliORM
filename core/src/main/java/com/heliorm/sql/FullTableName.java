package com.heliorm.sql;


import com.heliorm.OrmException;
import com.heliorm.Table;

@FunctionalInterface
interface FullTableName {

    String apply(Table table) throws OrmException;


}
