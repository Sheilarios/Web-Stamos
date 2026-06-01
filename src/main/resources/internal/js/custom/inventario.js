/* global FullCalendar */

/**
 * inventario.js
 * Funcionalidades para la gestión de inventario (equipos y reservas) en STAMOS
 * Dependencias: jQuery, DataTables, FullCalendar, Bootstrap, Moment.js
 */

$(document).ready(function() {
    // ==================== DATATABLES PARA LISTA DE EQUIPOS ====================
    if ($('#tablaEquipos').length) {
        $('#tablaEquipos').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
            },
            order: [[0, 'asc']],
            pageLength: 10,
            responsive: true
        });
    }

    // ==================== DATATABLES PARA LISTA DE RESERVAS ====================
    if ($('#tablaReservas').length) {
        $('#tablaReservas').DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
            },
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true
        });
    }

    // ==================== VALIDACIÓN FORMULARIO EQUIPO ====================
    const $formEquipo = $('#formEquipo');
    if ($formEquipo.length) {
        $formEquipo.on('submit', function(e) {
            let isValid = true;
            const nombre = $('#nombre').val().trim();
            const cantidadTotal = parseInt($('#cantidadTotal').val());
            const ubicacion = $('#ubicacion').val().trim();

            if (nombre === '') {
                mostrarAlerta('El nombre del equipo es obligatorio.', 'danger');
                isValid = false;
            }
            if (isNaN(cantidadTotal) || cantidadTotal < 0) {
                mostrarAlerta('La cantidad total debe ser un número mayor o igual a cero.', 'danger');
                isValid = false;
            }
            if (ubicacion === '') {
                mostrarAlerta('La ubicación es obligatoria.', 'danger');
                isValid = false;
            }
            if (!isValid) e.preventDefault();
        });
    }

    // ==================== ELIMINAR EQUIPO (CONFIRMACIÓN) ====================
    $(document).on('click', '.btn-eliminar-equipo', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        if (confirm('¿Eliminar este equipo? Se eliminarán también sus reservas asociadas (si las hay).')) {
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== CALENDARIO DE RESERVAS (FullCalendar) ====================
    const $calendar = $('#calendar');
    if ($calendar.length) {
        let calendar = null;
        let currentEquipoId = $('#filtroEquipo').val() || null;

        function loadCalendar(equipoId) {
            $.ajax({
                url: '/internal/inventario/reservas/eventos',
                data: equipoId ? { equipoId: equipoId } : {},
                method: 'GET',
                dataType: 'json',
                success: function(events) {
                    if (calendar) calendar.destroy();
                    calendar = new FullCalendar.Calendar($calendar[0], {
                        initialView: 'dayGridMonth',
                        locale: 'es',
                        headerToolbar: {
                            left: 'prev,next today',
                            center: 'title',
                            right: 'dayGridMonth,timeGridWeek'
                        },
                        events: events.map(function(ev) {
                            return {
                                title: ev.title,
                                start: ev.start,
                                end: ev.end,
                                backgroundColor: ev.backgroundColor,
                                borderColor: ev.backgroundColor
                            };
                        }),
                        eventClick: function(info) {
                            // Mostrar detalles de la reserva en un modal (opcional)
                            alert('Reserva: ' + info.event.title);
                        }
                    });
                    calendar.render();
                },
                error: function() {
                    mostrarAlerta('Error cargando el calendario de reservas.', 'danger');
                }
            });
        }

        loadCalendar(currentEquipoId);

        $('#filtroEquipo').on('change', function() {
            currentEquipoId = $(this).val() || null;
            loadCalendar(currentEquipoId);
        });
    }

    // ==================== RESERVAS: FORMULARIO NUEVA / EDITAR ====================
    const $formReserva = $('#formReserva');
    if ($formReserva.length) {
        // Pre-cargar datos de disponibilidad al cambiar fechas o equipo
        const $equipoId = $('#equipoId');
        const $fechaInicio = $('#fechaInicio');
        const $fechaFin = $('#fechaFin');
        const $cantidad = $('#cantidad');
        const $disponibilidadMsg = $('#disponibilidadMsg');

        function checkDisponibilidad() {
            const equipoId = $equipoId.val();
            const fechaInicio = $fechaInicio.val();
            const fechaFin = $fechaFin.val();
            const cantidad = parseInt($cantidad.val());
            const reservaId = $('#reservaId').val(); // null si nueva

            if (!equipoId || !fechaInicio || !fechaFin || isNaN(cantidad)) {
                $disponibilidadMsg.html('');
                return;
            }

            $.ajax({
                url: '/internal/inventario/api/disponibilidad-equipo',
                data: {
                    equipoId: equipoId,
                    fechaInicio: fechaInicio,
                    fechaFin: fechaFin,
                    cantidad: cantidad,
                    reservaId: reservaId || ''
                },
                dataType: 'json',
                success: function(data) {
                    if (data.disponible) {
                        $disponibilidadMsg.html('<span class="text-success">✓ Disponible. Cantidad disponible: ' + data.cantidadDisponible + '</span>');
                    } else {
                        $disponibilidadMsg.html('<span class="text-danger">✗ No hay suficiente disponibilidad en el período seleccionado. Disponible: ' + data.cantidadDisponible + '</span>');
                    }
                },
                error: function() {
                    $disponibilidadMsg.html('<span class="text-warning">No se pudo verificar disponibilidad.</span>');
                }
            });
        }

        $equipoId.on('change', checkDisponibilidad);
        $fechaInicio.on('change', checkDisponibilidad);
        $fechaFin.on('change', checkDisponibilidad);
        $cantidad.on('input', checkDisponibilidad);

        // Validación antes de enviar
        $formReserva.on('submit', function(e) {
            const disponible = $disponibilidadMsg.find('.text-success').length > 0;
            if (!disponible) {
                e.preventDefault();
                mostrarAlerta('No se puede guardar la reserva porque no hay suficiente disponibilidad.', 'danger');
            }
        });
    }

    // ==================== CANCELAR RESERVA (CONFIRMACIÓN) ====================
    $(document).on('click', '.btn-cancelar-reserva', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        if (confirm('¿Cancelar esta reserva? Se liberará el equipo para otras fechas.')) {
            const $form = $('<form method="POST" action="' + url + '"></form>');
            $('body').append($form);
            $form.submit();
        }
    });

    // ==================== CONSULTA DE DISPONIBILIDAD POR FECHAS ====================
    const $formDisponibilidad = $('#formDisponibilidad');
    if ($formDisponibilidad.length) {
        $formDisponibilidad.on('submit', function(e) {
            e.preventDefault();
            const fechaInicio = $('#fechaInicioConsulta').val();
            const fechaFin = $('#fechaFinConsulta').val();
            if (!fechaInicio || !fechaFin) {
                mostrarAlerta('Debe seleccionar ambas fechas.', 'danger');
                return;
            }
            window.location.href = '/internal/inventario/disponibilidad/consultar?fechaInicio=' + fechaInicio + '&fechaFin=' + fechaFin;
        });
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
            $('.card').first().prepend(alertHtml);
        }
        setTimeout(() => $('.alert').alert('close'), 5000);
    }
});