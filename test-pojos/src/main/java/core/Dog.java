package core;

import me.legrange.orm.annotation.Pojo;
import me.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Dog extends Pet {

    @PrimaryKey
    private Long dogNumber;

}
