package com.heliorm.sql;

import com.heliorm.OrmException;
import com.heliorm.impl.ExecutablePart;
import com.heliorm.sql.mysql.MySqlDriver;
import com.heliorm.test.place.Town;
import com.heliorm.test.place.Province;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.heliorm.Query.where;
import static com.heliorm.test.Tables.TOWN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Boolean values in WHERE clauses must be rendered as 1 and 0.
 * <p>
 * They used to be rendered as 'true' and 'false'. H2 accepts that for boolean columns, which is why the database
 * backed tests never noticed, but MySQL and MariaDB convert the string 'true' to the number 0 when comparing it with
 * a TINYINT column, so {@code eq(true)} matched the false rows. This test therefore checks the generated SQL, not the
 * rows that come back from whatever database happens to run the suite.
 */
public class BooleanCriteriaTest extends AbstractOrmTest {

    private static Town withPetshop;
    private static Town withoutPetshop;

    @BeforeAll
    public static void createTowns() throws OrmException {
        var province = createAll(List.of(TestData.makeProvinces().getFirst())).getFirst();
        withPetshop = town("Petshopville", province, true);
        withoutPetshop = town("Plainville", province, false);
        createAll(List.of(withPetshop, withoutPetshop));
    }

    @AfterAll
    public static void removeTowns() throws OrmException {
        deleteAll(Town.class);
        deleteAll(Province.class);
    }

    private static Town town(String name, Province province, boolean petshop) {
        var town = new Town();
        town.setName(name);
        town.setProvinceId(province.provinceId());
        town.setFounded(LocalDateTime.of(1822, 10, 11, 16, 30));
        town.setHasPetshop(petshop);
        return town;
    }

    private List<String> names(com.heliorm.def.Where<Town> where) throws OrmException {
        return orm().select(TOWN, where).list().stream().map(Town::getName).sorted().toList();
    }

    // These run against whatever database the suite is pointed at (ORM_TEST_DB). On H2 they pass even without the
    // fix; on MySQL and MariaDB they select the wrong rows without it.

    @Test
    public void equalsTrueSelectsOnlyTownsWithPetshops() throws OrmException {
        assertEquals(List.of("Petshopville"), names(where(TOWN.hasPetshop.eq(true))));
    }

    @Test
    public void equalsFalseSelectsOnlyTownsWithoutPetshops() throws OrmException {
        assertEquals(List.of("Plainville"), names(where(TOWN.hasPetshop.eq(false))));
    }

    @Test
    public void notEqualsTrueSelectsTheOthers() throws OrmException {
        assertEquals(List.of("Plainville"), names(where(TOWN.hasPetshop.notEq(true))));
    }

    @Test
    public void combinesWithOtherCriteria() throws OrmException {
        assertEquals(List.of("Petshopville"), names(where(TOWN.name.eq("Petshopville").and(TOWN.hasPetshop.eq(true)))));
        assertEquals(List.of(), names(where(TOWN.name.eq("Plainville").and(TOWN.hasPetshop.eq(true)))));
    }

    private String sql(com.heliorm.def.Where<Town> where) throws OrmException {
        // Typed as SqlDriver, whose protected methods are visible from this package.
        SqlDriver driver = new MySqlDriver();
        var helper = new QueryHelper(driver, field -> field.getSqlName(), driver::fullTableName);
        @SuppressWarnings("unchecked")
        var exec = (ExecutablePart<Town>) orm().select(TOWN, where);
        return helper.buildSelectQuery(exec);
    }

    @Test
    public void equalsTrueUsesOne() throws OrmException {
        var sql = sql(where(TOWN.hasPetshop.eq(true)));
        assertTrue(sql.contains("`hasPetshop`='1'"), sql);
        assertFalse(sql.contains("true"), sql);
    }

    @Test
    public void equalsFalseUsesZero() throws OrmException {
        var sql = sql(where(TOWN.hasPetshop.eq(false)));
        assertTrue(sql.contains("`hasPetshop`='0'"), sql);
        assertFalse(sql.contains("false"), sql);
    }

    @Test
    public void notEqualsUsesNumbersToo() throws OrmException {
        var sql = sql(where(TOWN.hasPetshop.notEq(true)));
        assertTrue(sql.contains("`hasPetshop`<>'1'") || sql.contains("`hasPetshop`!='1'"), sql);
    }

    @Test
    public void combinedWithOtherCriteria() throws OrmException {
        var sql = sql(where(TOWN.name.eq("Paarl").and(TOWN.hasPetshop.eq(true))));
        assertTrue(sql.contains("`hasPetshop`='1'"), sql);
        assertTrue(sql.contains("`name`='Paarl'"), sql);
    }
}
