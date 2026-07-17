package com.starfinanz.LibrarySpring.model;

import com.opencsv.bean.CsvBindByName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {

    @CsvBindByName
    private int id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private String borrowedBooks;


    public User() {
    }


    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getBorrowedBooks() {
        return borrowedBooks;
    }


    public void setBorrowedBooks(String borrowedBooks) {
        this.borrowedBooks = borrowedBooks;
    }


    public void addBook (long isbn) { //Fügt ein Buch in der users.csv hinzu

        if (borrowedBooks == null || borrowedBooks.isEmpty()) {
            borrowedBooks = String.valueOf(isbn);
        } else {
            borrowedBooks += ";" + isbn;
        }
    }


    public void removeBook (long isbn) { //entfernt ein Buch aus der users.csv

        if (borrowedBooks == null || borrowedBooks.isEmpty()) {
            return;
        }

        List<String> books = new ArrayList<>(Arrays.asList(borrowedBooks.split(";")));
        books.remove(String.valueOf(isbn));

        borrowedBooks = String.join(";", books);
    }

    public boolean hasBook (long isbn) { //Checkt ob beim User das Buch überhaupt hinterlegt ist

        if (borrowedBooks == null || borrowedBooks.isEmpty()) {
            return false;
        }

        return Arrays.asList(borrowedBooks.split(";"))
                .contains(String.valueOf(isbn));
    }
}