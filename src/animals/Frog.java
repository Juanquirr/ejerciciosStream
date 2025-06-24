package animals;

public class Frog implements Animal {
    @Override
    public int legs() {
        return 4;
    }

    @Override
    public String toString() {
        return "Frog";
    }
}

