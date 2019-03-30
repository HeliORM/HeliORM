package me.legrange.orm.impl;

import static java.lang.String.format;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import me.legrange.orm.BooleanClause;
import me.legrange.orm.BooleanField;
import me.legrange.orm.DateClause;
import me.legrange.orm.DateField;
import me.legrange.orm.EnumClause;
import me.legrange.orm.EnumField;
import me.legrange.orm.Field;
import me.legrange.orm.Join;
import me.legrange.orm.NumberClause;
import me.legrange.orm.NumberField;
import me.legrange.orm.Order;
import me.legrange.orm.Orm;
import me.legrange.orm.OrmException;
import me.legrange.orm.Select;
import me.legrange.orm.StringClause;
import me.legrange.orm.StringField;
import me.legrange.orm.Table;

/**
 *
 * @author gideon
 */
public class SelectPart<T extends Table<O>, O, RT extends Table<RO>, RO> extends Part<T, O, RT, RO> implements Select<T, O, RT, RO> {

    private final Orm orm;
    private final Table table;

    public SelectPart(Part left, Table table) {
        this(left, table, null);
    }

    public SelectPart(Part left, Table table, Orm orm) {
        super(left);
        this.table = table;
        this.orm = orm;
    }

    @Override
    protected Orm getOrm() {
        if (left != null) {
            return left.getOrm();
        }
        return orm;
    }

    @Override
    protected Table getReturnTable() {
        if (left != null) {
            return left.getReturnTable();
        }
        return table;
    }

    @Override
    public <RT extends Table<RO>, RO> Join<T, O, RT, RO> join(RT table) {
        return new JoinPart(this, table);
    }

    @Override
    public <F extends NumberField<T, O, C>, C extends Number> NumberClause<T, O, C, RT, RO> where(F field) {
        return new NumberClausePart(this, field);
    }

    @Override
    public <F extends StringField<T, O>> StringClause<T, O, RT, RO> where(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends DateField<T, O>> DateClause<T, O, RT, RO> where(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends EnumField<T, O, C>, C extends Enum> EnumClause<T, O, C, RT, RO> where(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends BooleanField<T, O>> BooleanClause<T, O, RT, RO> where(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RO> list() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Stream<RO> stream() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public RO one() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Optional<RO> oneOrNone() throws OrmException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends Field<RT, RO, C>, C> Order<RT, RO> orderBy(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public <F extends Field<RT, RO, C>, C> Order<RT, RO> orderByDesc(F field) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected String query() {
        if (left == null) {
            return format("select %s", table.getSqTable());
        }
        return format(" %s", table.getSqTable());
    }

}
