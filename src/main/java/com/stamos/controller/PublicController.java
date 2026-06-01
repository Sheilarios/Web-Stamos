package com.stamos.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublicController {

    @GetMapping("/")
    public String index() {
        return "public/index";
    }

    @GetMapping("/servicios")
    public String servicios() {
        return "public/servicios";
    }

    @GetMapping("/galeria")
    public String galeria() {
        return "public/galeria";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "public/contacto";
    }
}