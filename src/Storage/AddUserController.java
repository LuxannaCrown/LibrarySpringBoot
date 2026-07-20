package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AddUserController {


    private final LibraryService libraryService;


    public AddUserController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @PostMapping("/addUser")
    public String addUser(
            @RequestParam String name,
            Model model) {

        boolean addedUser = libraryService.addUser(name);

        if(!addedUser) {
            model.addAttribute("error", "Benutzer konnte nicht hinzugefügt werden.");

            return "error-page";
        }

        return "redirect:/";
    }
}