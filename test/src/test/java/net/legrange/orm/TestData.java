package net.legrange.orm;

import test.pets.Cat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class TestData {

    private static final String[] names = {
        "Arnold",
        "Billy",
        "Chesire",
        "Darrel",
        "Edward",
        "Frankie"
    };
    private static Random random = new Random();

    private TestData() {}

    static Cat makeCat() {
        Cat cat = new Cat();
        cat.setType(random.nextBoolean() ? Cat.Type.INDOOR : Cat.Type.OUTDOOR);
        cat.setAge(random.nextInt(18));
        cat.setName(names[random.nextInt(names.length)]);
        cat.setPersonNumber((long) random.nextInt(5));
        return cat;
    }

    static List<Cat> makeCats() {
        List<Cat> res = new ArrayList<>();
        for (int i = 0; i < 20; i++ ) {
            res.add(makeCat());
        }
        return res;
    }

}
