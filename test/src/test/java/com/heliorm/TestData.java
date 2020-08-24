package com.heliorm;

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

    private TestData() {}

    static Cat makeCat() {
        Cat cat = new Cat();
        cat = makePet(cat);
        cat.setType(random.nextBoolean() ? Cat.Type.INDOOR : Cat.Type.OUTDOOR);
        return cat;
    }


    static Dog makeDog() {
        Dog dog = new Dog();
        dog = makePet(dog);
        return dog;
    }

    static List<Cat> makeCats(int n) {
        List<Cat> res = new ArrayList<>();
        for (int i = 0; i < n; i++ ) {
            res.add(makeCat());
        }
        return res;
    }

    static List<Dog> makeDogs(int n) {
        List<Dog> res = new ArrayList<>();
        for (int i = 0; i < n; i++ ) {
            res.add(makeDog());
        }
        return res;
    }

    private static <P extends Pet> P makePet(P pet) {
        pet.setAge(random.nextInt(18));
        pet.setName(names[random.nextInt(names.length)]);
        pet.setPersonNumber((long) random.nextInt(5));
        return pet;
    }

}
