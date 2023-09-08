package com.heliorm.sql;

import com.heliorm.Orm;
import com.heliorm.OrmException;
import test.persons.Person;
import test.pets.Bird;
import test.pets.Cat;
import test.pets.CatBreed;
import test.pets.CatType;
import test.pets.Dog;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static test.Tables.BIRD;
import static test.Tables.CAT;
import static test.Tables.CATBREED;
import static test.Tables.DOG;
import static test.Tables.PERSON;
import static test.Tables.PROVINCE;
import static test.Tables.TOWN;

class TestData {


    private static final String[] PEOPLE_NAMES = {
            "Oliver",
            "Leo",
            "Milo",
            "Charlie",
            "Max",
            "Jack",
            "Simba",
            "Loki",
            "Oscar",
            "Jasper",
            "Buddy",
            "Tiger",
            "Toby",
            "George",
            "Smokey",
            "Simon",
            "Tigger",
            "Ollie",
            "Louie",
            "Felix",
            "Dexter",
            "Shadow",
            "Finn",
            "Henry",
            "Kitty",
            "Oreo",
            "Gus",
            "Binx",
            "Winston",
            "Sam",
            "Rocky",
            "Gizmo",
            "Sammy",
            "Jax",
            "Sebastian",
            "Blu",
            "Theo",
            "Beau",
            "Salem",
            "Chester",
            "Lucky",
            "Frankie",
            "Boots",
            "Cooper",
            "Thor",
            "Bear",
            "Romeo",
            "Teddy",
            "Bandit",
            "Ziggy",
            "Apollo",
            "Pumpkin",
            "Boo",
            "Zeus",
            "Bob",
            "Tucker",
            "Jackson",
            "Tom",
            "Cosmo",
            "Bruce",
            "Murphy",
            "Buster",
            "Midnight",
            "Moose",
            "Merlin",
            "Frank",
            "Joey",
            "Thomas",
            "Harley",
            "Prince",
            "Archie",
            "Tommy",
            "Marley",
            "Otis",
            "Casper",
            "Harry",
            "Benny",
            "Percy",
            "Bentley",
            "Jake",
            "Ozzy",
            "Ash",
            "Sylvester",
            "Mickey",
            "Fred",
            "Walter",
            "Clyde",
            "Pepper",
            "Calvin",
            "Tux",
            "Stanley",
            "Garfield",
            "Louis",
            "Mowgli",
            "Mac",
            "Luke",
            "Sunny",
            "Duke",
            "Hobbes",
            "Remi"
    };

    private static final String[][] TOWN_NAMES = {
            {"Somerset West",
                    "Gordons Bay",
                    "Stellenbosch"},
            {"Vreyheid", "Durban", "Pinetown"}
    };

    private static final String[] PROVINCE_NAMES = {
            "Western Cape",
            "Kwazulu-Natal"
    };

    private static final Random random = new Random();


    static List<Province> makeProvinces(Orm orm) {
        return Arrays.stream(PROVINCE_NAMES)
                .map(name -> {
                    try {
                        return orm.create(PROVINCE)
                                .with(PROVINCE.name, name)
                                .build();
                    } catch (OrmException e) {
                        throw new RuntimeException(e);
                    }

                }).collect(Collectors.toList());
    }

    static List<Town> makeTowns(Orm orm, List<Province> provinces) throws OrmException {
        List<Town> towns = new ArrayList<>();
        for (int i = 0; i < TOWN_NAMES.length; ++i) {
            String[] names = TOWN_NAMES[i];
            Province province = provinces.get(i);
            for (String name : names) {
                Town town = orm.create(TOWN)
                        .with(TOWN.provinceId, province.provinceId())
                        .with(TOWN.name, name)
                        .build();
                towns.add(town);
            }
        }
        return towns;
    }

    static Cat makeCat(Orm orm, Person person, CatBreed breed) throws OrmException {
        int age = new Random().nextInt(18);
        return orm.create(CAT)
                .with(CAT.age, age)
                .with(CAT.name, PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)])
                .with(CAT.personId, person.id())
                .with(CAT.birthday, makeBirthDay(age))
                .with(CAT.type, random.nextBoolean() ? CatType.INDOOR : CatType.OUTDOOR)
                .with(CAT.breedId, breed.id())
                .build();
    }

    static Dog makeDog(Orm orm, Person person) throws OrmException {
        int age = new Random().nextInt(12);
        return orm.create(DOG)
                .with(DOG.age, age)
                .with(DOG.name, PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)])
                .with(DOG.personId, person.id())
                .with(DOG.birthday, makeBirthDay(age))
                .build();
    }

    static Bird makeBird(Orm orm, Person person) throws OrmException {
        int age = new Random().nextInt(9);
        return orm.create(BIRD)
                .with(BIRD.age, age)
                .with(BIRD.name, PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)])
                .with(BIRD.personId, person.id())
                .with(BIRD.birthday, makeBirthDay(age))
                .with(BIRD.type, random.nextBoolean() ? Bird.Type.CAGED : Bird.Type.FREERANGE)
                .with(BIRD.singTime, random.nextInt(31))
                .build();
    }

    static Person makePerson(Orm orm, long n) throws OrmException {
        return orm.create(PERSON)
                .with(PERSON.firstName, PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)])
                .with(PERSON.lastName, random.nextInt(5) == 0 ? null :
                        PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)])
                .with(PERSON.emailAddress, PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)].toLowerCase() + n + "@gmail.com")
                .with(PERSON.income, random.nextDouble() * 10000)
                .with(PERSON.townId, n)
                .build();
    }

    static List<Person> makePersons(Orm orm, List<Town> towns) throws OrmException {
        List<Person> persons = new ArrayList<>();
        for (Town town : towns) {
            for (int i = 0; i < 4; ++i) {
                String first = PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)];
                String last = PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)];
                if (random.nextInt(5) == 0) {
                    last = null;
                }
                Person person = makePerson(orm, town.id());
                persons.add(person);
            }

        }
        return persons;
    }

    static List<Cat> makeCats(Orm orm, int n, List<Person> persons, List<CatBreed> breeds) throws OrmException {
        List<Cat> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeCat(orm, persons.get(random.nextInt(persons.size())), breeds.get(random.nextInt(breeds.size()))));
        }
        return res;
    }

    static List<CatBreed> makeCatBreeds(Orm orm) throws OrmException {
        List<CatBreed> res = new ArrayList<>();
        res.add(makeCatBreed(orm, "Persian"));
        res.add(makeCatBreed(orm,"Brithish Blue"));
        res.add(makeCatBreed(orm, "African Wild Cat"));
        return res;
    }

    static CatBreed makeCatBreed(Orm orm, String name) throws OrmException {
        return orm.create(CATBREED).with(CATBREED.name, name).build();
    }


    static List<Dog> makeDogs(Orm orm, int n, List<Person> persons) throws OrmException {
        List<Dog> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeDog(orm, persons.get(random.nextInt(persons.size()))));
        }
        return res;
    }

    static List<Bird> makeBirds(Orm orm, int n, List<Person> persons) throws OrmException {
        List<Bird> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeBird(orm, persons.get(random.nextInt(persons.size()))));
        }
        return res;
    }

    private static Date makeBirthDay(int age) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, -age);
        cal.add(Calendar.DATE, random.nextInt(364));
        return cal.getTime();
    }

}
