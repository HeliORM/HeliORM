# HeliORM 
![Java CI with Maven](https://github.com/GideonLeGrange/HeliORM/workflows/Java%20CI%20with%20Maven/badge.svg)

HeliORM is a **code-first** object-relational model for accessing SQL data from Java. It allows **compile time type safe** SQL queries from Java code. 

Here is an example of a type safe query in Java: 

```java
        List<Cat> all = orm().select(CAT)
                .where(CAT.type.eq(CatType.INDOOR))
                .join(PERSON).on(CAT.personId, PERSON.id)
                .where(PERSON.emailAddress.eq(person.getEmailAddress()))
                .orderBy(CAT.name, CAT.age.desc())
                .list();
```

If you find this example interesting, read on.

The idea is that you write your plain old Java Objects (POJOs), or from Java 16 your Records, and then generate a supporting data model that allows type-safe queries, and then use this model and a very simple API to do Structured Query Language (SQL) queries and updates. It is focussed on working with POJOs and is intended to make create, update, read, delete (CRUD) operations easy. It is not meant to be a complete implementation of SQL in Java. 

HeliORM also supports querying on abstract data types.

:warning: This page is currently **very** incomplete


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


## Quick CRUD examples 

In these examples we have a POJO class called `Dog` and a running ORM referenced by `orm`. 

Class `Dog` can more or less look like this:

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


## Retrieving data as lists, streams, singletons or optionals

### Retrieve a list of data

Get all dogs in any order as a list:

```java
List<Dogs> dogs = orm.select(DOG).list();
```

### Get a single object

Get dog with ID 10 if you're sure it exists. If it doesn't exist, an exception will be thrown.

```java

Dog dog = orm.select(DOG)
                .where(DOG.id.eq(10L)
                .one();
```

### Get an optional object 

Get dog with ID 10 if you're not sure it exists. If it doesn't exist, an empty `Optional` is returned.

```java
Optional<Dog> optDog =  orm.select(DOG)
                .where(DOG.id.eq(10L)
                .optional();
```

### Retrieve data as a stream 

Get all dogs in any order as a stream:

```java
Stream<Dogs> dogs = orm.select(DOG).stream();
```

## Ordering 


### Order by sinlge column/field in ascending order 

 Get all dogs ordered by name:

```java
List<Dog> alphaDogs = orm.select(DOG)
   .orderBy(DOG.name)
   .list();
```

### Order by one column/field in descending order

Get all dogs ordered from oldest to youngest:

```java
List<Dog> dogHierarchy = orm.select(DOG)
   .orderBy(DOG.age.desc())
   .list();
```

### Order by a column/field in descending order, other in ascending 

Get all dogs ordered from oldest to youngest:

```java
List<Dog> dogHierarchy = orm.select(DOG)
   .orderBy(DOG.age.desc(), DOG.name)
   .list();
```


## Joining data 


Keep in mind what `Dog` looks like, and take a look at `Person`:

```java
@Pojo
public class Person {


    @PrimaryKey
    private Long id;
    @ForeignKey(pojo = Town.class)
    private Long townId;
    @Column
    private String firstName;
    @Column(nullable = true)
    private String lastName;
    @Column
    private String emailAddress;
    @Column(nullable = true)
    private Double income;
	
}

````

### Select on a joined selection table 

Get all dogs owned by Bob: 

```java
List<Dog> bobsDogs = orm.select(DOG)
    .join(PERSON).on(DOG.personId, PERSON.id)
    .where(PERSON.name.eq("Bob"))
    .list();
```

###  Select on the result table and joined selection table

Get all dogs older than 2 years owned by Bob 

```java
List<Dog> bobsDogs = orm.select(DOG)
	.where(DOG.age.gt(2))
    .join(PERSON).on(DOG.personId, PERSON.id)
    .where(PERSON.name.eq("Bob"))
    .list();
```

