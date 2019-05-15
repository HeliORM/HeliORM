package pojos;

import java.util.Date;
import me.legrange.orm.annotation.Pojo;

/**
 *
 * @author gideon
 */
@Pojo
public abstract class Obj {

    private Date created;
    private Date modified;
    private int version;

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
