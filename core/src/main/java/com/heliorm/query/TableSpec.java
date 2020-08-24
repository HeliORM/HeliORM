package com.heliorm.query;

import java.util.Optional;
import com.heliorm.Table;

/**
 *
 * @author gideon
 */
public abstract class TableSpec {

    private final Table table;
    private Optional<Criteria> criteria = Optional.empty();
    private Optional<Link> link = Optional.empty();

    public TableSpec(Table table) {
        this.table = table;
    }

    public Table<?> getTable() {
        return table;
    }

    public Optional<Criteria> getCriteria() {
        return criteria;
    }

    public Optional<Link> getLink() {
        return link;
    }

    public void setCriteria(Criteria criteria) {
        this.criteria = Optional.ofNullable(criteria);
    }

    public void setLink(Link link) {
        this.link = Optional.ofNullable(link);
    }

    void dump() {
        System.out.printf("Table: %s ", table.getSqlTable());
        if (criteria.isPresent()) {
            criteria.get().dump();
        }
        if (link.isPresent()) {
            link.get().dump();
        }
    }

}
