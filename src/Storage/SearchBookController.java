package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.model.Book;
import com.starfinanz.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class SearchBookController {

    private final LibraryService libraryService;

    public SearchBookController (LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @PostMapping ("/search")
    public String searchBook (
            @RequestParam long isbn,
            HttpSession session) {

        Book book = libraryService.findBookByIsbn(isbn);

        session.setAttribute("searchResult", book);

        return "redirect:/";
    }
}
