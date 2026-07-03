package com.stamos.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarPasswords {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        String[][] usuarios = {
            {"Administrador", "admin@stamos.com", "Admin123"},
            {"Vendedor", "vendedor@stamos.com", "Vendedor123"},
            {"Diseñador", "disenador@stamos.com", "Disenador123"},
            {"Producción", "produccion@stamos.com", "Produccion123"},
            {"Soporte Técnico", "soporte@stamos.com", "Soporte123"},
            {"Cliente", "cliente@stamos.com", "Cliente123"}
        };
        
        System.out.println();
        
        for (String[] u : usuarios) {
            String nombre = u[0];
            String email = u[1];
            String pass = u[2];
            String hash = encoder.encode(pass);
            
            System.out.println("-- " + nombre + " | " + email + " | Contraseña: " + pass);
            System.out.println("-- Hash: " + hash);
            System.out.println();
        }
        
        System.out.println();
        System.out.println("INSERT INTO Usuario (id_rol, nombre_completo, email, password_hash, telefono, activo, fecha_creacion) VALUES");
        
        String[] nombres = {"Administrador STAMOS", "Vendedor STAMOS", "Diseñador STAMOS", "Producción STAMOS", "Soporte Técnico STAMOS", "Cliente STAMOS"};
        String[] emails = {"admin@stamos.com", "vendedor@stamos.com", "disenador@stamos.com", "produccion@stamos.com", "soporte@stamos.com", "cliente@stamos.com"};
        String[] hashes = new String[6];
        String[] telefonos = {"987819456", "987819457", "987819458", "987819459", "987819460", "987654321"};
        int[] roles = {1,2,3,4,5,6};
        
        for (int i = 0; i < 6; i++) {
            String pass = (i == 0) ? "Admin123" : 
                          (i == 1) ? "Vendedor123" : 
                          (i == 2) ? "Disenador123" : 
                          (i == 3) ? "Produccion123" : 
                          (i == 4) ? "Soporte123" : "Cliente123";
            hashes[i] = encoder.encode(pass);
        }
        
        for (int i = 0; i < 6; i++) {
            System.out.print("(" + roles[i] + ", '" + nombres[i] + "', '" + emails[i] + "', '" + hashes[i] + "', '" + telefonos[i] + "', TRUE, NOW())");
            if (i < 5) System.out.println(",");
            else System.out.println(";");
        }
        
        System.out.println();
    }
}