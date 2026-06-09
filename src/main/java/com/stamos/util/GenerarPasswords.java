package com.stamos.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarPasswords {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String adminPass = "Admin123";
        String clientePass = "Cliente123";
        
        System.out.println("Contraseña: " + adminPass);
        System.out.println("Hash BCrypt: " + encoder.encode(adminPass));
        System.out.println("\n---\n");
        System.out.println("Contraseña: " + clientePass);
        System.out.println("Hash BCrypt: " + encoder.encode(clientePass));
    }
}