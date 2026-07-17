package com.starfinanz.LibrarySpring.service;


import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.starfinanz.LibrarySpring.model.Book;
import com.starfinanz.LibrarySpring.model.User;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
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



   private void saveBook () { //Funktion zum abspeichern in die books.csv Datei

        try (Writer writer = new FileWriter("src\\main\\resources\\data\\books.csv")) {
            StatefulBeanToCsv<Book> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .build();
            beanToCsv.write(books);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void saveUser () {

        try (Writer writer = new FileWriter("src\\main\\resources\\data\\users.csv")) {
            StatefulBeanToCsv<User> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .build();
            beanToCsv.write(users);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}