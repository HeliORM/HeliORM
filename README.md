
# helicopter-orm
A simple type safe Java Object Relational Mapper (ORM) 

# About

Helicopter ORM provides a type safe, Java centric way of accessing relational data 
(typically stored in a SQL database). It has a few nifty features, but before we get
into that, it may be best to start with a few examples. 


```java
Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/test?user=dbuser&password=dbpass");
Orm orm = Orm.open(con, Orm.Dialect.MYSQL);

List<Person> persons = orm.select(PERSON)
    .where(PERSON.name).eq("Bob")
    .list();

```

```java
List<Person> persons = orm.select(PERSON)
  .orderBy(PERSON.name)
  .list(); 
```

**Work in progress**

## Cool features we want 

* Mojo to generate meta databa based on POJO structures
  * Ability to write different types of meta data generation via generators.
    * Pure POJO generator 
    * Annotations to influence meta data generation. Use JPA standard 
    * Heuristic-based generators? 
* POJO CRUD:
  * Create, update, delete by passing the POJO 
  * Read by having a SQL-ish query language 
* Ability to query on Abstract class
  * If multiple POJO classes have the same super class (but not java.lang.Object) generate meta data for that type and allow queries using that type. 
  * This implies meta data hierachy following POJO hierachy
* Data validation:
  * Generate meta data that can generate SQL that applies validation rules 
  * Generate meta data that allows framework to do pre-SQL validation 
  * Annotations to support validation. Use Java Bean Validation standard 


