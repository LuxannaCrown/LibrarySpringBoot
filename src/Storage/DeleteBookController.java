package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class DeleteBookController {


    private final LibraryService libraryService;


    public DeleteBookController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @PostMapping("/delete/{isbn}")
    public String deleteBook(
            @PathVariable long isbn,
            Model model) {


        boolean deleted = libraryService.deleteBook(isbn);


        if (!deleted) {
            model.addAttribute("error",
                    "Buch konnte nicht nicht gelöscht werden.");

            return "error-page";
        }

        return "redirect:/";
    }
}