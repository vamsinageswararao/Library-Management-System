package library.pattern;

// Observer Pattern
public interface BookObserver {
    void onBookAvailable(String isbn, String title);
}
