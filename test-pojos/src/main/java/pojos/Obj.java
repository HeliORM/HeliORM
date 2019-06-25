package pojos;

import java.time.Instant;
import net.legrange.orm.annotation.Pojo;

/**
 *
 * @author gideon
 */
@Pojo
public abstract class Obj {

    private Instant created;// = Instant.now();
    private Instant modified;// = Instant.now();
    private int version = 1;

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getModified() {
        return modified;
    }

    public void setModified(Instant modified) {
        this.modified = modified;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
