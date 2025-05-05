package com.library.controller;

import com.library.model.User;
import com.library.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
public class LoginController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    
    @PostMapping("/login")
    public String login(@RequestParam String username, 
                       @RequestParam String password,
                       HttpSession session,
                       Model model) {
        if (userService.authenticateUser(username, password)) {
            Optional<User> userOpt = userService.findUserByUsername(username);
            if (userOpt.isPresent()) {
                User user = userOpt.get();
                session.setAttribute("user", user);
                
                // Redirect based on role
                switch (user.getRole()) {
                    case ADMIN:
                        return "redirect:/admin";
                    case LIBRARIAN:
                        return "redirect:/librarian";
                    case LIBRARIAN_ASSISTANT:
                        return "redirect:/assistant";
                    case TEACHER:
                        return "redirect:/teacher";
                    case STUDENT:
                        return "redirect:/student";
                    default:
                        return "redirect:/guest";
                }
            }
        }
        
        model.addAttribute("error", "Invalid username or password");
        return "login";
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    
    @GetMapping("/")
    public String home(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            // Redirect based on role
            switch (user.getRole()) {
                case ADMIN:
                    return "redirect:/admin";
                case LIBRARIAN:
                    return "redirect:/librarian";
                case LIBRARIAN_ASSISTANT:
                    return "redirect:/assistant";
                case TEACHER:
                    return "redirect:/teacher";
                case STUDENT:
                    return "redirect:/student";
                default:
                    return "redirect:/guest";
            }
        }
        return "redirect:/guest";
    }
}
