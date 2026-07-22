package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.model.Book;
import com.starfinanz.LibrarySpring.model.User;
import com.starfinanz.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AdminController {

    private final LibraryService libraryService;

    public AdminController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @GetMapping("/admin")
    public String home(Model model,
                       HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        model.addAttribute("books", libraryService.getBooks());
        model.addAttribute("users", libraryService.getUsers());
        model.addAttribute("searchResult", session.getAttribute("searchResult"));

        session.removeAttribute("searchResult");

        return "admin";
    }



    @PostMapping("/admin/add")
    public String addBook(
            @RequestParam long isbn,
            @RequestParam String titel,
            @RequestParam String autor,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

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

        return "redirect:/admin";
    }



    @PostMapping("/admin/delete/{isbn}")
    public String deleteBook(
            @PathVariable long isbn,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        boolean deleted = libraryService.deleteBook(isbn);

        if (!deleted) {
            model.addAttribute("error",
                    "Buch konnte nicht nicht gelöscht werden.");

            return "error-page";
        }

        return "redirect:/admin";
    }



    @PostMapping("/admin/search")
    public String searchBook (
            @RequestParam (required = false) Long isbn,
            @RequestParam (required = false) String titel,
            @RequestParam (required = false) String autor,
            HttpSession session) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        if(isbn == null) {
            isbn = 0L;
        }
        if (titel == null) {
            titel = "";
        }
        if (autor == null) {
            autor = "";
        }

        Book book = libraryService.findBook(isbn, titel, autor);

        session.setAttribute("searchResult", book);

        return "redirect:/admin";
    }



    @PostMapping("/admin/addUser")
    public String addUser(
            @RequestParam String name,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        User user = libraryService.findUserName(name);

        if (user.getName().equals(name)) {
            model.addAttribute("error", "Username exisitert bereits, bitte verwende einen anderen.");

            return "error-page";
        }

        boolean addedUser = libraryService.addUser(name);

        if(!addedUser) {
            model.addAttribute("error", "Benutzer konnte nicht hinzugefügt werden.");

            return "error-page";
        }

        return "redirect:/admin";
    }



    @PostMapping("/admin/deleteUser/{userId}")
    public String deleteUser(
            @PathVariable int userId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        boolean deletedUser = libraryService.deleteUser(userId);

        if(!deletedUser) {
            model.addAttribute("error", "Benutzer konnte nicht gelöscht werden.");

            return "error-page";
        }

        return "redirect:/admin";
    }


    @PostMapping("/admin/closeSession")
    public String closeSession (
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        session.invalidate();
        return "redirect:/";
    }
}