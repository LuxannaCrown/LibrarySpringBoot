package com.starfinanz.LibrarySpring.controller;


import com.starfinanz.LibrarySpring.service.LibraryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class DeleteUserController {


    private final LibraryService libraryService;


    public DeleteUserController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @PostMapping("/deleteUser/{userId}")
    public String addUser(
            @PathVariable int userId,
            Model model) {

        boolean deletedUser = libraryService.deleteUser(userId);

        if(!deletedUser) {
            model.addAttribute("error", "Benutzer konnte nicht gelöscht werden.");

            return "error-page";
        }

        return "redirect:/";
    }
}