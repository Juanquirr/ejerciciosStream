import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.stream.Collectors;

import animals.*;
import jdk.jfr.Period;
import pets.*;

record Country(String code, String name, String continent, double surfaceArea,
               int population, double gnp, int capital, List<City> cities) {
    public record City(int id, String name, int population) { }
}

public class Main {
    private static List<Country> countries;
    private static List<Movie> movies;
    private static List<Animal> animals;
    public static void main(String[] args) throws FileNotFoundException {
        initializeCountries();
        initializeMovies();
        initializeAnimals();

        /// DICTIONARY
        BufferedReader bufferedReader = new BufferedReader(new FileReader("src/0_palabras_todas.txt"));
        List<String> lines = bufferedReader.lines().toList();
        // 1. Encontrar las palabras que comienzan con las letras de la "a" a la "m".
        List<String> list = lines.stream().filter(word -> word.charAt(0) < 'm' ).toList();

        // 2. Encontrar las palabras que comienzan con la letra "n" hasta el final del diccionario.
        List<String> nToEnd = lines.stream().filter(word -> word.charAt(0) >= 'n').toList();

        // 3. Agrupar las palabras del diccionario por sus tres primeras letras.
        Map<String, List<String>> wordsBy3letters = lines.stream().collect(Collectors.groupingBy(
                word -> getFirstLetters(word)
        ));

        // 4. Encontrar los palíndromos en el diccionario. Un palíndromo es una palabra, número, frase u otra secuencia de caracteres que se lee igual de izquierda a derecha y viceversa, como "madam" o "racecar".
        List<String> palyndroms = lines.stream().filter(word -> isPalindrome(word)).toList();

        // 5. Contar las vocales utilizadas en las palabras.
        Map<String, Integer> vowelsPerWord = lines.stream().collect(Collectors.toMap(
                word -> word,
                word -> vowelsInWord(word)
        ));

        // 6. Encontrar las palabras que comienzan con la letra "a" y terminan con la letra "z"
        List<String> aStartzEnd = lines.stream().filter(word -> word.charAt(0) == 'a' && word.charAt(word.length() - 1) == 'z').toList();

        // 7. Encontrar la palabra más larga en el diccionario.
        String s = lines.stream().min((a, b) -> b.length() - a.length()).get();


        /// MOVIES
        // 1. Obtener el número de películas de cada director
        Map<String, Long> moviesPerDirector = movies.stream()
                .flatMap(movie -> movie.directors().stream())
                .collect(Collectors.groupingBy(
                        director -> director.name(),
                        Collectors.counting()
                ));

        // 2. Obtener el número de géneros de las películas de cada director
        Map<String, Long> genresPerDirector = movies.stream()
                .flatMap(movie -> movie.directors().stream()).distinct()
                .collect(Collectors.toMap(
                        director -> director.name(),
                        director -> movies.stream()
                                .filter(movie -> movie.directors().contains(director))
                                .flatMap(movies -> movies.genres().stream())
                                .distinct().count()
                ));

        // 3. Obtener la lista de películas que solo tienen los géneros "Drama" y "Comedia"
        List<String> movieDramComedia = movies.stream()
                .filter(movie -> movie.genres().size() == 2)
                .filter(movie -> movie.genres().stream()
                        .allMatch(genre -> genre.name().equals("Drama") || genre.name().equals("Comedia")))
                .map(Movie::title)
                .toList();

        // 4. Agrupar las películas por año y enumerarlas
        Map<Integer, Long> moviesPerYear = movies.stream().collect(Collectors.groupingBy(
                Movie::year,
                Collectors.counting()
        ));
        System.out.println(moviesPerYear);

        // 5. Encontrar el año en el que hay disponibles la mayor cantidad de películas.
        Integer year = movies.stream().collect(Collectors.groupingBy(
                        Movie::year,
                        Collectors.counting()
                )).entrySet().stream()
                .max((a, b) -> a.getValue().compareTo(b.getValue()))
                .get()
                .getKey();

        /// COUNTRIES
        // 1. Encontrar la ciudad más poblada de cada continente.
        Map<String, String> mostPopulatedCityOfContinent = countries.stream()
                .map(Country::continent)
                .distinct()
                .collect(Collectors.toMap(
                        continent -> continent,
                        continent -> countries.stream()
                                .filter(country -> country.continent().equals(continent))
                                .flatMap(country -> country.cities().stream())
                                .max((a, b) -> a.population() - b.population())
                                .get()
                                .name()
                ));
        // 2. Encontrar la capital más poblada.
        String capitalPopulated = countries.stream()
                .flatMap(country -> country.cities().stream()
                        .filter(city -> city.id() == country.capital()))
                .max((a, b) -> a.population() - b.population()).get().name();

        // 3. Encontrar la capital más poblada de cada continente.
        Map<String, String> capitalPerContinent = countries.stream().map(Country::continent)
                .distinct()
                .collect(Collectors.toMap(
                        continent -> continent,
                        continent -> countries.stream()
                                .filter(country -> country.continent().equals(continent))
                                .flatMap(country -> country.cities().stream()
                                        .filter(city -> city.id() == country.capital()))
                                .max((a, b) -> a.population() - b.population())
                                .get().name()
                ));

        // 4. Ordenar los países por número de ciudades en orden descendente.
        List<Map.Entry<String, Long>> citiesPerCountry = countries.stream()
                .collect(Collectors.toMap(
                        country -> country.name(),
                        country -> countries.stream()
                                .filter(c -> c.equals(country))
                                .flatMap(c -> c.cities().stream())
                                .count()
                )).entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue())).toList();

        List<String> citiesPerCountryDescendent = countries.stream()
                .sorted((a, b) -> b.cities().size() - a.cities().size()).map(Country::name).toList();

        // 5. Ordenar los países por densidad de población en orden descendente, ignorando los países con una población de cero.
        List<String> densityCountries = countries.stream()
                .sorted((a, b) -> (int) ((b.population() / b.surfaceArea()) - (a.population() / a.surfaceArea())))
                .map(Country::name).toList();

        // 6. Encontrar el país más rico de cada continente en términos de su PNB (Producto Nacional Bruto).
        Map<String, String> richestCountryByContinent = countries.stream()
                .map(Country::continent)
                .distinct()
                .collect(Collectors.toMap(
                        continent -> continent,
                        continent -> countries.stream()
                                .filter(c -> c.continent().equals(continent))
                                .max((a, b) -> (int) (a.gnp() - b.gnp()))
                                .get().name()
                ));

        // 7. Encontrar la población mínima, máxima y promedio de los países del mundo.
        IntSummaryStatistics statistics = countries.stream()
                .mapToInt(Country::population).summaryStatistics();

        // 8. Encontrar la población mínima, máxima y promedio de cada continente.
        Map<String, IntSummaryStatistics> summaryOverContinent = countries.stream()
                .map(Country::continent)
                .distinct()
                .collect(Collectors.toMap(
                        continent -> continent,
                        continent -> countries.stream()
                                .filter(c -> c.continent().equals(continent))
                                .mapToInt(Country::population).summaryStatistics()
                ));

        // 9. Encontrar los países con la población mínima y máxima.
        countries.stream().min((a,b) -> a.population() - b.population()).get().name();
        countries.stream().max((a,b) -> a.population() - b.population()).get().name();

        countries.stream()
                .filter(country -> country.name().equals(
                                countries.stream().min((a, b) -> a.population() - b.population()).get().name()
                        ) || country.name().equals(
                                countries.stream().max((a, b) -> a.population() - b.population()).get().name())
                )
                .map(Country::name).toList();

        // 10. Encontrar los países de cada continente con la población mínima y máxima.
        Map<String, List<String>> countriesPerContinentWithStats = countries.stream().map(Country::continent).distinct()
                .collect(Collectors.toMap(
                        continent -> continent,
                        continent -> countries.stream()
                                .filter(c -> c.continent().equals(continent))
                                .filter(country -> country.name().equals(
                                                countries.stream()
                                                        .filter(c -> c.continent().equals(continent))
                                                        .min((a, b) -> a.population() - b.population())
                                                        .get().name()
                                        ) || country.name().equals(
                                                countries.stream()
                                                        .filter(c -> c.continent().equals(continent))
                                                        .max((a, b) -> a.population() - b.population())
                                                        .get().name())
                                )
                                .map(Country::name).toList()
                ));

        // 11. Agrupar los países por continente y luego ordena los países dentro de cada continente por número de ciudades.
        Map<String, List<String>> agrupation = countries.stream()
                .map(Country::continent)
                .distinct()
                .collect(Collectors.toMap(
                        continent -> continent,
                        continent -> countries.stream()
                                .filter(country -> country.continent().equals(continent))
                                .sorted((a, b) -> a.cities().size() - b.cities().size())
                                .map(Country::name)
                                .toList()
                ));

        // 12. Encontrar las ciudades con la población mínima y máxima en cada país.
        Map<String, List<String>> citiesMinMax = countries.stream().collect(Collectors.toMap(
                        country -> country.name(),
                        country -> country.cities().stream()
                                .filter(city ->
                                        city.name().equals(country.cities().stream()
                                                .max((a, b) -> a.population() - b.population()).get().name()) ||
                                                city.name().equals(country.cities().stream()
                                                        .min((a, b) -> a.population() - b.population()).get().name())
                                ).map(Country.City::name).toList()
                )
        );

        // 13. Encontrar el valor mínimo, máximo, promedio y desviación estándar de los valores de PNB.
        DoubleSummaryStatistics doubleSummaryStatistics = countries.stream().mapToDouble(Country::gnp).summaryStatistics();
        // No voy a dimitir.


        /// ANIMALS
        //1. Obtener una lista de animales salvajes
        List<Animal> wildAnimals = animals.stream().filter(animal -> !(animal instanceof Pet)).toList();

        //2. Obtener una lista de mascotas
        List<Animal> pets = animals.stream().filter(animal -> animal instanceof Pet).toList();

        //3. Encontrar el animal con el mayor número de patas
        Animal mostLeggedAnimal = animals.stream().max((a, b) -> a.legs() - b.legs()).get();

        //4. Obtener una lista de 100 animales al azar
        List<Animal> hundredAnimals = animals.stream()
                .sorted((a, b) -> new Random().nextInt())
                .limit(100)
                .toList();

        //5. Encontrar el número total de patas
        long totalOfLegsSummary = animals.stream().mapToInt(Animal::legs).summaryStatistics().getSum();
        int totalOfLegs = animals.stream().mapToInt(Animal::legs).sum();

        //6. Agrupar los animales según el número de patas
        Map<Integer, List<Animal>> animalsPerLegs = animals.stream().collect(Collectors.groupingBy(
                Animal::legs
        ));

        //7. Contar el número de animales en cada especie
        Map<String, Long> animalsPerSpecie = animals.stream().collect(Collectors.groupingBy(
                animal -> animal.getClass().getSimpleName(),
                Collectors.counting()
        ));

        //8. Contar el número de especies
        int numberOfSpecies = animals.stream().collect(Collectors.groupingBy(
                animal -> animal.getClass()
        )).size();
        long numberOfSpecies2 = animals.stream().map(animal -> animal.getClass()).distinct().count();

    }

    private static int vowelsInWord(String word) {
        Set<Character> vowels = Set.of('a', 'e', 'i', 'o', 'u');
        int numberOfVowels = 0;
        for (int i = 0; i < word.length(); i++) if (vowels.contains(word.charAt(i))) numberOfVowels++;
        return numberOfVowels;
    }

    private static boolean isPalindrome(String word) {
        for (int i = 0; i < word.length(); i++) if (word.charAt(i) != word.charAt(word.length() - 1 - i)) return false;
        return true;
    }

    private static String getFirstLetters(String word) {
        if (word.length() < 4) return word;
        return word.substring(0, 3);
    }

    private static void initializeMovies() {
        movies = new ArrayList<>();
        // Géneros más realistas
        List<Movie.Genre> genres = List.of(
                new Movie.Genre(0, "Drama"),
                new Movie.Genre(1, "Comedia"),
                new Movie.Genre(2, "Acción"),
                new Movie.Genre(3, "Ciencia Ficción"),
                new Movie.Genre(4, "Aventura")
        );

        // Directores reales
        List<Movie.Director> directors = List.of(
                new Movie.Director(0, "George", "Lucas"),
                new Movie.Director(1, "Steven", "Spielberg"),
                new Movie.Director(2, "Christopher", "Nolan"),
                new Movie.Director(3, "Quentin", "Tarantino"),
                new Movie.Director(4, "Peter", "Jackson"),
                new Movie.Director(5, "David", "Fincher")
        );

        // Añadiendo películas
        movies.add(new Movie(0, "Star Wars: A New Hope", 1977, "Una rebelión contra el Imperio Galáctico.",
                List.of(genres.get(3), genres.get(4)), List.of(directors.get(0))));
        movies.add(new Movie(1, "Jurassic Park", 1993, "Un parque temático con dinosaurios vivos.",
                List.of(genres.get(4), genres.get(1)), List.of(directors.get(1))));
        movies.add(new Movie(2, "Inception", 2010, "Un ladrón que roba secretos a través de sueños.",
                List.of(genres.get(3), genres.get(0)), List.of(directors.get(2))));
        movies.add(new Movie(3, "Pulp Fiction", 1994, "Historias entrelazadas del bajo mundo de Los Ángeles.",
                List.of(genres.get(0), genres.get(1)), List.of(directors.get(3))));
        movies.add(new Movie(4, "The Lord of the Rings: The Fellowship of the Ring", 2001, "Un grupo de héroes lucha contra el mal en la Tierra Media.",
                List.of(genres.get(4), genres.get(0)), List.of(directors.get(4))));
        movies.add(new Movie(5, "Star Wars: The Empire Strikes Back", 1980, "La batalla contra el Imperio continúa.",
                List.of(genres.get(3), genres.get(4)), List.of(directors.get(0))));
        movies.add(new Movie(6, "E.T. the Extra-Terrestrial", 1982, "Un niño ayuda a un extraterrestre perdido a regresar a su hogar.",
                List.of(genres.get(0), genres.get(4)), List.of(directors.get(1))));
        movies.add(new Movie(7, "The Dark Knight", 2008, "Batman lucha contra el Joker para salvar Gotham.",
                List.of(genres.get(2), genres.get(0)), List.of(directors.get(2))));
        movies.add(new Movie(8, "Kill Bill: Vol. 1", 2003, "Una mujer busca venganza contra su antiguo grupo de asesinos.",
                List.of(genres.get(2), genres.get(0)), List.of(directors.get(3))));
        movies.add(new Movie(9, "The Hobbit: An Unexpected Journey", 2012, "Un hobbit emprende una aventura inesperada.",
                List.of(genres.get(4), genres.get(0)), List.of(directors.get(4))));
        movies.add(new Movie(10, "The Social Network", 2010, "La creación de Facebook y las luchas legales que siguieron.",
                List.of(genres.get(0)), List.of(directors.get(5))));
    }

    private static void initializeCountries() {
        countries = new ArrayList<>();

        // Europa
        countries.add(new Country(
                "ES", "Spain", "Europe", 505990, 47350000, 1394000, 1,
                List.of(
                        new Country.City(1, "Madrid", 3266126), // Capital
                        new Country.City(2, "Barcelona", 1636762),
                        new Country.City(3, "Valencia", 791413),
                        new Country.City(4, "Seville", 688592)
                )
        ));

        countries.add(new Country(
                "FR", "France", "Europe", 551695, 65273511, 2787631, 5,
                List.of(
                        new Country.City(5, "Paris", 2140526), // Capital
                        new Country.City(6, "Marseille", 861635),
                        new Country.City(7, "Lyon", 515695)
                )
        ));

        countries.add(new Country(
                "DE", "Germany", "Europe", 357022, 83166711, 3845630, 8,
                List.of(
                        new Country.City(8, "Berlin", 3769000), // Capital
                        new Country.City(9, "Hamburg", 1841000),
                        new Country.City(10, "Munich", 1471508)
                )
        ));

        // Asia
        countries.add(new Country(
                "CN", "China", "Asia", 9596961, 1402000000, 14722731, 11,
                List.of(
                        new Country.City(11, "Beijing", 21540000), // Capital
                        new Country.City(12, "Shanghai", 24870000),
                        new Country.City(13, "Shenzhen", 12530000)
                )
        ));

        countries.add(new Country(
                "JP", "Japan", "Asia", 377975, 126300000, 5081770, 14,
                List.of(
                        new Country.City(14, "Tokyo", 13929286), // Capital
                        new Country.City(15, "Osaka", 8839469),
                        new Country.City(16, "Nagoya", 2295630)
                )
        ));

        countries.add(new Country(
                "IN", "India", "Asia", 3287263, 1380004385, 2875142, 17,
                List.of(
                        new Country.City(17, "New Delhi", 31000000), // Capital
                        new Country.City(18, "Mumbai", 20411000),
                        new Country.City(19, "Bangalore", 12764000)
                )
        ));

        // Norteamérica
        countries.add(new Country(
                "US", "United States", "North America", 9833520, 331893745, 21433225, 20,
                List.of(
                        new Country.City(20, "Washington D.C.", 705749), // Capital
                        new Country.City(21, "New York", 8419600),
                        new Country.City(22, "Los Angeles", 3980400)
                )
        ));

        countries.add(new Country(
                "CA", "Canada", "North America", 9984670, 37742154, 1736426, 23,
                List.of(
                        new Country.City(23, "Ottawa", 934243), // Capital
                        new Country.City(24, "Toronto", 2930000),
                        new Country.City(25, "Vancouver", 631486)
                )
        ));

        countries.add(new Country(
                "MX", "Mexico", "North America", 1964375, 126014024, 1199260, 26,
                List.of(
                        new Country.City(26, "Mexico City", 9209944), // Capital
                        new Country.City(27, "Guadalajara", 1495182),
                        new Country.City(28, "Monterrey", 1135512)
                )
        ));

        // Sudamérica
        countries.add(new Country(
                "BR", "Brazil", "South America", 8515767, 212559409, 1444732, 29,
                List.of(
                        new Country.City(29, "Brasília", 3055149), // Capital
                        new Country.City(30, "São Paulo", 12300000),
                        new Country.City(31, "Rio de Janeiro", 6748000)
                )
        ));

        countries.add(new Country(
                "AR", "Argentina", "South America", 2780400, 45195774, 449663, 32,
                List.of(
                        new Country.City(32, "Buenos Aires", 3054300), // Capital
                        new Country.City(33, "Córdoba", 1435000),
                        new Country.City(34, "Rosario", 1274000)
                )
        ));

        countries.add(new Country(
                "CO", "Colombia", "South America", 1141748, 50882891, 323400, 35,
                List.of(
                        new Country.City(35, "Bogotá", 7743955), // Capital
                        new Country.City(36, "Medellín", 2569000),
                        new Country.City(37, "Cali", 2221000)
                )
        ));

        // Oceanía
        countries.add(new Country(
                "AU", "Australia", "Oceania", 7692024, 25687041, 1389105, 38,
                List.of(
                        new Country.City(38, "Canberra", 462000), // Capital
                        new Country.City(39, "Sydney", 5312163),
                        new Country.City(40, "Melbourne", 5078193)
                )
        ));

        countries.add(new Country(
                "NZ", "New Zealand", "Oceania", 268838, 5084300, 205594, 41,
                List.of(
                        new Country.City(41, "Wellington", 215100), // Capital
                        new Country.City(42, "Auckland", 1657000),
                        new Country.City(43, "Christchurch", 377200)
                )
        ));

        countries.add(new Country(
                "FJ", "Fiji", "Oceania", 18274, 896444, 5416, 44,
                List.of(
                        new Country.City(44, "Suva", 88500), // Capital
                        new Country.City(45, "Nadi", 42284),
                        new Country.City(46, "Lautoka", 52500)
                )
        ));
    }
    private static void initializeAnimals() {
        animals = List.of(
                new Pig("JoséJuan"),
                new Pig("LuisHernández"),
                new Pig("JoséQuinteiro"),
                new Dog("JuanCarlosDelPino"),
                new Hamster("Julio"),
                new Cat("Ricardo"),
                new Parrot("Ernestina"),
                new Parrot("Carlos"),
                new Octopus(),
                new Octopus(),
                new Ant(),
                new Centipede(),
                new Kangaroo(),
                new Frog()
        );
    }

}
