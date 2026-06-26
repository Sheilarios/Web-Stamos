package com.stamos.util;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class ControllerHelper {

    public static boolean hasErrors(BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Por favor, corrige los errores.");
            return true;
        }
        return false;
    }

    public static void addSuccessMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("mensaje", message);
        redirectAttributes.addFlashAttribute("mensajeClass", "alert-success");
    }

    public static void addErrorMessage(RedirectAttributes redirectAttributes, String message) {
        redirectAttributes.addFlashAttribute("error", message);
        redirectAttributes.addFlashAttribute("errorClass", "alert-danger");
    }
}