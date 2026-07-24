package com.luxanna.LibrarySpring.service;


import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.luxanna.LibrarySpring.model.Book;
import com.luxanna.LibrarySpring.model.User;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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


    public  User findUserName (String name) {  //Sucht nach einen User mit Name (users.csv)

        for (User user : users) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }


    public Book findBookByIsbn(long isbn) {  //Sucht nach einen Buch mit ISBN (books.csv)

        for (Book book : books) {
            if (book.getIsbn() == isbn) {
                return book;
            }
        }

        return null;
    }


    public Book findBook(long isbn, String titel, String autor) { //Sucht nach einem Buch mit Titel/ISBN/Autor (users.csv)

        for (Book book : books) {
            if (book.getIsbn() == isbn || book.getTitel().equalsIgnoreCase(titel)
                    || book.getAutor().equalsIgnoreCase(autor)) {
                return book;
            }
        }

        return null;
    }



    public List<Book> getBorrowedBooks(int userId) { //Gibt alle ausgeliehenen Bücher einer userId zurück (users.csv)
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



    public boolean addBook (long isbn, String titel, String autor) {  //Fügt ein neues Buch hinzu (books.csv)
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



    public boolean deleteBook(long isbn) { //Löscht ein Buch (books.csv)
        boolean borrowed = users.stream() //Verhindert das Löschen eines Buches wenn ausgeliehen
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



    public boolean borrowBook(long isbn, int userId) {  //Funktion zum ausleihen eines Buches
        Book book = findBookByIsbn(isbn);

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



        book.setStatus("Ausgeliehen");   //books.csv
        book.setBesitzer(user.getName());  //books.csv

        user.addBook(isbn);   //users.csv

        saveBook();
        saveUser();

        return true;
    }



    public boolean returnBook(long isbn, int userId) { //Funktion für Ruckgabe eines Buches
        Book book = findBookByIsbn(isbn);

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

        user.removeBook(isbn);  //Entfernt der Buch aus den ausgeliehenen Büchern eines Users (users.csv)

        saveBook();
        saveUser();

        return true;
    }



    public boolean addUser(String name) {  //Funktion um einen neuen User hinzuzufügen (users.csv)
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
        newUser.setPassword(hashPassword(name));

        users.add(newUser);
        saveUser();

        return true;
    }



    public boolean deleteUser(int userId) {  //Funktion zum löschen eines Users aus der users.csv
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



    public boolean checkLogin (String username, String password) {  //Funktion für Bestätigung via Login-Daten


            if (username.isEmpty() || password.isBlank()) {
                return false;
            }


            for (User user : users) {

                if (user.getName().equals(username)) {
                    String hashedPassword = hashPassword(password);  //Jagt den String "password" durch ein Hash

                    return user.getPassword().equals(hashedPassword);
                }
            }
            return false;
      }



    public boolean replacePassword (int userId, String newPassword, String oldPassword) { //Funktion um das alte Passwort durch ein neues zu ersetzen (users.csv)
        String hashedPassword = hashPassword(oldPassword);

        for (User user : users) {

            if (hashedPassword.equals(user.getPassword()) && user.getId() == userId) {
                user.setPassword(hashPassword(newPassword));
                saveUser();
                return true;
            }
        }
        return false;
      }



    public String hashPassword  (String password) {  //Hasht einmal den Input
        String fehlschlag = "FAIL";

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            IO.println("FAIL");
            return fehlschlag;
        }
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