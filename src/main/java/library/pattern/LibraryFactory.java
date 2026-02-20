package library.pattern;

import library.model.Book;
import library.model.Patron;

import java.util.UUID;

// Factory Pattern
public class LibraryFactory {

    public static Book createBook(String isbn, String title, String author, int year) {
        return new Book(isbn, title, author, year);
    }

    public static Patron createPatron(String name, String email) {
        String id = "P-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new Patron(id, name, email);
    }
}
