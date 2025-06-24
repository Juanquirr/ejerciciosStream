package animals;

public class Kangaroo implements Animal {
    @Override
    public int legs() {
        return 2;
    }

    @Override
    public String toString() {
        return "Kangaroo";
    }
}

