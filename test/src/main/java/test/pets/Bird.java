package test.pets;

import com.heliorm.annotation.Pojo;

/**
 * @author gideon
 */
@Pojo
public interface Bird extends Avian {

    Type type();
    int singTime();

     enum Type {
        CAGED, FREERANGE
    }
}
