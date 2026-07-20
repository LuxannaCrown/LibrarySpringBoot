package com.starfinanz.LibraryBeanWithSpringBoot;

import com.opencsv.bean.CsvBindByName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserBean {

    @CsvBindByName
    private int id;

    @CsvBindByName
    private String name;

    @CsvBindByName
    private String borrowedbooks;

    public UserBean () {
    }

    public void addBorrowedBooks (long isbn) { //Fügt ein Buch in der users.csv hinzu

        if (borrowedbooks == null || borrowedbooks.isEmpty()) {
            borrowedbooks = String.valueOf(isbn);
        } else {
            borrowedbooks += ";" + isbn;
        }
    }

    public void removeBorrowedBooks (long isbn) { //entfernt ein Buch aus der users.csv

        if (borrowedbooks == null || borrowedbooks.isEmpty()) {
            return;
        }

        List<String> books = new ArrayList<>(Arrays.asList(borrowedbooks.split(";")));
        books.remove(String.valueOf(isbn));

        borrowedbooks = String.join(";", books);
    }

    public boolean hasBorrowedBooks (long isbn) { //Checkt ob beim User das Buch überhaupt hinterlegt ist

        if (borrowedbooks == null || borrowedbooks.isEmpty()) {
            return false;
        }

        return Arrays.asList(borrowedbooks.split(";"))
                .contains(String.valueOf(isbn));
    }

    public String getBorrowedbooks() {
        return borrowedbooks;
    }

    public void setBorrowedbooks(String borrowedbooks) {
        this.borrowedbooks = borrowedbooks;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}


