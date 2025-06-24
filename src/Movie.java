import java.util.List;

public record Movie(int id, String title, int year, String imdb, List<Genre> genres, List<Director> directors) {
    public record Director(int id, String name, String imdb) {}
    public record Genre(int id, String name) {}
}