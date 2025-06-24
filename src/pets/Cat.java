package pets;

import animals.Animal;

public class Cat implements Animal, Pet {
    private final String name;

    public Cat(String name) {
        this.name = name;
    }

    @Override
    public int legs() {
        return 4;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return "Cat(" + name + ")";
    }
}
