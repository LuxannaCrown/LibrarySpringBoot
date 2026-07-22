package com.starfinanz.LibrarySpring.controller;

import com.starfinanz.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReturnController {

    private final LibraryService libraryService;

    public ReturnController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping("/return/{userId}")
    public String returnPage(
            @PathVariable int userId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null  && session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        int currentUser = (int) session.getAttribute("loggedUser");
        if (userId != currentUser && currentUser != 0) {
            return "redirect:/home";
        }

        var user = libraryService.findUserID(userId);
        var books = libraryService.getBorrowedBooks(userId);

        model.addAttribute("user", user);
        model.addAttribute("books", books);


        return "return";
    }



    @PostMapping("/return/{userId}")
    public String returnBook(
            @RequestParam long isbn,
            @PathVariable int userId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null   && session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        int currentUser = (int) session.getAttribute("loggedUser");
        if (userId != currentUser && currentUser != 0) {
            return "redirect:/home";
        }

        boolean returned = libraryService.returnBook(isbn, userId);

        if (!returned) {
            model.addAttribute("error",
                    "Buch konnte nicht zurückgegeben werden.");

            return "return";
        }

        if (currentUser == 0) {
            return "redirect:/admin";
        }
        return "redirect:/home";
    }
}