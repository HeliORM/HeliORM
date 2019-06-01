package core;

import me.legrange.orm.annotation.Pojo;
import me.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Cat {

    @PrimaryKey
    private Long catNumber;

}
