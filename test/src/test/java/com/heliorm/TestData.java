package com.heliorm;

import test.persons.Person;
import test.pets.Cat;
import test.pets.Dog;
import test.pets.Pet;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TestData {

    private static final String[] names = {
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
    private static Random random = new Random();

    private TestData() {
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
        person.setFirstName(names[random.nextInt(names.length)]);
        person.setLastName(names[random.nextInt(names.length)]);
        person.setEmailAddress(names[random.nextInt(names.length)].toLowerCase()+ n +"@gmail.com");
        return person;
    }

    static List<Person> makePersons(int n) {
        List<Person> res = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            res.add(makePerson(i));
        }
        return res;
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
        pet.setName(names[random.nextInt(names.length)]);
        pet.setPersonNumber(person.getId());
        return pet;
    }

}
