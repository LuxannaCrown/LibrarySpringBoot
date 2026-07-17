package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
public class BorrowController {


    private final LibraryService libraryService;


    public BorrowController(LibraryService libraryService){
        this.libraryService = libraryService;
    }


    @GetMapping("/borrow/{isbn}")
    public String borrowPage(
            @PathVariable long isbn,
            Model model){

        model.addAttribute("isbn", isbn);
        model.addAttribute(
                "users",
                libraryService.getUsers()
        );

        return "borrow";
    }

    @PostMapping("/borrow/{isbn}")
    public String borrowBook (
            @PathVariable long isbn,
            @RequestParam int userId,
            Model model){

        boolean borrowed = libraryService.borrowBook(isbn, userId);

        if (!borrowed) {
            model.addAttribute("error",
                    "Buch konnte nicht ausgeliehen werden.");

            return "borrow";
        }
        return "redirect:/";
    }
}