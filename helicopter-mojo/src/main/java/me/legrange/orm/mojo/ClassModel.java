package me.legrange.orm.mojo;

import java.util.List;

/**
 *
 * @author gideon
 */
public interface ClassModel {

    String getClassName();

    String getJavaName();

    String getTableName();

    List<FieldModel> getFields();

}
