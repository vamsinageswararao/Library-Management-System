# Library Management System

A simple Java console application demonstrating core OOP principles and design patterns.

---

## Project Structure

```
library/src/main/java/library/
├── Main.java
├── model/
│   ├── Book.java
│   └── Patron.java
├── pattern/
│   ├── BookObserver.java
│   └── LibraryFactory.java
├── service/
│   └── LibraryService.java
└── util/
    └── AppLogger.java
```

---

## How to Run

```bash
# Compile
javac -d out $(find src -name "*.java")

# Run
java -cp out library.Main
```

---

## Features

- **Book Management** – Add, remove, update, and search books by title, author, or ISBN
- **Patron Management** – Add/update patrons and track borrowing history
- **Lending** – Checkout and return books
- **Inventory** – View available and borrowed books
- **Reservation + Notifications** – Reserve checked-out books; get notified on return (Observer pattern)

---

## Design Patterns Used

| Pattern | Where |
|---|---|
| **Factory** | `LibraryFactory` – creates `Book` and `Patron` instances |
| **Observer** | `BookObserver` – notifies patrons when a reserved book becomes available |

---

## Class Diagram

```mermaid
classDiagram
    class Book {
        -String isbn
        -String title
        -String author
        -int publicationYear
        -boolean available
        +getIsbn() String
        +getTitle() String
        +isAvailable() boolean
        +setAvailable(boolean)
    }

    class Patron {
        -String patronId
        -String name
        -String email
        -List~String~ borrowingHistory
        +getPatronId() String
        +getBorrowingHistory() List
        +addToBorrowingHistory(String)
    }

    class BookObserver {
        <<interface>>
        +onBookAvailable(String isbn, String title)
    }

    class LibraryFactory {
        <<utility>>
        +createBook(String, String, String, int) Book
        +createPatron(String, String) Patron
    }

    class LibraryService {
        -Map~String, Book~ books
        -Map~String, Patron~ patrons
        -Map~String, String~ checkouts
        -Map~String, Queue~ reservations
        -Map~String, List~ observers
        +addBook(Book)
        +removeBook(String)
        +updateBook(String, ...)
        +searchByTitle(String) List
        +searchByAuthor(String) List
        +searchByIsbn(String) Optional
        +addPatron(Patron)
        +updatePatron(String, ...)
        +checkoutBook(String, String)
        +returnBook(String)
        +reserveBook(String, String)
        +getAvailableBooks() List
        +getBorrowedBooks() List
    }

    class AppLogger {
        <<utility>>
        +getLogger() Logger
    }

    class Main {
        +main(String[])
    }

    LibraryService --> Book : manages
    LibraryService --> Patron : manages
    LibraryService --> BookObserver : notifies
    LibraryService --> AppLogger : uses
    LibraryFactory --> Book : creates
    LibraryFactory --> Patron : creates
    Main --> LibraryService : uses
    Main --> LibraryFactory : uses
```

---

## OOP & SOLID Principles

- **Encapsulation** – All fields are private with controlled access via getters/setters
- **Abstraction** – `BookObserver` interface abstracts the notification mechanism
- **Single Responsibility** – Each class has one clear responsibility
- **Open/Closed** – New observer types can be added without modifying `LibraryService`
- **Dependency on abstractions** – `LibraryService` depends on `BookObserver` interface, not a concrete class
