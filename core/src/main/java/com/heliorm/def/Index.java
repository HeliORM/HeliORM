package com.heliorm.def;

import com.heliorm.Table;

import java.util.List;

public interface Index<T extends Table<O>, O> {

    List<Field<T,O,?>> getFields();

    boolean isUnique();

}
