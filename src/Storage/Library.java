package com.starfinanz.LibraryBeanWithSpringBoot;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Library {

    boolean libraryAktiv = false;

    public void starteLibrary() {
        List<BookBean> beans = new ArrayList<>();
        List<UserBean> users = new ArrayList<>();

        try {
            beans = new CsvToBeanBuilder(new FileReader("src\\main\\java\\com\\starfinanz\\LibraryBeanWithSpringBoot\\books.csv"))
                    .withType(BookBean.class)
                    .build()
                    .parse();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            users =  new CsvToBeanBuilder(new FileReader("src\\main\\java\\com\\starfinanz\\LibraryBeanWithSpringBoot\\users.csv"))
                    .withType(UserBean.class)
                    .build()
                    .parse();

        } catch (Exception e) {
            e.printStackTrace();
        }


        libraryAktiv = getLogin();  //Startet das Menü wenn Login bestätigt ist
        if (!libraryAktiv) {
            IO.println("Login wurde 3 mal Falsch eingegeben.\nProgramm wird beendet.");
        }

        while (libraryAktiv) {

            String menu = getMenu();  //Holt das Menü und die Auswahl für den Switch-Case

            switch (menu) {
                case "1"://Buch hinzufügen
                    getNewBook(beans);
                    saveBook(beans);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "2":  //Alle bücher anzeigen
                    getBookList(beans);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "3":  //Buch ausleihen
                    getBorrowBook(beans, users);
                    saveBook(beans);
                    saveUser(users);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "4":  //Buch zurückgeben
                    getReturnBook(beans, users);
                    saveBook(beans);
                    saveUser(users);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "5": //Buch suchen
                    getSearchBook(beans);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "6": //Benutzerliste ausgeben
                    getUserList(users);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "7": //Benutzer hinzufügen
                    getNewUser(users);
                    saveUser(users);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "8":
                    getDeleteBook(beans, users);
                    saveBook(beans);
                    saveUser(users);
                    IO.readln("\nDrück Enter um fortzufahren.");
                    break;


                case "9":
                    getDeleteUser(users);
                    saveBook(beans);
                    saveUser(users);
                    IO.readln("\nDrück Enter um fortzufahren");
                    break;


                case "0": //Programm beenden
                    saveBook(beans);
                    saveUser(users);
                    IO.println("\nLibrary Programm wird beendet.");
                    libraryAktiv = false;
                    break;
            }
        }
    }



    boolean getLogin () {  //Funktion für Bestätigung via Login-Daten
        IO.println("\n\n    Gebe deine Login-Daten ein.");

        String uid;
        String password;

        for (int i = 0; i < 3; i++) {

            while (true) {
                uid = IO.readln("ID: ");
                password = IO.readln("Password: ");

                if (uid.isEmpty() || password.isEmpty()) {
                    IO.println("Bitte gebe deine Login-Daten ein.\n");
                } else {
                    break;
                }
            }

            String hashPassword = hashPassword(password);  //Jagt den String "password" durch ein Hash

            if (uid.equals("Admin") && hashPassword.equals("03ac674216f3e15c761ee1a5e255f067953623c8b388b4459e13f978d7c846f4")) {
                return true;
            } else {
                IO.println("Deine ID und Password stimmen nicht überein.");
            }
        }
        return false;
    }



    String getMenu() {  //Funktion für ein Menü mit Auswahl
        String[] menuOptions = {"1 - Buch hinzufügen", "2 - Alle Bücher anzeigen", "3 - Buch ausleihen",
                "4 - Buch zurückgeben", "5 - Buch suchen", "6 - Benutzerliste anzeigen",
                "7 - Benutzer hinzufügen", "8 - Buch löschen", "9 - Benutzer löschen", "0 - Beenden"};

        IO.println("\n\n===== Bibliothek =====\n");

        for (String option : menuOptions) {
            IO.println(option);
        }

        while (true) {
            String selection = IO.readln("\nGebe zur Auswahl die zugehörige Zahl ein.\nMenuauswahl: ");

            if (selection.equals("1") || selection.equals("2") || selection.equals("3") || selection.equals("4") || selection.equals("5")
                    || selection.equals("6") || selection.equals("7") || selection.equals("8") || selection.equals("9") || selection.equals("0")) {
                return selection;
            } else {
                IO.println("Bitte benutze 0-9 zur Menüauswahl.");
            }
        }
    }



    void getNewBook (List<BookBean> beans) {  //Funktion um ein neues Buch in die books.csv einzufügenm

        IO.println("\n===== Neues Buch =====\n");

        BookBean newBook = new BookBean();

        while (true) {
            try {
                long newIsbn = Long.parseLong(IO.readln("ISBN: "));

                if (newIsbn <= 0) {
                    IO.println("\nRückkehr in das Hauptmenü\n");
                    return;
                } else {
                    newBook.setIsbn(newIsbn);
                    break;
                }

            } catch (NumberFormatException e) {
                IO.println("Bitte gebe eine Gültige ISBN ein.");
            }
        }
        newBook.setTitel(IO.readln("Titel: "));
        newBook.setAutor(IO.readln("Autor: "));
        newBook.setStatus("Verfügbar");
        newBook.setBesitzer("Bibliothek");

        boolean isbnExists = beans.stream()  //Checkt ob es bereits einen Eintrag mit der ISBN in books.csv existiert
                .anyMatch(book -> book.getIsbn() == newBook.getIsbn());
        if (isbnExists) {
            IO.println("Fehler: Buch mit der ISBN: [" + newBook.getIsbn() + "] existiert bereits.");
        } else {
            beans.add(newBook);
            IO.println("Buch wurde hinzugefügt.");
        }
    }



    void getBookList (List<BookBean> beans) {  //Gibt einmal die komplette Buchliste aus books.csv aus
        IO.println("\n===== Inventar =====\n");
        for (BookBean book : beans) {
            IO.println(book.getIsbn() + " | " + book.getTitel() + " | " + book.getAutor() + " | " + book.getStatus() + " | " + book.getBesitzer());
        }
    }



    void getBorrowBook (List<BookBean> beans, List<UserBean> users) { //Funktion zum Buch ausleihen, ändert Einträge in users.csv und books.csv
        long borrowIsbn;

        IO.println("\n===== Verleihung =====\n");

        while (true) {
            try {
                borrowIsbn = Long.parseLong(IO.readln("Welches Buch möchtest du dir ausleihen? ISBN: "));
                break;
            } catch (NumberFormatException e) {
                IO.println("Bitte gebe eine gültige Nummer ein.");
            }
        }
        boolean bookFound = false;

        for (BookBean book : beans) {  //Checkt ob das Buch existiert
            if (book.getIsbn() == borrowIsbn) {
                bookFound = true;

                if ("Ausgeliehen".equals(book.getStatus())) {  //Checkt ob das Buch schon ausgeliehen ist
                    IO.println("Das Buch '" + book.getTitel() + "' ist bereits ausgeliehen.");

                } else {
                    while (true) {
                        String confirmBorrow = IO.readln("\nBestätige: Möchtest du '" + book.getTitel() + "' ausleihen? Ja/Nein: ");

                        if ("Ja".equalsIgnoreCase(confirmBorrow)) {
                            UserBean borrower = getBorrower(users);  //Eingabe wer das Buch ausleiht mit Check ob der User existiert in users.csv


                            if (borrower != null) {
                                borrower.addBorrowedBooks(book.getIsbn());
                                book.setStatus("Ausgeliehen");
                                book.setBesitzer(borrower.getName());
                                IO.println("\nBuch '" + book.getTitel() + "' wurde erfolgreich ausgeliehen.");
                                break;
                            }

                            break;

                        } else if ("Nein".equalsIgnoreCase(confirmBorrow)) {
                            IO.println("Leih-Prozess wird abgebrochen.");
                            break;

                        } else {
                            IO.println("Bitte mit 'Ja' oder 'Nein' antworten.");
                        }
                    }
                }
                break;
            }
        }

        if (!bookFound) {
            IO.println("Fehler: Buch mit der ISBN: [" + borrowIsbn + "] existiert nicht.");
        }
    }



    void getReturnBook (List<BookBean> beans, List<UserBean> users) {  //Funktion zur RÜckgabe eines Buches, ändert die Einträge in users.csv und books.csv
        long returnIsbn;

        IO.println("\n===== Rückgabe =====\n");

        while (true) {
            try {
                returnIsbn = Long.parseLong(IO.readln("Welches Buch möchtest du zurückgeben? ISBN: "));
                break;
            } catch (NumberFormatException e) {
                IO.println("Bitte gebe eine gültige ISBN ein.");
            }
        }
        boolean bookFound = false;

        for (BookBean book : beans) {
            if (book.getIsbn() == returnIsbn) {  //Checkt ob das Buch existiert in books.csv
                bookFound = true;

                if ("Verfügbar".equals(book.getStatus())) {  //Checkt ob das Buch bereits im Besitz der Bibliothek ist
                    IO.println("\nDas Buch '" + book.getTitel() + "' ist bereits in der Bibliothek.");
                    break;

                } else {
                    String confirmBorrow = IO.readln("Bestätige: Möchtest du '" + book.getTitel() + "' zurückgeben? Ja/Nein: ");  //Eingabe bestätigung

                    while (true) {
                        if ("Ja".equalsIgnoreCase(confirmBorrow)) {
                            UserBean borrower = getBorrower(users);  //Eingabe wer das Buch zurückgibt mit Check ob User existiert in users.csv


                            if (borrower != null && borrower.hasBorrowedBooks(book.getIsbn())) {  //borrower.hasBorrowedBooks checkt ob das Buch auch auf den eingebenen User hinterlegt ist
                                borrower.removeBorrowedBooks(book.getIsbn());
                                book.setStatus("Verfügbar");
                                book.setBesitzer("Bibliothek");
                                IO.println("\nBuch '" + book.getTitel() + "' wurde erfolgreich zurückgegeben.");
                            } else {
                                IO.println("\nRückgabe ist nicht möglich. Buch ist dem Besitzer nicht zugeordnet.");
                            }
                            break;

                        } else if ("Nein".equalsIgnoreCase(confirmBorrow)) {
                            IO.println("Rückgabe-Prozess wird abgebrochen.");
                            break;

                        } else {
                            IO.println("Bitte mit 'Ja' oder 'Nein' antworten.");
                        }
                    }
                }
                break;
            }
        }

        if (!bookFound) {
            IO.println("Fehler: Buch mit der ISBN: [" + returnIsbn + "] existiert nicht.");
        }
    }



    void getSearchBook (List<BookBean> beans) {  //Sucht ein Buch mit der ISBN aus der books.csv
        long searchIsbn;

        IO.println("\n===== Buchsuche =====\n");

        while (true) {
            try {
                searchIsbn = Long.parseLong(IO.readln("Welches Buch suchst du? ISBN: "));
                break;
            } catch (NumberFormatException e) {
                IO.println("Bitte gebe eine gültige ISBN ein.");
            }
        }

        boolean bookFound = false;

        for (BookBean book : beans) {  //Geht alle Beans der books.csv Zeile für Zeile durch und fängt dann mit der IF-Clause wenn die ISBNs übereinstimmen

            if (book.getIsbn() == searchIsbn) {
                IO.println("\nBuch wurde gefunden:");
                bookFound = true;
                IO.println(book.getIsbn() + " | " + book.getTitel() + " | " + book.getAutor() + " | " + book.getStatus() + " | " + book.getBesitzer());
                break;
            }
        }

        if (!bookFound) {
            IO.println("Buch mit der ISBN: [" + searchIsbn + "] wurde nicht gefunden.");
        }
    }



    UserBean getBorrower (List<UserBean> users) { //Ist für die Eingabe wer das Buch ausleiht

        while (true) {
            String name = IO.readln("Name des Entleiher: ");
            name = name.trim();

            if (name.equalsIgnoreCase("abbrechen")) {
                return null;
            } else if (name.isEmpty()) {
                IO.println("Bitte gebe einen Namen ein.");
                continue;
            }

            UserBean user =  findUserName(users, name); //<-- Holt den Check ob User existiert

            if (user != null) {
                return user;
            }

            IO.println("Benutzer konnte nicht gefunden werden.");
        }
    }



    UserBean findUserName (List<UserBean> users, String name) {  //Checkt einmal ober der User überhaubt in der users.csv existiert

        for (UserBean user :  users) {
            if (user.getName() != null && user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }

        return null;
    }



    UserBean findUserID (List<UserBean> users, int deleteID) {  //Checkt einmal ober der User überhaubt in der users.csv existiert

        for (UserBean user :  users) {
            if (user.getId() == deleteID) {
                return user;
            }
        }

        return null;
    }



    void getUserList (List<UserBean> users) {
        IO.println("\n===== Benutzerliste =====\n");

        for (UserBean user : users) {
            IO.println(user.getId() + " | " + user.getName() + " | " + user.getBorrowedbooks());
        }
    }



    void getNewUser(List<UserBean> users) {
        IO.println("\n===== Neuen Nutzer hinzufügen =====\n");

        UserBean newUser = new UserBean();
        int lastID = 0;

        for (UserBean user : users) {  //Es wird automatisch eine ID erstellt mit der nächst größten Zahl der bisherig größten ID

            if (user.getId() > lastID){
                lastID = user.getId();
            }
        }

        newUser.setId(lastID + 1);
        newUser.setName(IO.readln("Name: "));
        newUser.setBorrowedbooks("");

        users.add(newUser);
        IO.println("\nNutzer wurde hinzugefügt.");
        }



    void getDeleteBook (List<BookBean> beans, List<UserBean> users) {
        IO.println("\n===== Buch löschen =====\n");
        long deleteIsbn = 0;

        while (true) {
            try {
                deleteIsbn = Long.parseLong(IO.readln("Zu löschende ISBN: "));
            } catch (NumberFormatException e) {
                IO.println("Bitte gültige ISBN eingeben.");
            }

            if (deleteIsbn <= 0) {
                IO.println("\nRückkehr in das Hauptmenü.\n");
                return;
            }

            long finalDeleteIsbn = deleteIsbn;

            for (UserBean user : users) {
                if (user.hasBorrowedBooks(finalDeleteIsbn)) {

                    while (true) {
                        String confirmContinue = IO.readln(user.getName() + " hat das Buch gerade ausgeliehen. Trotzdem fortfahren? Ja/Nein: ");

                        if (confirmContinue.equalsIgnoreCase("Ja")) {
                            user.removeBorrowedBooks(finalDeleteIsbn);
                            break;

                        } else if (confirmContinue.equalsIgnoreCase("Nein")) {
                            IO.println("Vorgang abgebrochen.");
                            return;

                        } else {
                            IO.println("Bitte mit 'Ja' oder 'Nein' antworten.");
                        }
                    }
                    break;
                }
            }

            boolean confirmDelete = beans.removeIf(book -> book.getIsbn() == finalDeleteIsbn);


            if (confirmDelete) {
                IO.println("Das Buch wurde erfolgreich gelöscht.");
                break;
            } else {
                IO.println("Buch konnte nicht gefunden werden");
            }
        }
    }



    void getDeleteUser (List<UserBean> users) {
        IO.println("\n===== Benutzer löschen =====\n");

        int deleteID = 0;

        while (true) {
            try {
                deleteID = Integer.parseInt(IO.readln("Zu löschende ID: "));
            } catch (NumberFormatException e) {
                IO.println("Bitte gültige ID eingeben.");
            }

            if (deleteID <= 0) {
                IO.println("\nRückkehr in das Hauptmenü.\n");
                return;
            }

            UserBean user = findUserID(users, deleteID);

            if (user == null) {
                IO.println("Benutzer konnte nicht gefunden werden");

            } else if (user.getBorrowedbooks() != null && !user.getBorrowedbooks().isEmpty()) {
                IO.println("Benutzer hat noch Bücher ausgeliehen. Vorgang wird abgebrochen.");
                return;

            } else {

                while (true) {
                    String confirmContinue = IO.readln(user.getName() + " unwiderruflich löschen? Ja/Nein: ");

                    if (confirmContinue.equalsIgnoreCase("Ja")) {
                        users.remove(user);
                        IO.println("Benutzer " + user.getId() + " | " + user.getName() + " wurde gelöscht.");
                        break;

                    } else if (confirmContinue.equalsIgnoreCase("Nein")) {
                        IO.println("Vorgang abgebrochen.");
                        return;

                    } else {
                        IO.println("Bitte mit 'Ja' oder 'Nein' antworten.");
                    }
                }
                break;
            }
        }
    }



    String hashPassword  (String password) {
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
            JOptionPane.showMessageDialog(null, "Fail");
            return fehlschlag;
        }
    }



    void saveBook (List<BookBean> beans) { //Funktion zum abspeichern in die books.csv Datei
        try {
            Writer writer = new FileWriter("src\\main\\java\\com\\starfinanz\\LibraryBeanWithSpringBoot\\books.csv");
            StatefulBeanToCsv<BookBean> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .build();
            beanToCsv.write(beans);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        IO.println("änderungen in der books.csv gespeichert...");
    }



    void saveUser (List<UserBean> users) {  //Funktion zum abspeichern in die users.csv Datei
        try {
            Writer writer = new FileWriter("src\\main\\java\\com\\starfinanz\\LibraryBeanWithSpringBoot\\users.csv");
            StatefulBeanToCsv<UserBean> beanToCsv = new StatefulBeanToCsvBuilder(writer)
                    .build();
            beanToCsv.write(users);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        IO.println("änderungen in der users.csv gespeichert...");
    }
}