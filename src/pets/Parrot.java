package pets;

import animals.Animal;

public record Parrot(String name) implements Animal, Pet {

    @Override
    public int legs() {
        return 2;
    }

    @Override
    public String toString() {
        return "Parrot(" + name + ")";
    }
}

