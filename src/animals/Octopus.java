package animals;

public class Octopus implements Animal {
    @Override
    public int legs() {
        return 8;
    }

    @Override
    public String toString() {
        return "Octopus";
    }
}

