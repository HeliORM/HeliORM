package com.heliorm;

import test.persons.Person;
import test.pets.Cat;
import test.pets.Dog;
import test.pets.Pet;
import test.place.Province;
import test.place.Town;

import java.util.ArrayList;
import java.util.Arrays;
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

    static Cat makeCat(Person person) {
        Cat cat = new Cat();
        cat = makePet(cat, person);
        cat.setType(random.nextBoolean() ? Cat.Type.INDOOR : Cat.Type.OUTDOOR);
        return cat;
    }


    static Dog makeDog(Person person) {
        Dog dog = new Dog();
        dog = makePet(dog, person);
        return dog;
    }


    static Person makePerson(int n) {
        Person person = new Person();
        person.setFirstName(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)]);
        person.setLastName(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)]);
        person.setEmailAddress(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)].toLowerCase() + n + "@gmail.com");
        return person;
    }

    static List<Person> makePersons(List<Town> towns) {
        List<Person> persons = new ArrayList<>();
        for (Town town : towns) {
            for (int i = 0; i < 4; ++i) {
                String first = PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)];
                String last = PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)];
                Person person = new Person();
                person.setFirstName(first);
                person.setLastName(last);
                person.setEmailAddress(first + "." + last + i + "@gmail.com");
                person.setTownId(town.getProvinceId());
                persons.add(person);
            }

        }
        return persons;
    }

    static List<Cat> makeCats(int n, List<Person> persons) {
        List<Cat> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeCat(persons.get(random.nextInt(persons.size()))));
        }
        return res;
    }

    static List<Dog> makeDogs(int n, List<Person> persons) {
        List<Dog> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makeDog(persons.get(random.nextInt(persons.size()))));
        }
        return res;
    }

    private static <P extends Pet> P makePet(P pet, Person person) {
        pet.setAge(random.nextInt(18));
        pet.setName(PEOPLE_NAMES[random.nextInt(PEOPLE_NAMES.length)]);
        pet.setPersonId(person.getId());
        return pet;
    }

}
