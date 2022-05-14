package test.pets;

import com.heliorm.annotation.Pojo;
import com.heliorm.annotation.PrimaryKey;
import com.heliorm.annotation.Text;

/**
 * @author gideon
 */
@Pojo
public class CatBreed {

    @PrimaryKey(autoIncrement = true)
    private Long id;
    @Text(length = 32, nullable = false)
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
