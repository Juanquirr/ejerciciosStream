package animals;

public class Ant implements Animal {
    @Override
    public int legs() {
        return 6;
    }

    @Override
    public String toString() {
        return "Ant";
    }
}

