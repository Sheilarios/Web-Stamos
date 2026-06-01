/**
 * cotizaciones.js
 * Funcionalidades para la gestión de cotizaciones en STAMOS
 * Dependencias: jQuery, DataTables, Bootstrap, autocomplete (jQuery UI opcional)
 */

$(document).ready(function() {
    // ==================== DATATABLES EN LISTADO ====================
    if ($('#tablaCotizaciones').length) {
        $('#tablaCotizaciones').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
            },
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true
        });
    }

    // ==================== AUTOCOMPLETADO DE CLIENTES ====================
    const $clienteInput = $('#clienteNombre');
    const $clienteIdHidden = $('#clienteId');
    if ($clienteInput.length) {
        $clienteInput.autocomplete({
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
                                id: item.id
                            };
                        }));
                    },
                    error: function() { response([]); }
                });
            },
            minLength: 2,
            select: function(event, ui) {
                $clienteInput.val(ui.item.label);
                $clienteIdHidden.val(ui.item.id);
                return false;
            }
        });
    }

    // ==================== AUTOCOMPLETADO DE PRODUCTOS (catálogo) ====================
    const $productoInput = $('#productoNombre');
    const $productoIdHidden = $('#productoId');
    const $precioUnitario = $('#precioUnitario');
    const $cantidad = $('#cantidad');

    if ($productoInput.length) {
        $productoInput.autocomplete({
            source: function(request, response) {
                $.ajax({
                    url: '/internal/productos/buscar', // endpoint ficticio, ajustar según controlador real
                    data: { term: request.term },
                    dataType: 'json',
                    success: function(data) {
                        response($.map(data, function(item) {
                            return {
                                label: item.nombre,
                                value: item.nombre,
                                id: item.id,
                                precio: item.precioBase
                            };
                        }));
                    },
                    error: function() { response([]); }
                });
            },
            minLength: 1,
            select: function(event, ui) {
                $productoInput.val(ui.item.label);
                $productoIdHidden.val(ui.item.id);
                if ($precioUnitario.length) {
                    $precioUnitario.val(ui.item.precio.toFixed(2));
                }
                return false;
            }
        });
    }

    // ==================== AGREGAR ÍTEMS A LA COTIZACIÓN ====================
    const $btnAgregarItem = $('#agregarItem');
    const $tablaItems = $('#tablaItems tbody');
    let itemCounter = 0;

    if ($btnAgregarItem.length) {
        $btnAgregarItem.on('click', function() {
            const concepto = $('#concepto').val().trim();
            const cantidad = parseInt($('#cantidad').val());
            const precioUnitario = parseFloat($('#precioUnitario').val());
            const productoNombre = $('#productoNombre').val().trim();

            if (concepto === '' && productoNombre === '') {
                mostrarAlerta('Debe ingresar un concepto o seleccionar un producto.', 'danger');
                return;
            }
            if (isNaN(cantidad) || cantidad <= 0) {
                mostrarAlerta('La cantidad debe ser mayor a cero.', 'danger');
                return;
            }
            if (isNaN(precioUnitario) || precioUnitario <= 0) {
                mostrarAlerta('El precio unitario debe ser mayor a cero.', 'danger');
                return;
            }

            const subtotal = cantidad * precioUnitario;
            const fila = `
                <tr data-item-id="${itemCounter}">
                    <td><input type="hidden" name="items[${itemCounter}].concepto" value="${escapeHtml(concepto || productoNombre)}">${escapeHtml(concepto || productoNombre)}</td>
                    <td><input type="hidden" name="items[${itemCounter}].cantidad" value="${cantidad}">${cantidad}</td>
                    <td><input type="hidden" name="items[${itemCounter}].precioUnitario" value="${precioUnitario}">${precioUnitario.toFixed(2)}</td>
                    <td class="subtotal-item">${subtotal.toFixed(2)}</td>
                    <td><button type="button" class="btn btn-sm btn-danger eliminar-item"><i class="bi bi-trash"></i></button></td>
                </tr>
            `;
            $tablaItems.append(fila);
            recalcularTotales();
            limpiarCamposItem();
            itemCounter++;
        });
    }

    // Eliminar ítem
    $(document).on('click', '.eliminar-item', function() {
        $(this).closest('tr').remove();
        recalcularTotales();
    });

    // ==================== CÁLCULO DE TOTALES ====================
    function recalcularTotales() {
        let subtotal = 0;
        $('.subtotal-item').each(function() {
            subtotal += parseFloat($(this).text());
        });
        const igvPorcentaje = 18.0; // Debe coincidir con el backend (configurable)
        const igv = subtotal * (igvPorcentaje / 100);
        const total = subtotal + igv;

        $('#subtotal').val(subtotal.toFixed(2));
        $('#igv').val(igv.toFixed(2));
        $('#total').val(total.toFixed(2));
    }

    function limpiarCamposItem() {
        $('#concepto').val('');
        $('#productoNombre').val('');
        $('#productoId').val('');
        $('#cantidad').val('1');
        $('#precioUnitario').val('');
    }

    // ==================== VALIDACIÓN DEL FORMULARIO ====================
    const $formCotizacion = $('#formCotizacion');
    if ($formCotizacion.length) {
        $formCotizacion.on('submit', function(e) {
            // Validar al menos un ítem
            if ($tablaItems.children().length === 0) {
                e.preventDefault();
                mostrarAlerta('La cotización debe contener al menos un ítem.', 'danger');
                return;
            }
            // Validar cliente
            const clienteId = $clienteIdHidden.val();
            if (!clienteId) {
                e.preventDefault();
                mostrarAlerta('Debe seleccionar un cliente válido.', 'danger');
                return;
            }
            // Enviar formulario
        });
    }

    // ==================== APROBAR / RECHAZAR / ANULAR COTIZACIÓN ====================
    $(document).on('click', '.btn-aprobar, .btn-rechazar, .btn-anular', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        const action = $(this).hasClass('btn-aprobar') ? 'aprobar' : ($(this).hasClass('btn-rechazar') ? 'rechazar' : 'anular');
        let message = '';
        if (action === 'aprobar') {
            message = '¿Aprobar esta cotización? Una vez aprobada no podrá modificarse.';
        } else if (action === 'rechazar') {
            message = '¿Rechazar esta cotización?';
        } else {
            message = '¿Anular esta cotización? Esta acción no se puede deshacer.';
        }
        if (confirm(message)) {
            // Si es rechazar o anular, podemos pedir motivo
            if (action === 'rechazar' || action === 'anular') {
                let motivo = prompt('Motivo:');
                if (motivo === null) return;
                const $form = $('<form method="POST" action="' + url + '"><input type="hidden" name="motivo" value="' + escapeHtml(motivo) + '"></form>');
                $('body').append($form);
                $form.submit();
            } else {
                const $form = $('<form method="POST" action="' + url + '"></form>');
                $('body').append($form);
                $form.submit();
            }
        }
    });

    // ==================== ENVIAR COTIZACIÓN POR CORREO ====================
    $(document).on('click', '.btn-enviar-correo', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        if (confirm('¿Enviar esta cotización por correo electrónico al cliente?')) {
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== CREAR PROYECTO DESDE COTIZACIÓN ====================
    $(document).on('click', '.btn-crear-proyecto', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        if (confirm('¿Crear un proyecto a partir de esta cotización aprobada?')) {
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== FUNCIONES AUXILIARES ====================
    function mostrarAlerta(mensaje, tipo) {
        tipo = tipo || 'success';
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
            $formCotizacion.prepend(alertHtml);
        }
        setTimeout(() => $('.alert').alert('close'), 5000);
    }

    function escapeHtml(text) {
        if (!text) return '';
        return text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    // Exportar funciones globales (opcional)
    window.stamosCotizaciones = {
        recalcularTotales,
        mostrarAlerta
    };
});