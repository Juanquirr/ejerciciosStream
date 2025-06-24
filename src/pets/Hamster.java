package pets;

import animals.Animal;

public class Hamster implements Animal, Pet {
    private final String name;

    public Hamster(String name) {
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
        return "Hamster(" + name + ")";
    }
}

