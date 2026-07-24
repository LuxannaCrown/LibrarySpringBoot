package com.luxanna.LibrarySpring.controller;

import com.luxanna.LibrarySpring.service.LibraryService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {

    private final LibraryService libraryService;

    public PasswordController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }


    @GetMapping("/changePassword/{userId}")  //Website für Passwort-Änderung
    public String loginPage(
            @PathVariable int userId,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null && session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";
        }

        int currentUser = (int) session.getAttribute("loggedUser");
        if (userId != currentUser && currentUser != 0) {
            return "redirect:/home";
        }

        var user = libraryService.findUserID(userId);

        model.addAttribute("user", user);

        return "change-password";
    }



    @PostMapping("/changePassword/{userId}")  //Zugriff von Webiste auf den Service
    public String changePassword (
            @PathVariable int userId,
            @RequestParam String newPassword,
            @RequestParam String oldPassword,
            @RequestParam String newPasswordCheck,
            HttpSession session,
            Model model) {

        if (session.getAttribute("loggedIn") == null && session.getAttribute("loggedInAdmin") == null) {
            return "redirect:/";  //Verwehrt nutzung ohne gültigen session-token
        }

        int currentUser = (int) session.getAttribute("loggedUser");
        if (userId != currentUser && currentUser != 0) {
            return "redirect:/home";  //Wehrt ab, dass User auf andere Zugreifen können (außer dem Admin)
        }

        var user = libraryService.findUserID(userId);
        model.addAttribute("user", user);

        if (!newPassword.equals(newPasswordCheck)) {  //Checkt ob die Passwort-Wiederholung mit der ersten Eingabe übereinstimmt
            model.addAttribute("error", "Passwörter müssen übereinstimmen.");
            return "change-password";
        }

        if (libraryService.replacePassword(userId, newPassword, oldPassword)) {  //Wenn Admin eingeloogt -> führt zurück zur Admin-Page
            if (currentUser == 0) {
                return "redirect:/admin";
            }
            return "redirect:/home";
        } else {
            model.addAttribute("error", "Passwort konnte nicht geändert werden");
            return "error-page";
        }
    }
}