package com.starfinanz.LibrarySpring.service;


import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.starfinanz.LibrarySpring.model.Book;
import com.starfinanz.LibrarySpring.model.User;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
public class LibraryService {


    private List<Book> books = new ArrayList<>();
    private List<User> users = new ArrayList<>();


    public LibraryService(){
        loadBooks();
        loadUsers();
    }

    private void loadBooks(){
        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/books.csv");

            if (inputStream == null) {
                IO.println("Datei 'books.csv' konnte nicht gefunden werden.");
                return;
            }

            Reader reader = new InputStreamReader(inputStream);

            books = new CsvToBeanBuilder<Book>(reader)
                    .withType(Book.class)
                    .build()
                    .parse();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadUsers(){
        try {
            InputStream inputStream = getClass().getResourceAsStream("/data/users.csv");

            if (inputStream == null) {
                IO.println("Datei 'users.csv' konnte nicht gefunden werden.");
                return;
            }

            Reader reader =  new InputStreamReader(inputStream);

            users = new CsvToBeanBuilder<User>(reader)
                    .withType(User.class)
                    .build()
                    .parse();

            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public List<Book> getBooks(){
        return books;
    }


    public List<User> getUsers(){
        return users;
    }


    public User findUserID (int userId) {  //Checkt einmal ober der User überhaubt in der users.csv existiert

        for (User user :  users) {
            if (user.getId() == userId) {
                return user;
            }
        }

        return null;
    }


    public Book findBookByIsbn(long isbn) {

        for (Book book : books) {
            if (book.getIsbn() == isbn) {
                return book;
            }
        }

        return null;
    }


    public List<Book> getBorrowedBooks(int userId) {
        User user = findUserID(userId);

        List<Book> borrowedBooks = new ArrayList<>();

        if (user == null || user.getBorrowedBooks() == null) {
            return borrowedBooks;
        }

        List<String> isbnList = Arrays.asList(user.getBorrowedBooks().split(";"));

        for (Book book : books) {

            if (isbnList.contains(String.valueOf(book.getIsbn()))) {
                borrowedBooks.add(book);
            }
        }

        return borrowedBooks;
    }


    public boolean addBook (long isbn, String titel, String autor) {
        boolean isbnExists = books.stream()  //Checkt ob es bereits einen Eintrag mit der ISBN in books.csv existiert
                .anyMatch(book -> book.getIsbn() == isbn);

        if (isbnExists) {
            return false;
        }

        Book book = new Book();

        book.setIsbn(isbn);
        book.setTitel(titel);
        book.setAutor(autor);
        book.setStatus("Verfügbar");
        book.setBesitzer("Bibliothek");

        books.add(book);
        saveBook();

        return true;
    }


    public boolean deleteBook(long isbn) {


        boolean borrowed = users.stream()
                .anyMatch(user -> user.hasBook(isbn));

        if (borrowed) {
            return false;
        }

        boolean success = books.removeIf(book -> book.getIsbn() == isbn);

        if (success) {
            saveBook();
        }

        return success;
    }


    public boolean borrowBook(long isbn, int userId) {

        Book book = null;

        for (Book b : books) {
            if (b.getIsbn() == isbn) {
                book = b;
                break;
            }
        }

        if (book == null) {
            return false;
        }

        if ("Ausgeliehen".equals(book.getStatus())) {
            return false;
        }

        User user = findUserID(userId);
        if (user == null) {
            return false;
        }

        if(user.hasBook(isbn)){
            return false;
        }



        book.setStatus("Ausgeliehen");
        book.setBesitzer(user.getName());

        user.addBook(isbn);

        saveBook();
        saveUser();

        return true;
    }


    public boolean returnBook(long isbn, int userId) {

        Book book = null;

        for (Book b : books) {
            if (b.getIsbn() == isbn) {
                book = b;
                break;
            }
        }

        if (book == null) {  //Buch existiert nicht
            return false;
        }

        if ("Verfügbar".equals(book.getStatus())) {  //Buch ist bereits verfügbar
            return false;
        }

        User user = findUserID(userId);
        if (user == null) {  //User existiert nicht
            return false;
        }

        if(!user.hasBook(isbn)){  //Bricht ab wenn der User das Büch nicht hat
            return false;
        }



        book.setStatus("Verfügbar");
        book.setBesitzer("Bibliothek");

        user.removeBook(isbn);  //Entfernt der Buch aus den ausgeliehenen Büchern eines Users

        saveBook();
        saveUser();

        return true;
    }


    public boolean addUser(String name) {
        if (name == null || name.isBlank()) {
            return false;
        }

        int lastId = 0;

        for (User user : users) {  //Checkt user nach der höchsten ID
            if (user.getId() > lastId) {
                lastId = user.getId();
            }
        }

        User newUser =  new User();

        newUser.setId(lastId + 1);  //Setzt den neuen User als nächste höchste ID
        newUser.setName(name);
        newUser.setBorrowedBooks("");

        users.add(newUser);
        saveUser();

        return true;
    }


    public boolean deleteUser(int userId) {
        User user = findUserID(userId);  //Checkt alle user nach gegebener userId

        if (user == null) {
            return false;
        }

        if (user.getBorrowedBooks() != null && !user.getBorrowedBooks().isEmpty()) {
            return false;
        }

        users.remove(user);  //Entfernt den Eintrag zu welcher die userId gehört
        saveUser();

        return true;
    }


   private void saveBook () { //Funktion zum abspeichern in die books.csv Datei

        try (Writer writer = new FileWriter("src\\main\\resources\\data\\books.csv")) {
            StatefulBeanToCsv<Book> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .build();
            beanToCsv.write(books);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private void saveUser () {  //Speichert User änderungen in der users.csv

        try (Writer writer = new FileWriter("src\\main\\resources\\data\\users.csv")) {
            StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .build();
            beanToCsv.write(users);
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim bearbeiten der users.csv.", e);
        }
    }

}