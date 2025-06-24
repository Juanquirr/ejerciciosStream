package animals;

public class Centipede implements Animal {
    @Override
    public int legs() {
        return 100;
    }

    @Override
    public String toString() {
        return "Centipede";
    }
}

