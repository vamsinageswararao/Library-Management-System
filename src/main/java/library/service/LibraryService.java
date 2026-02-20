package library.service;

import library.model.Book;
import library.model.Patron;
import library.pattern.BookObserver;
import library.util.AppLogger;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LibraryService {

    private static final Logger logger = AppLogger.getLogger();

    // Inventory
    private final Map<String, Book> books = new HashMap<>();

    // Patrons
    private final Map<String, Patron> patrons = new HashMap<>();

    // isbn -> patronId (active checkouts)
    private final Map<String, String> checkouts = new HashMap<>();

    // isbn -> list of patronIds (reservations queue)
    private final Map<String, Queue<String>> reservations = new HashMap<>();

    // Observer pattern: isbn -> list of observers (patrons waiting)
    private final Map<String, List<BookObserver>> observers = new HashMap<>();

    // ─── Book Management ────────────────────────────────────────────

    public void addBook(Book book) {
        books.put(book.getIsbn(), book);
        logger.info("Book added: " + book);
    }

    public void removeBook(String isbn) {
        if (books.remove(isbn) != null) {
            logger.info("Book removed: " + isbn);
        } else {
            logger.warning("Book not found for removal: " + isbn);
        }
    }

    public void updateBook(String isbn, String newTitle, String newAuthor, int newYear) {
        Book book = getBookOrThrow(isbn);
        book.setTitle(newTitle);
        book.setAuthor(newAuthor);
        book.setPublicationYear(newYear);
        logger.info("Book updated: " + book);
    }

    public List<Book> searchByTitle(String title) {
        return books.values().stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(title))
                .collect(Collectors.toList());
    }

    public List<Book> searchByAuthor(String author) {
        return books.values().stream()
                .filter(b -> b.getAuthor().equalsIgnoreCase(author))
                .collect(Collectors.toList());
    }

    public Optional<Book> searchByIsbn(String isbn) {
        return Optional.ofNullable(books.get(isbn));
    }

    // ─── Patron Management ──────────────────────────────────────────

    public void addPatron(Patron patron) {
        patrons.put(patron.getPatronId(), patron);
        logger.info("Patron added: " + patron);
    }

    public void updatePatron(String patronId, String newName, String newEmail) {
        Patron patron = getPatronOrThrow(patronId);
        patron.setName(newName);
        patron.setEmail(newEmail);
        logger.info("Patron updated: " + patron);
    }

    public List<String> getBorrowingHistory(String patronId) {
        return getPatronOrThrow(patronId).getBorrowingHistory();
    }

    // ─── Lending ────────────────────────────────────────────────────

    public void checkoutBook(String isbn, String patronId) {
        Book book = getBookOrThrow(isbn);
        getPatronOrThrow(patronId); // validate patron

        if (!book.isAvailable()) {
            logger.warning("Book unavailable for checkout: " + isbn);
            throw new IllegalStateException("Book is not available: " + isbn);
        }

        book.setAvailable(false);
        checkouts.put(isbn, patronId);
        patrons.get(patronId).addToBorrowingHistory(isbn);
        logger.info("Checked out book " + isbn + " to patron " + patronId);
    }

    public void returnBook(String isbn) {
        Book book = getBookOrThrow(isbn);

        if (!checkouts.containsKey(isbn)) {
            throw new IllegalStateException("Book was not checked out: " + isbn);
        }

        checkouts.remove(isbn);
        book.setAvailable(true);
        logger.info("Book returned: " + isbn);

        // Notify reserved patrons
        notifyObservers(isbn, book.getTitle());
    }

    // ─── Inventory ──────────────────────────────────────────────────

    public List<Book> getAvailableBooks() {
        return books.values().stream().filter(Book::isAvailable).collect(Collectors.toList());
    }

    public List<Book> getBorrowedBooks() {
        return books.values().stream().filter(b -> !b.isAvailable()).collect(Collectors.toList());
    }

    // ─── Reservation (Observer Pattern) ─────────────────────────────

    public void reserveBook(String isbn, String patronId) {
        Book book = getBookOrThrow(isbn);
        Patron patron = getPatronOrThrow(patronId);

        if (book.isAvailable()) {
            logger.warning("Book is already available, no need to reserve: " + isbn);
            return;
        }

        reservations.computeIfAbsent(isbn, k -> new LinkedList<>()).add(patronId);

        // Register observer for this patron
        BookObserver observer = (id, title) ->
                System.out.println("[NOTIFICATION] Dear " + patron.getName() +
                        ", the book '" + title + "' (ISBN: " + id + ") is now available!");

        observers.computeIfAbsent(isbn, k -> new ArrayList<>()).add(observer);
        logger.info("Patron " + patronId + " reserved book " + isbn);
    }

    private void notifyObservers(String isbn, String title) {
        List<BookObserver> list = observers.getOrDefault(isbn, List.of());
        list.forEach(obs -> obs.onBookAvailable(isbn, title));
        observers.remove(isbn);
        reservations.remove(isbn);
    }

    // ─── Helpers ────────────────────────────────────────────────────

    private Book getBookOrThrow(String isbn) {
        Book book = books.get(isbn);
        if (book == null) throw new NoSuchElementException("Book not found: " + isbn);
        return book;
    }

    private Patron getPatronOrThrow(String patronId) {
        Patron patron = patrons.get(patronId);
        if (patron == null) throw new NoSuchElementException("Patron not found: " + patronId);
        return patron;
    }
}
