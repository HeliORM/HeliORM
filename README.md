# HeliORM 
![Java CI with Maven](https://github.com/GideonLeGrange/HeliORM/workflows/Java%20CI%20with%20Maven/badge.svg)

HeliORM is a code-first object-relational model for accessing SQL data from Java. It allows compile time type safe SQL queries from Java code. 

The idea is that you write your plain old Java Objects (POJOs), or from Java 16 your Records, and then generate a supporting data model that allows type-safe queries, and then use this model and a very simple API to do Structured Query Language (SQL) queries and updates. It is focussed on working with POJOs and is intended to make create, update, read, delete (CRUD) operations easy. It is not meant to be a complete implementation of SQL in Java. 

HeliORM also supports querying on abstract data types.

:warning: This page is currently **very** incomplete

Here is a quick taste of what it is like to use HeliORM to query:


## Quick CRUD examples 

In these examples we have a POJO class called `Dog` and a running ORM referenced by `orm`. 

### Create 

Create a new 3-year old dog, and call it 'Fido':

```java
   Dog dog = new Dog();
   dog.setName("Fido"); // The classics are the best
   dog.setAge(3);
   dog.setPersonId(1);
   // code ommitted to set values in dog 
   dog = orm.create(dog);
```

### Read 

Read the dog called 'Fido':

```java 
   Dog dog = orm.select(DOG)
   .where(DOG.name.eq("Fido"))
   .one();
```

### Update

Change a dog's name and save it:

```java 
   // assume variable dog references a Dog object is loaded 
   dog.setName("Rex"); // I've renamed a dog IRL once 
   dog = orm.update(dog);
```

### Delete

Delete a dog:

```java
   // assume variable dog references a Dog object is loaded 
   orm.delete(dog); // Rip Rex 
```

## Getting HeliORM

I recommend using Maven or your prefered package management technology to add HeliORM To your project. You'll need at least the `core` library, and most likely a SQL driver library, and if you wish to use annotations in your POJOs, the annotation library.

### The core library 

```xml
  <dependency>
    <groupId>com.heliorm</groupId>
     <artifactId>core</artifactId>
     <version>0.91</version>
  </dependency>
```

### Annotations to annotate your POJOs

```xml
  <dependency>
    <groupId>com.heliorm</groupId>
     <artifactId>annotation</artifactId>
     <version>0.9</version>
  </dependency>
```

### The MySQL driver 

```xml
  <dependency>
    <groupId>com.heliorm</groupId>
     <artifactId>mysql</artifactId>
     <version>0.9</version>
  </dependency>
```


## More exmamples

### Query using concrete POJOs

Assume there is a Object `Dog` which looks like this:

```java
@Pojo
public class Dog {
    @PrimaryKey(autoIncrement = true)
    private Long id;

    @ForeignKey(pojo = Person.class)
    private Long personId;
    @Column(length = 32)
    private String name;
    private int age;
}
    
```

`Dog` is annotated with `@Pojo` and some of it's fields are annotated as well. We'll explain more about annotations later



#### Get dog with ID 10 if you're sure it exists

```java


Dog dog = orm.select(DOG)
                .where(DOG.id.eq(10L)
                .one();
```

#### Get dog with ID 10 if you're not sure it exists
```java

Optional<Dog> optDog =  orm.select(DOG)
                .where(DOG.id.eq(10L)
                .optional();
```

#### Get all dogs in any order
```java
List<Dogs> dogs = orm.select(DOG).list();
```

#### Get all dogs ordered by name 
```java

List<Dog> alphaDogs = orm.select(DOG)
   .orderBy(DOG.name)
   .list();
```

#### Get all dogs 2 years in age or younger 
```java

List<Dog> youngDogs = orm.select(DOG)
   .where(DOG.age.le(2))
   .list();
```


#### Get all dogs owned by Bob 

```java
List<Dog> bobsDogs = orm.select(DOG)
    .join(PERSON).on(DOG.personId, PERSON.id)
    .where(PERSON.name.eq("Bob"))
    .list();
```

