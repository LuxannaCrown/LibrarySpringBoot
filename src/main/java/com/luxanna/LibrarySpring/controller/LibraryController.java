package com.luxanna.LibrarySpring.controller;


import com.luxanna.LibrarySpring.model.Book;
import com.luxanna.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @GetMapping("/home")
    public String home(Model model,
                       HttpSession session) {

        if (session.getAttribute("loggedIn") == null) {  //Verwehrt nutzung ohne gültigen session-token
            return "redirect:/";
        }
        int userId = (int) session.getAttribute("loggedUser");

        model.addAttribute("books", libraryService.getBooks());
        model.addAttribute("user", libraryService.findUserID(userId));
        model.addAttribute("searchResult", session.getAttribute("searchResult"));

        session.removeAttribute("searchResult");

        return "index";
    }



    @PostMapping("/search")
    public String searchBook (
            @RequestParam (required = false) Long isbn,
            @RequestParam (required = false) String titel,
            @RequestParam (required = false) String autor,
            HttpSession session) {

        if (session.getAttribute("loggedIn") == null) {  //Verwehrt nutzung ohne gültigen session-token
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

        return "redirect:/home";
    }



    @PostMapping("/closeSession")
    public String closeSession (
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null) {  //Verwehrt nutzung ohne gültigen session-token
            return "redirect:/";
        }

            session.invalidate();
            return "redirect:/";
    }
}