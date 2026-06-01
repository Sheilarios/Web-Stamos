/**
 * ajax-interceptor.js
 * Interceptor global de peticiones AJAX para STAMOS
 * Dependencias: jQuery, Bootstrap
 */

$(document).ready(function() {
    // ==================== CONFIGURACIÓN GLOBAL ====================
    // Mostrar indicador de carga global (spinner overlay)
    let requestCount = 0;
    const $loadingOverlay = $('#globalLoadingOverlay');

    // Crear el overlay si no existe en el DOM
    if (!$loadingOverlay.length) {
        const overlayHtml = `
            <div id="globalLoadingOverlay" style="display:none; position:fixed; top:0; left:0; width:100%; height:100%; background:rgba(0,0,0,0.5); z-index:9999; text-align:center; padding-top:20%;">
                <div class="spinner-border text-light" role="status" style="width:3rem; height:3rem;">
                    <span class="visually-hidden">Cargando...</span>
                </div>
                <div class="text-light mt-2">Procesando solicitud...</div>
            </div>
        `;
        $('body').append(overlayHtml);
        window.$loadingOverlay = $('#globalLoadingOverlay');
    }

    function showLoading() {
        if (requestCount === 0) {
            window.$loadingOverlay.fadeIn(200);
        }
        requestCount++;
    }

    function hideLoading() {
        requestCount--;
        if (requestCount <= 0) {
            requestCount = 0;
            window.$loadingOverlay.fadeOut(200);
        }
    }

    // ==================== INTERCEPTOR GLOBAL (jQuery AJAX) ====================
    $(document).ajaxStart(function() {
        // Se puede mostrar un spinner más sutil, pero usamos showLoading()
        showLoading();
    });

    $(document).ajaxStop(function() {
        hideLoading();
    });

    // Interceptar cada petición para manejar errores comunes
    $(document).ajaxError(function(event, jqXHR, settings, thrownError) {
        // Ocultar loading en caso de error (aunque ajaxStop también lo hará)
        hideLoading();

        // Si el error ya fue manejado por el código específico (con error local), evitar duplicar
        if (jqXHR._handled) return;

        // Manejo de códigos de estado HTTP
        const status = jqXHR.status;
        let errorMessage = 'Ocurrió un error inesperado. Intente nuevamente.';

        if (status === 401) {
            errorMessage = 'Su sesión ha expirado. Será redirigido al login.';
            mostrarAlertaGlobal(errorMessage, 'warning');
            setTimeout(function() {
                window.location.href = '/login?expired=true';
            }, 2000);
            return;
        } else if (status === 403) {
            errorMessage = 'No tiene permisos para realizar esta acción.';
        } else if (status === 404) {
            errorMessage = 'El recurso solicitado no existe.';
        } else if (status === 500) {
            errorMessage = 'Error interno del servidor. Consulte al administrador.';
        }

        // Si el servidor devuelve un mensaje de error personalizado en JSON
        if (jqXHR.responseJSON && jqXHR.responseJSON.message) {
            errorMessage = jqXHR.responseJSON.message;
        } else if (jqXHR.responseJSON && jqXHR.responseJSON.error) {
            errorMessage = jqXHR.responseJSON.error;
        }

        mostrarAlertaGlobal(errorMessage, 'danger');
    });

    // Interceptor opcional: antes de enviar, agregar token CSRF si es necesario
    // (Spring Security con CSRF habilitado; por defecto está deshabilitado en la configuración,
    // pero se deja como referencia)
    /*
    $(document).ajaxSend(function(event, jqXHR, settings) {
        const csrfToken = $('meta[name="_csrf"]').attr('content');
        const csrfHeader = $('meta[name="_csrf_header"]').attr('content');
        if (csrfToken && csrfHeader && !settings.headers[csrfHeader]) {
            jqXHR.setRequestHeader(csrfHeader, csrfToken);
        }
    });
    */

    // ==================== FUNCIÓN GLOBAL DE ALERTA ====================
    function mostrarAlertaGlobal(mensaje, tipo) {
        // Crear contenedor si no existe
        let $alertContainer = $('#globalAlertContainer');
        if (!$alertContainer.length) {
            $alertContainer = $('<div id="globalAlertContainer" style="position:fixed; top:20px; right:20px; z-index:10000; min-width:300px;"></div>');
            $('body').append($alertContainer);
        }

        const alertId = 'alert_' + new Date().getTime();
        const alertHtml = `
            <div id="${alertId}" class="alert alert-${tipo} alert-dismissible fade show shadow" role="alert" style="margin-bottom:10px;">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        $alertContainer.prepend(alertHtml);
        setTimeout(function() {
            $('#' + alertId).fadeOut(500, function() { $(this).remove(); });
        }, 5000);
    }

    // Exponer funciones globales para uso en otros scripts
    window.stamosAjax = {
        showLoading: showLoading,
        hideLoading: hideLoading,
        mostrarAlerta: mostrarAlertaGlobal
    };
});