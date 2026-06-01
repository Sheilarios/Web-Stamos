/**
 * usuarios.js
 * Funcionalidades para la gestión de usuarios (empleados) en STAMOS
 * Dependencias: jQuery, DataTables, Bootstrap
 */

$(document).ready(function() {
    // ==================== DATATABLES PARA LISTADO DE USUARIOS ====================
    if ($('#tablaUsuarios').length) {
        $('#tablaUsuarios').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
            },
            order: [[0, 'asc']],
            pageLength: 10,
            responsive: true,
            columnDefs: [
                { orderable: false, targets: [4, 5] } // columnas de acciones y estado no ordenables
            ]
        });
    }

    // ==================== VALIDACIÓN FORMULARIO CREAR/EDITAR USUARIO ====================
    const $formUsuario = $('#formUsuario');
    if ($formUsuario.length) {
        $formUsuario.on('submit', function(e) {
            let isValid = true;
            const email = $('#email').val().trim();
            const nombre = $('#nombre').val().trim();
            const rol = $('#rol').val();
            const emailRegex = /^[^\s@]+@([^\s@.,]+\.)+[^\s@.,]{2,}$/;

            // Limpiar errores previos
            $('.is-invalid').removeClass('is-invalid');
            $('.invalid-feedback').remove();

            if (!nombre) {
                mostrarError('#nombre', 'El nombre es obligatorio.');
                isValid = false;
            }
            if (!email) {
                mostrarError('#email', 'El correo electrónico es obligatorio.');
                isValid = false;
            } else if (!emailRegex.test(email)) {
                mostrarError('#email', 'Ingrese un correo válido.');
                isValid = false;
            }
            if (!rol) {
                mostrarError('#rol', 'Debe seleccionar un rol.');
                isValid = false;
            }

            if (!isValid) {
                e.preventDefault();
            }
        });
    }

    // Función auxiliar para mostrar error debajo del campo
    function mostrarError(selector, mensaje) {
        const $field = $(selector);
        $field.addClass('is-invalid');
        $field.after('<div class="invalid-feedback">' + mensaje + '</div>');
    }

    // ==================== HABILITAR / DESHABILITAR USUARIO ====================
    $(document).on('click', '.btn-deshabilitar-usuario, .btn-habilitar-usuario', function(e) {
        e.preventDefault();
        const $btn = $(this);
        const url = $btn.attr('href');
        const action = $btn.hasClass('btn-deshabilitar-usuario') ? 'deshabilitar' : 'habilitar';
        const message = (action === 'deshabilitar')
            ? '¿Deshabilitar este usuario? No podrá iniciar sesión.'
            : '¿Habilitar este usuario nuevamente?';
        if (confirm(message)) {
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== RESTABLECER CONTRASEÑA (ENVIAR CORREO) ====================
    $(document).on('click', '.btn-reset-password', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        if (confirm('¿Enviar un correo para restablecer la contraseña de este usuario?')) {
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== ELIMINAR USUARIO (OPCIONAL, SEGÚN REGLAS DE NEGOCIO) ====================
    $(document).on('click', '.btn-eliminar-usuario', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        if (confirm('¿Eliminar permanentemente este usuario? Esta acción no se puede deshacer.')) {
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== AUTOCOMPLETADO DE ROLES EN FORMULARIO (ya viene en select) ====================
    // No necesario, pero podemos mostrar un tooltip de ayuda
    $('#rol').tooltip({
        title: 'Los roles determinan los permisos en el sistema',
        placement: 'right'
    });

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
            $('.main-content').prepend(alertHtml);
        }
        setTimeout(() => $('.alert').alert('close'), 5000);
    }

    // Exponer función global (opcional)
    window.usuarios = { mostrarAlerta: mostrarAlerta };
});