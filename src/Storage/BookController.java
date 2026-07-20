package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class BookController {


    private final LibraryService libraryService;


    public BookController(LibraryService libraryService){
        this.libraryService = libraryService;
    }



    @GetMapping("/books")
    public String books(Model model){
        model.addAttribute("books", libraryService.getBooks());
        return "books";
    }


    @GetMapping("/books/new")
    public String newBook() {
        return "add-books";
    }

    @PostMapping("/books/add")
    public String addBook(
            @RequestParam long isbn,
            @RequestParam String titel,
            @RequestParam String autor,
            Model model) {

        if (isbn <= 0) {
            model.addAttribute("error", "Bitte eine gültige ISBN eingeben.");
            return "add-books";
        }

        if (titel.isBlank()) {
            model.addAttribute("error", "Bitte Titel eingeben.");
            return "add-books";
        }

        if (autor.isBlank()) {
            model.addAttribute("error", "Bitte Autor eingeben");
            return "add-books";
        }

        boolean success = libraryService.addBook(isbn, titel, autor);

        if (!success){
            model.addAttribute("error", "Ein Buch mit der ISBN: " + isbn +" existiert bereits");
            return "add-books";
        }

        return "redirect:/";
    }


    @PostMapping("/books/delete/{isbn}")
    public String deleteBook(
            @PathVariable long isbn,
            Model model) {


        boolean deleted = libraryService.deleteBook(isbn);


        if (!deleted) {
            model.addAttribute("error",
                    "Buch konnte nicht gefunden werden.");

            return "books";
        }


        return "redirect:/";
    }
}