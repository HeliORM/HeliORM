# HeliORM 
![Java CI with Maven](https://github.com/GideonLeGrange/HeliORM/workflows/Java%20CI%20with%20Maven/badge.svg)

A code-first object-relational model for accessing SQL data from Java

```java

#### Get dog with ID 10 if you're sure it exists

Dog dog = orm.select(DOG)
                .where(DOG.id.eq(10L)
                .get();

#### Get dog with ID 10 if you're not sure it exists

Optional<Dog> optDog =  orm.select(DOG)
                .where(DOG.id.eq(10L)
                .optional();

#### Get all dogs in any order
List<Dogs> dogs = orm.select(DOG).list();

#### Get all dogs ordered by name 
List<Dog> alphaDogs = orm.select(DOG)
   .orderBy(DOG.name)
   .list();

#### Get all dogs 2 years in age or younger 
List<Dog> youngDogs = orm.select(DOG)
   .where(DOG.age.le(2))
   .list();

#### Get all dogs owned by Bob 

List<Dog> bobsDogs = orm.select(DOG)
    .join(PERSON).on(DOG.personId, PERSON.id)
    .list();
    
#### 


```
