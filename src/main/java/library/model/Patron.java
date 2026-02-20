package library.model;

import java.util.ArrayList;
import java.util.List;

public class Patron {
    private final String patronId;
    private String name;
    private String email;
    private final List<String> borrowingHistory = new ArrayList<>();

    public Patron(String patronId, String name, String email) {
        this.patronId = patronId;
        this.name = name;
        this.email = email;
    }

    public String getPatronId() { return patronId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getBorrowingHistory() { return List.copyOf(borrowingHistory); }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void addToBorrowingHistory(String isbn) { borrowingHistory.add(isbn); }

    @Override
    public String toString() {
        return String.format("Patron[id=%s, name=%s, email=%s]", patronId, name, email);
    }
}
