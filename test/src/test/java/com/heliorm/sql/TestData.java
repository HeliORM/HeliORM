package com.heliorm.sql;

import test.persons.Person;
import test.pets.Bird;
import test.pets.Cat;
import test.pets.CatBreed;
import test.pets.CatType;
import test.pets.Dog;
import test.pets.Pet;
import test.place.Province;
import test.place.Town;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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

    private static Random random = new Random();
    private List<Province> provinces = new ArrayList<>();

    private TestData() {
    }

    static List<Province> makeProvinces() {
        return Arrays.stream(PROVINCE_NAMES)
                .map(name -> {
                    Province province = new Province();
                    province.setName(name);
                    return province;

                }).collect(Collectors.toList());
    }

    static List<Town> makeTowns(List<Province> provinces) {
        List<Town> towns = new ArrayList<>();
        for (int i = 0; i < TOWN_NAMES.length; ++i) {
            String names[] = TOWN_NAMES[i];
            Province province = provinces.get(i);
            for (String name : names) {
                Town town = new Town();
                town.setProvinceId(province.getProvinceId());
                town.setName(name);
                towns.add(town);
            }
        }
        return towns;
    }

    static Cat makeCat(Person person, CatBreed breed) {
        Cat cat = new Cat();
        cat = makePet(cat, person);
        cat.setType(random.nextBoolean() ? CatType.INDOOR : CatType.OUTDOOR);
        cat.setBreedId(breed.getId());
        return cat;
    }


    static Dog makeDog(Person person) {
        Dog dog = new Dog();
        dog = makePet(dog, person);
        return dog;
    }

    static Bird makeBird(Person person) {
        Bird bird = new Bird();
        bird = makePet(bird, person);
        bird.setType(random.nextBoolean() ? Bird.Type.CAGED : Bird.Type.FREERANGE);
        bird.setSingTime(Duration.ofMinutes(random.nextInt(31)));
        return bird;
    }

    static Person makePerson(int n) {
        Person person = new Person();
        person.setFirstName(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)]);
        if (random.nextInt(5) == 0) {
            person.setLastName(null);
        }
        else {
            person.setLastName(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)]);
        }
        person.setEmailAddress(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)].toLowerCase() + n + "@gmail.com");
        person.setIncome(random.nextDouble()*10000);
        return person;
    }

    static List<Person> makePersons(List<Town> towns) {
        List<Person> persons = new ArrayList<>();
        for (Town town : towns) {
            for (int i = 0; i < 4; ++i) {
                String first = PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)];
                String last = PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)];
                if (random.nextInt(5) == 0) {
                    last = null;
                }
                Person person = new Person();
                person.setFirstName(first);
                person.setLastName(last);
                person.setEmailAddress(first + "." + last + i + "@gmail.com");
                person.setTownId(town.getId());
                person.setIncome(random.nextDouble()*10000);
                persons.add(person);
            }

        }
        return persons;
    }

    static List<Cat> makeCats(int n, List<Person> persons, List<CatBreed> breeds) {
        List<Cat> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeCat(persons.get(random.nextInt(persons.size())),breeds.get(random.nextInt(breeds.size()))));
        }
        return res;
    }

    static List<CatBreed> makeCatBreeds() {
        List<CatBreed> res = new ArrayList<>();
        res.add(makeCatBreed("Persian"));
        res.add(makeCatBreed("Brithish Blue"));
        res.add(makeCatBreed("African Wild Cat"));
        return res;
    }

    static CatBreed makeCatBreed(String name) {
        CatBreed breed = new CatBreed();
        breed.setName(name);
        return breed;
    }


    static List<Dog> makeDogs(int n, List<Person> persons) {
        List<Dog> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeDog(persons.get(random.nextInt(persons.size()))));
        }
        return res;
    }
    static List<Bird> makeBirds(int n, List<Person> persons) {
        List<Bird> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeBird(persons.get(random.nextInt(persons.size()))));
        }
        return res;
    }
    private static <P extends Pet> P makePet(P pet, Person person) {
        pet.setAge(random.nextInt(18));
        pet.setName(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)]);
        pet.setPersonId(person.getId());
        pet.setBirthday(makeBirthDay(pet.getAge()));
        return pet;
    }

    private static Date makeBirthDay(int age) {
        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.YEAR, -age);
        cal.add(Calendar.DATE, random.nextInt(364));
        return cal.getTime();
    }

}
