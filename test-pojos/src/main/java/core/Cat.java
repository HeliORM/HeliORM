package core;

import me.legrange.orm.annotation.Pojo;
import me.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Cat extends Pet {

    @PrimaryKey
    private Long catNumber;

}
