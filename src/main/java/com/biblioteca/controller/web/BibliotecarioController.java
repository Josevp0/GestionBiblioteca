package com.biblioteca.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/bibliotecario")
public class BibliotecarioController {
    
    @GetMapping("/inicio")
    public String inicio() {
        return "bibliotecario/inicio";
    }
}
