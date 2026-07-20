package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AddBookController {


    private final LibraryService libraryService;


    public AddBookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @PostMapping("/add")
    public String addBook(
            @RequestParam long isbn,
            @RequestParam String titel,
            @RequestParam String autor,
            Model model) {

        if (isbn <= 0) {
            model.addAttribute("error", "Bitte eine gültige ISBN eingeben.");
            return "error-page";
        }

        if (titel.isBlank()) {
            model.addAttribute("error", "Bitte Titel eingeben.");
            return "error-page";
        }

        if (autor.isBlank()) {
            model.addAttribute("error", "Bitte Autor eingeben");
            return "error-page";
        }

        boolean success = libraryService.addBook(isbn, titel, autor);

        if (!success){
            model.addAttribute("error", "Ein Buch mit der ISBN: " + isbn +" existiert bereits");
            return "error-page";
        }

        return "redirect:/";
    }


}