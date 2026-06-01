/**
 * perfil.js
 * Funcionalidades para la gestión del perfil de usuario en STAMOS
 * Dependencias: jQuery, Bootstrap
 */

$(document).ready(function() {
    // ==================== MOSTRAR/OCULTAR CONTRASEÑA ====================
    $('.toggle-password').on('click', function() {
        const target = $(this).data('target');
        const $input = $(target);
        const type = $input.attr('type') === 'password' ? 'text' : 'password';
        $input.attr('type', type);
        $(this).find('i').toggleClass('bi-eye bi-eye-slash');
    });

    // ==================== VALIDACIÓN Y ENVÍO DEL FORMULARIO DE CAMBIO DE CONTRASEÑA ====================
    const $formPassword = $('#formCambiarPassword');
    if ($formPassword.length) {
        $formPassword.on('submit', function(e) {
            let isValid = true;
            const currentPassword = $('#currentPassword').val();
            const newPassword = $('#newPassword').val();
            const confirmPassword = $('#confirmPassword').val();

            // Limpiar mensajes previos
            $('.invalid-feedback').remove();
            $('.is-invalid').removeClass('is-invalid');

            // Validar contraseña actual
            if (!currentPassword) {
                mostrarError('#currentPassword', 'La contraseña actual es obligatoria.');
                isValid = false;
            }

            // Validar nueva contraseña
            if (!newPassword) {
                mostrarError('#newPassword', 'La nueva contraseña es obligatoria.');
                isValid = false;
            } else if (newPassword.length < 8) {
                mostrarError('#newPassword', 'La nueva contraseña debe tener al menos 8 caracteres.');
                isValid = false;
            }

            // Validar confirmación
            if (!confirmPassword) {
                mostrarError('#confirmPassword', 'Debe confirmar la nueva contraseña.');
                isValid = false;
            } else if (newPassword !== confirmPassword) {
                mostrarError('#confirmPassword', 'Las contraseñas no coinciden.');
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
                return false;
            }

            // Envío normal del formulario (POST a /cambiar-contraseña)
            // Si se desea AJAX, se puede implementar (ver opción más abajo)
        });
    }

    // Función auxiliar para mostrar errores debajo del campo
    function mostrarError(selector, mensaje) {
        const $field = $(selector);
        $field.addClass('is-invalid');
        $field.after('<div class="invalid-feedback">' + mensaje + '</div>');
    }

    // ==================== OPCIÓN: ENVÍO MEDIANTE AJAX (mejor experiencia) ====================
    // (Comentado por defecto, se puede activar descomentando y cambiando el submit normal)
    /*
    if ($formPassword.length) {
        $formPassword.on('submit', function(e) {
            e.preventDefault();
            const $btn = $(this).find('button[type="submit"]');
            const originalText = $btn.text();

            // Realizar las mismas validaciones que arriba
            let isValid = true;
            const currentPassword = $('#currentPassword').val();
            const newPassword = $('#newPassword').val();
            const confirmPassword = $('#confirmPassword').val();

            $('.invalid-feedback').remove();
            $('.is-invalid').removeClass('is-invalid');

            if (!currentPassword) {
                mostrarError('#currentPassword', 'La contraseña actual es obligatoria.');
                isValid = false;
            }
            if (!newPassword) {
                mostrarError('#newPassword', 'La nueva contraseña es obligatoria.');
                isValid = false;
            } else if (newPassword.length < 8) {
                mostrarError('#newPassword', 'La nueva contraseña debe tener al menos 8 caracteres.');
                isValid = false;
            }
            if (!confirmPassword) {
                mostrarError('#confirmPassword', 'Debe confirmar la nueva contraseña.');
                isValid = false;
            } else if (newPassword !== confirmPassword) {
                mostrarError('#confirmPassword', 'Las contraseñas no coinciden.');
                isValid = false;
            }

            if (!isValid) return;

            $btn.prop('disabled', true).html('<span class="spinner-border spinner-border-sm"></span> Procesando...');

            $.ajax({
                url: $formPassword.attr('action'),
                method: 'POST',
                data: $formPassword.serialize(),
                success: function(response) {
                    // Si el servidor responde con éxito (redirección o JSON)
                    if (response.success) {
                        mostrarAlerta('Contraseña actualizada correctamente. Será redirigido al login.', 'success');
                        setTimeout(function() {
                            window.location.href = '/login?logout';
                        }, 2000);
                    } else {
                        mostrarAlerta(response.error || 'Error al cambiar la contraseña.', 'danger');
                        $btn.prop('disabled', false).text(originalText);
                    }
                },
                error: function(xhr) {
                    let errorMsg = 'Error en el servidor. Intente nuevamente.';
                    if (xhr.responseJSON && xhr.responseJSON.error) {
                        errorMsg = xhr.responseJSON.error;
                    }
                    mostrarAlerta(errorMsg, 'danger');
                    $btn.prop('disabled', false).text(originalText);
                }
            });
        });
    }
    */

    // ==================== ACTUALIZACIÓN DE DATOS DEL PERFIL (OPCIONAL) ====================
    // Si se implementa la edición de nombre, email, etc., este código puede servir
    const $formPerfil = $('#formEditarPerfil');
    if ($formPerfil.length) {
        $formPerfil.on('submit', function(e) {
            let isValid = true;
            const nombre = $('#nombre').val().trim();
            const email = $('#email').val().trim();
            const emailRegex = /^[^\s@]+@([^\s@.,]+\.)+[^\s@.,]{2,}$/;

            if (!nombre) {
                mostrarError('#nombre', 'El nombre es obligatorio.');
                isValid = false;
            }
            if (!email) {
                mostrarError('#email', 'El correo electrónico es obligatorio.');
                isValid = false;
            } else if (!emailRegex.test(email)) {
                mostrarError('#email', 'El correo no es válido.');
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    }

    // ==================== FUNCIONES AUXILIARES ====================
    function mostrarAlerta(mensaje, tipo) {
        const alertHtml = `
            <div class="alert alert-${tipo} alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        const $container = $('#alertContainer');
        if ($container.length) {
            $container.html(alertHtml);
        } else {
            $('.card-body').first().prepend(alertHtml);
        }
        setTimeout(() => $('.alert').alert('close'), 5000);
    }
});