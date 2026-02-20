package library;

import library.model.Book;
import library.model.Patron;
import library.pattern.LibraryFactory;
import library.service.LibraryService;

public class Main {

    public static void main(String[] args) {
        LibraryService service = new LibraryService();

        // Create books via Factory
        Book b1 = LibraryFactory.createBook("978-0-13-468599-1", "Effective Java", "Joshua Bloch", 2018);
        Book b2 = LibraryFactory.createBook("978-0-13-235088-4", "Clean Code", "Robert Martin", 2008);
        service.addBook(b1);
        service.addBook(b2);

        // Create patrons via Factory
        Patron p1 = LibraryFactory.createPatron("Ram", "Ram@mail.com");
        Patron p2 = LibraryFactory.createPatron("Bheem", "Bheem@mail.com");
        service.addPatron(p1);
        service.addPatron(p2);

        // Search
        System.out.println("\n--- Search by author ---");
        service.searchByAuthor("Joshua Bloch").forEach(System.out::println);

        // Checkout
        System.out.println("\n--- Checkout ---");
        service.checkoutBook(b1.getIsbn(), p1.getPatronId());

        // Try to checkout same book (should throw)
        try {
            service.checkoutBook(b1.getIsbn(), p2.getPatronId());
        } catch (IllegalStateException e) {
            System.out.println("Expected error: " + e.getMessage());
        }

        // Reserve (Observer pattern)
        System.out.println("\n--- Reserve ---");
        service.reserveBook(b1.getIsbn(), p2.getPatronId());

        // Return — triggers notification
        System.out.println("\n--- Return (triggers notification) ---");
        service.returnBook(b1.getIsbn());

        // Inventory
        System.out.println("\n--- Available Books ---");
        service.getAvailableBooks().forEach(System.out::println);

        // Borrowing history
        System.out.println("\n--- Borrowing History for " + p1.getName() + " ---");
        System.out.println(service.getBorrowingHistory(p1.getPatronId()));
    }
}
