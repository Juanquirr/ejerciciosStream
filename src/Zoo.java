import java.util.*;

public class Zoo {

    public interface Pet {
        String name();
    }

    public interface Animal {
        int legs();
    }

    public static class Spider implements Animal {
        private final int legs;

        public Spider() {
            this.legs = 8;
        }

        @Override
        public int legs() {
            return legs;
        }

        @Override
        public String toString() {
            return "Spider";
        }
    }

    public static class Cat implements Animal, Pet {
        private final String name;
        private final int legs;

        public Cat(String name) {
            this.name = name;
            this.legs = 4;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int legs() {
            return legs;
        }

        @Override
        public String toString() {
            return "Cat: " + name;
        }
    }

    public static class Parrot implements Animal, Pet {
        private final String name;

        public Parrot(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int legs() {
            return 2;
        }

        @Override
        public String toString() {
            return "Parrot: " + name;
        }
    }

    public static class Capybara implements Animal, Pet {
        private final String name;

        public Capybara(String name) {
            this.name = name;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int legs() {
            return 4;
        }

        @Override
        public String toString() {
            return "Capybara: " + name;
        }
    }

    public static void main(String[] args) {
        List<Animal> animals = List.of(
                new Spider(),
                new Cat("Nala"),
                new Parrot("Kiko"),
                new Capybara("Coco"),
                new Spider()
        );

        // 1.
        System.out.println(list);
    }
}
List<Animal> list = animals.stream().filter(a -> !(a instanceof Pet)).toList();

