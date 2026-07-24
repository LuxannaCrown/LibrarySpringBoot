package com.luxanna.LibrarySpring.controller;


import com.luxanna.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
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
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null   && session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }


        model.addAttribute("isbn", isbn);
        model.addAttribute("users", libraryService.getUsers());

        return "borrow";
    }



    @PostMapping("/borrow/{isbn}")
    public String borrowBook (
            @PathVariable long isbn,
            @RequestParam int userId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null   && session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";   //Checkt nach einen gültigen session-token
        }

        int currentUser = (int) session.getAttribute("loggedUser");
        if (userId != currentUser  && currentUser != 0) {  //Verhindert Zugriff von Usern auf andere User (außer Admin)
            model.addAttribute("error", "Du kannst nur für dich selber Bücher ausleihen");
            return "error-page";
        }

        boolean borrowed = libraryService.borrowBook(isbn, userId);

        if (!borrowed) {
            model.addAttribute("error",
                    "Buch konnte nicht ausgeliehen werden.");

            return "error-page";
        }
        if (currentUser == 0) {  //Wenn Admin eingeloggt -> führt zurück zur Admin-Page
            return "redirect:/admin";
        }
        return "redirect:/home";
    }
}