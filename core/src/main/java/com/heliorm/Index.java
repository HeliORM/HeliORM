package com.heliorm;

import java.util.List;

public interface Index<T extends Table<O>, O> {

    List<Field<T,O,?>> getFields();

    boolean isUnique();

}
