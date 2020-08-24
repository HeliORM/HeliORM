# HeliORM 

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
   .where(DOG.age.lt(5))
   .list();

List<Dog> youngDogs = orm.select(DOG)
   .orderBy(DOG.name)
   .list();


```