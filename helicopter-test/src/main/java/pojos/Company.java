package pojos;

import me.legrange.orm.annotation.Pojo;
import me.legrange.orm.annotation.PrimaryKey;

/**
 *
 * @author gideon
 */
@Pojo
public class Company extends Obj {

    @PrimaryKey
    private Long companyNumber;
    private String name;

}
