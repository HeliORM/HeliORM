# HeliORM 
![Java CI with Maven](https://github.com/GideonLeGrange/HeliORM/workflows/Java%20CI%20with%20Maven/badge.svg)

A code-first object-relational model for accessing SQL data from Java

```java

Dog dog = orm.select(DOG)
                .where(DOG.id.eq(10L)
                .get();

Optional<Dog> optDog =  orm.select(DOG)
                .where(DOG.id.eq(10L)
                .optional();

List<Dogs> dogs = orm.select(DOG).list();


List<Dog> youngDogs = orm.select(DOG)
   .where(DOG.age.le(2))
   .list();

List<Dog> alphaDogs = orm.select(DOG)
   .orderBy(DOG.name)
   .list();


```
