package com.heliorm;

import java.util.List;

public interface Index<O> {

    List<Field<O, ?>> getFields();

    boolean isUnique();

}
