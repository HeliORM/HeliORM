[![Board Status](https://dev.azure.com/GideonLeGrange/b9854740-2080-4dd5-897e-a75c8a80fa64/e00c4bb2-cc8b-416e-971b-d59d8c4d97d9/_apis/work/boardbadge/202dde8b-5992-4874-be1e-5479605af8b0)](https://dev.azure.com/GideonLeGrange/b9854740-2080-4dd5-897e-a75c8a80fa64/_boards/board/t/e00c4bb2-cc8b-416e-971b-d59d8c4d97d9/Microsoft.RequirementCategory)
# helicopter-orm
A pachy attempt at making a type safe Java ORM

# Design Decisions

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


