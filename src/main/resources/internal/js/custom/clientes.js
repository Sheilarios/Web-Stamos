/**
 * clientes.js
 * Funcionalidades para la gestión de clientes en el sistema STAMOS
 * Dependencias: jQuery, DataTables (opcional), Bootstrap
 */

$(document).ready(function() {
    // Inicializar DataTables en la tabla de clientes (si existe)
    if ($('#tablaClientes').length) {
        $('#tablaClientes').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
            },
            order: [[0, 'asc']],
            pageLength: 10,
            responsive: true
        });
    }

    // ==================== AUTOCOMPLETADO DE CLIENTES ====================
    // Para inputs con clase "cliente-autocomplete"
    $('.cliente-autocomplete').each(function() {
        const $input = $(this);
        const hiddenId = $input.data('hidden-id'); // ID del campo oculto para almacenar el ID del cliente
        const $hiddenField = hiddenId ? $('#' + hiddenId) : null;

        $input.autocomplete({
            source: function(request, response) {
                $.ajax({
                    url: '/internal/clientes/buscar',
                    data: { term: request.term },
                    dataType: 'json',
                    success: function(data) {
                        response($.map(data, function(item) {
                            return {
                                label: item.nombre + (item.ruc ? ' (' + item.ruc + ')' : ''),
                                value: item.nombre,
                                id: item.id,
                                ruc: item.ruc
                            };
                        }));
                    },
                    error: function() {
                        response([]);
                    }
                });
            },
            minLength: 2,
            select: function(event, ui) {
                $input.val(ui.item.label);
                if ($hiddenField) {
                    $hiddenField.val(ui.item.id);
                }
                return false;
            }
        });
    });

    // ==================== VALIDACIÓN DE FORMULARIO DE CLIENTE ====================
    const $formCliente = $('#formCliente');
    if ($formCliente.length) {
        $formCliente.on('submit', function(e) {
            let isValid = true;
            const errors = [];

            // Validar nombre/razón social
            const nombre = $('#nombreRazonSocial').val().trim();
            if (nombre === '') {
                errors.push('El nombre o razón social es obligatorio.');
                $('#nombreRazonSocial').addClass('is-invalid');
                isValid = false;
            } else {
                $('#nombreRazonSocial').removeClass('is-invalid').addClass('is-valid');
            }

            // Validar teléfono
            const telefono = $('#telefono').val().trim();
            if (telefono === '') {
                errors.push('El teléfono es obligatorio.');
                $('#telefono').addClass('is-invalid');
                isValid = false;
            } else {
                $('#telefono').removeClass('is-invalid').addClass('is-valid');
            }

            // Validar email
            const email = $('#email').val().trim();
            const emailRegex = /^[^\s@]+@([^\s@.,]+\.)+[^\s@.,]{2,}$/;
            if (email === '') {
                errors.push('El correo electrónico es obligatorio.');
                $('#email').addClass('is-invalid');
                isValid = false;
            } else if (!emailRegex.test(email)) {
                errors.push('El correo electrónico no es válido.');
                $('#email').addClass('is-invalid');
                isValid = false;
            } else {
                $('#email').removeClass('is-invalid').addClass('is-valid');
            }

            // Validar tipo de persona (radio o select)
            const tipoPersona = $('input[name="tipoPersona"]:checked').val();
            if (!tipoPersona) {
                errors.push('Seleccione el tipo de persona (Natural o Empresa).');
                isValid = false;
            }

            // Si es empresa y RUC no es opcional (depende de validación backend)
            const ruc = $('#ruc').val().trim();
            if (tipoPersona === 'EMPRESA' && ruc === '') {
                errors.push('Para empresas, el RUC es obligatorio.');
                $('#ruc').addClass('is-invalid');
                isValid = false;
            } else if (ruc !== '' && !/^\d{11}$/.test(ruc)) {
                errors.push('El RUC debe tener 11 dígitos.');
                $('#ruc').addClass('is-invalid');
                isValid = false;
            } else {
                $('#ruc').removeClass('is-invalid').addClass('is-valid');
            }

            if (!isValid) {
                e.preventDefault();
                mostrarAlertaError(errors.join('<br>'));
            }
        });

        // Limpiar validación al escribir
        $formCliente.find('input, select').on('input change', function() {
            $(this).removeClass('is-invalid is-valid');
        });
    }

    // ==================== BÚSQUEDA EN TIEMPO REAL (clientes/lista) ====================
    const $searchInput = $('#searchClientes');
    if ($searchInput.length) {
        let timeout = null;
        $searchInput.on('keyup', function() {
            clearTimeout(timeout);
            timeout = setTimeout(function() {
                const term = $searchInput.val();
                window.location.href = '/internal/clientes/lista?searchTerm=' + encodeURIComponent(term);
            }, 500);
        });
    }

    // ==================== CONFIRMAR DESHABILITAR / HABILITAR ====================
    $(document).on('click', '.btn-deshabilitar, .btn-habilitar', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        const action = $(this).hasClass('btn-deshabilitar') ? 'deshabilitar' : 'habilitar';
        const message = action === 'deshabilitar' 
            ? '¿Estás seguro de deshabilitar este cliente? No podrá generar nuevas cotizaciones ni proyectos.'
            : '¿Estás seguro de habilitar este cliente nuevamente?';
        
        if (confirm(message)) {
            // Crear formulario POST para la acción
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== FUNCIONES AUXILIARES ====================
    function mostrarAlertaError(mensaje) {
        const alertHtml = `
            <div class="alert alert-danger alert-dismissible fade show" role="alert">
                <strong>Error:</strong> ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        const $container = $('#alertContainer');
        if ($container.length) {
            $container.html(alertHtml);
        } else {
            // Si no hay contenedor, mostrar al inicio del formulario
            $('#formCliente').prepend(alertHtml);
        }
        setTimeout(() => $('.alert').alert('close'), 5000);
    }

    function mostrarAlertaExito(mensaje) {
        const alertHtml = `
            <div class="alert alert-success alert-dismissible fade show" role="alert">
                ${mensaje}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        `;
        const $container = $('#alertContainer');
        if ($container.length) {
            $container.html(alertHtml);
        } else {
            $('#formCliente').prepend(alertHtml);
        }
        setTimeout(() => $('.alert').alert('close'), 3000);
    }

    // Exportar funciones globales si se necesitan (opcional)
    window.stamosClientes = {
        mostrarAlertaError,
        mostrarAlertaExito
    };
});