/**
 * pagos.js
 * Funcionalidades para la gestión de pagos en STAMOS
 * Dependencias: jQuery, DataTables, Bootstrap
 */

$(document).ready(function() {
    // ==================== DATATABLES PARA LISTADO DE PAGOS ====================
    if ($('#tablaPagos').length) {
        $('#tablaPagos').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
            },
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true
        });
    }

    // ==================== CARGAR SALDO PENDIENTE AL SELECCIONAR PROYECTO ====================
    const $proyectoSelect = $('#proyectoId');
    const $saldoPendienteSpan = $('#saldoPendiente');
    const $montoInput = $('#monto');

    if ($proyectoSelect.length && $saldoPendienteSpan.length) {
        $proyectoSelect.on('change', function() {
            const proyectoId = $(this).val();
            if (proyectoId) {
                $.ajax({
                    url: '/internal/pagos/api/saldo-pendiente',
                    method: 'GET',
                    data: { proyectoId: proyectoId },
                    dataType: 'json',
                    success: function(saldo) {
                        $saldoPendienteSpan.text('S/ ' + saldo.toFixed(2));
                        // Si el monto ingresado supera el saldo, mostrar advertencia
                        validarMonto(saldo);
                    },
                    error: function() {
                        $saldoPendienteSpan.text('Error');
                    }
                });
            } else {
                $saldoPendienteSpan.text('S/ 0.00');
            }
        });
    }

    // ==================== VALIDAR MONTO NO SUPERE SALDO PENDIENTE ====================
    function validarMonto(saldoMaximo) {
        if (!$montoInput.length) return;
        const monto = parseFloat($montoInput.val());
        if (!isNaN(monto) && monto > saldoMaximo) {
            $montoInput.addClass('is-invalid');
            $('#montoError').text('El monto no puede superar el saldo pendiente (S/ ' + saldoMaximo.toFixed(2) + ')');
        } else {
            $montoInput.removeClass('is-invalid');
            if ($('#montoError').length) $('#montoError').empty();
        }
    }

    if ($montoInput.length && $proyectoSelect.length) {
        $montoInput.on('input', function() {
            const proyectoId = $proyectoSelect.val();
            if (proyectoId) {
                $.ajax({
                    url: '/internal/pagos/api/saldo-pendiente',
                    method: 'GET',
                    data: { proyectoId: proyectoId },
                    dataType: 'json',
                    success: function(saldo) {
                        validarMonto(saldo);
                    }
                });
            }
        });
    }

    // ==================== VALIDACIÓN FORMULARIO DE PAGO ====================
    const $formPago = $('#formPago');
    if ($formPago.length) {
        $formPago.on('submit', function(e) {
            let isValid = true;
            const proyectoId = $('#proyectoId').val();
            const monto = parseFloat($('#monto').val());
            const fecha = $('#fecha').val();
            const medioPago = $('#medioPago').val();

            if (!proyectoId) {
                mostrarAlerta('Debe seleccionar un proyecto.', 'danger');
                isValid = false;
            }
            if (isNaN(monto) || monto <= 0) {
                mostrarAlerta('El monto debe ser mayor a cero.', 'danger');
                isValid = false;
            }
            if (!fecha) {
                mostrarAlerta('La fecha es obligatoria.', 'danger');
                isValid = false;
            }
            if (!medioPago) {
                mostrarAlerta('Debe seleccionar un medio de pago.', 'danger');
                isValid = false;
            }

            // Verificar nuevamente saldo pendiente vía AJAX sincrónico (opcional)
            if (proyectoId && !isNaN(monto)) {
                $.ajax({
                    url: '/internal/pagos/api/saldo-pendiente',
                    method: 'GET',
                    data: { proyectoId: proyectoId },
                    dataType: 'json',
                    async: false,
                    success: function(saldo) {
                        if (monto > saldo) {
                            mostrarAlerta('El monto excede el saldo pendiente (S/ ' + saldo.toFixed(2) + ')', 'danger');
                            isValid = false;
                        }
                    }
                });
            }

            if (!isValid) e.preventDefault();
        });
    }

    // ==================== ANULAR PAGO (CONFIRMACIÓN Y MOTIVO) ====================
    $(document).on('click', '.btn-anular-pago', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        const motivo = prompt('Ingrese el motivo de la anulación:');
        if (motivo && motivo.trim() !== '') {
            const $form = $('<form method="POST" action="' + url + '"><input type="hidden" name="motivo" value="' + escapeHtml(motivo.trim()) + '"></form>');
            $('body').append($form);
            $form.submit();
        } else if (motivo !== null) {
            alert('Debe especificar un motivo para anular el pago.');
        }
    });

    // ==================== ACTUALIZAR DATOS DEL PROYECTO AL CARGAR PÁGINA (si hay proyecto preseleccionado) ====================
    if ($proyectoSelect.length && $proyectoSelect.val()) {
        $proyectoSelect.trigger('change');
    }

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
            $formPago.prepend(alertHtml);
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
});