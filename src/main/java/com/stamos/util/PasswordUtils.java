package com.stamos.util;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Clase utilitaria para operaciones con contraseñas.
 * Proporciona generación de contraseñas temporales y validación de fortaleza.
 */
public final class PasswordUtils {

    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";
    private static final int TEMP_PASSWORD_LENGTH = 8;
    private static final Random RANDOM = new SecureRandom();

    private PasswordUtils() {
        // Constructor privado para evitar instanciación
    }

    /**
     * Genera una contraseña temporal aleatoria.
     * Contiene letras mayúsculas, minúsculas y dígitos (excluye caracteres confusos como 0, O, I, l).
     *
     * @return Contraseña temporal de 8 caracteres
     */
    public static String generateTemporaryPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASSWORD_LENGTH);
        for (int i = 0; i < TEMP_PASSWORD_LENGTH; i++) {
            int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Valida que una contraseña cumpla con los requisitos mínimos de seguridad.
     * Requisitos:
     * - Longitud mínima de 8 caracteres
     * - Al menos una letra mayúscula
     * - Al menos una letra minúscula
     * - Al menos un dígito
     *
     * @param password Contraseña a validar
     * @return true si cumple los requisitos, false en caso contrario
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
        }
        return hasUpper && hasLower && hasDigit;
    }

    /**
     * Obtiene un mensaje descriptivo de los requisitos de contraseña.
     *
     * @return String con los requisitos
     */
    public static String getPasswordRequirementsMessage() {
        return "La contraseña debe tener al menos 8 caracteres, incluyendo una mayúscula, una minúscula y un dígito.";
    }
}