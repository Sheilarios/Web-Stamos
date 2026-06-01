/**
 * proyectos.js
 * Funcionalidades para la gestión de proyectos en STAMOS
 * Dependencias: jQuery, DataTables, Bootstrap, Moment.js (opcional)
 */

$(document).ready(function() {
    // ==================== DATATABLES PARA LISTADO DE PROYECTOS ====================
    const $tablaProyectos = $('#tablaProyectos');
    if ($tablaProyectos.length) {
        $tablaProyectos.DataTable({
            language: {
                url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json'
            },
            order: [[0, 'desc']],
            pageLength: 10,
            responsive: true
        });
    }

    // ==================== FILTROS EN LISTADO ====================
    const $filtroEstado = $('#filtroEstado');
    const $filtroCliente = $('#filtroCliente');
    if ($filtroEstado.length || $filtroCliente.length) {
        function aplicarFiltros() {
            let url = '/internal/proyectos/lista?';
            if ($filtroEstado.val()) url += 'estado=' + encodeURIComponent($filtroEstado.val()) + '&';
            if ($filtroCliente.val()) url += 'clienteId=' + encodeURIComponent($filtroCliente.val()) + '&';
            window.location.href = url.slice(0, -1); // eliminar último & si existe
        }
        $filtroEstado.on('change', aplicarFiltros);
        $filtroCliente.on('change', aplicarFiltros);
    }

    // ==================== CAMBIO DE ESTADO ====================
    const $modalEstado = $('#modalCambiarEstado');
    if ($modalEstado.length) {
        let proyectoId = null;
        $(document).on('click', '.btn-cambiar-estado', function(e) {
            e.preventDefault();
            proyectoId = $(this).data('id');
            const url = $(this).attr('href');
            $.get(url, function(html) {
                // Cargar el formulario de cambio de estado en el modal
                $modalEstado.find('.modal-body').html(html);
                $modalEstado.modal('show');
            }).fail(function() {
                mostrarAlerta('Error al cargar el formulario.', 'danger');
            });
        });

        // Al enviar el formulario dentro del modal (delegación)
        $modalEstado.on('submit', '#formCambiarEstado', function(e) {
            e.preventDefault();
            const $form = $(this);
            const nuevoEstado = $form.find('select[name="nuevoEstado"]').val();
            if (!nuevoEstado) {
                mostrarAlerta('Debe seleccionar un estado.', 'danger');
                return;
            }
            $.ajax({
                url: $form.attr('action'),
                method: 'POST',
                data: { nuevoEstado: nuevoEstado },
                success: function(response) {
                    $modalEstado.modal('hide');
                    mostrarAlerta('Estado actualizado correctamente.', 'success');
                    setTimeout(function() { location.reload(); }, 1500);
                },
                error: function(xhr) {
                    let errorMsg = 'Error al cambiar el estado.';
                    if (xhr.responseJSON && xhr.responseJSON.error) errorMsg = xhr.responseJSON.error;
                    mostrarAlerta(errorMsg, 'danger');
                }
            });
        });
    }

    // ==================== ASIGNAR RESPONSABLE ====================
    $(document).on('click', '.btn-asignar-responsable', function(e) {
        e.preventDefault();
        const url = $(this).data('url');
        const proyectoId = $(this).data('id');
        const $modal = $('#modalAsignarResponsable');
        if ($modal.length) {
            $.get(url, function(html) {
                $modal.find('.modal-body').html(html);
                $modal.modal('show');
            }).fail(function() {
                mostrarAlerta('Error al cargar formulario de responsables.', 'danger');
            });
        } else {
            // Si no hay modal, redirigir a una página específica (opcional)
            window.location.href = '/internal/proyectos/asignar-responsable/' + proyectoId;
        }
    });

    // Envío de asignación (delegación)
    $(document).on('submit', '#formAsignarResponsable', function(e) {
        e.preventDefault();
        const $form = $(this);
        const usuarioId = $form.find('select[name="usuarioId"]').val();
        if (!usuarioId) {
            mostrarAlerta('Debe seleccionar un responsable.', 'danger');
            return;
        }
        $.ajax({
            url: $form.attr('action'),
            method: 'POST',
            data: $form.serialize(),
            success: function() {
                $('#modalAsignarResponsable').modal('hide');
                mostrarAlerta('Responsable asignado correctamente.', 'success');
                setTimeout(function() { location.reload(); }, 1500);
            },
            error: function() {
                mostrarAlerta('Error al asignar responsable.', 'danger');
            }
        });
    });

    // ==================== SUBIR ARCHIVOS (RENDER, PLANO, BOCETO) ====================
    const $formArchivo = $('#formSubirArchivo');
    if ($formArchivo.length) {
        $formArchivo.on('submit', function(e) {
            e.preventDefault();
            const formData = new FormData(this);
            const url = $(this).attr('action');
            $.ajax({
                url: url,
                method: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function(response) {
                    mostrarAlerta('Archivo subido correctamente.', 'success');
                    // Recargar la lista de archivos (si está en un modal o en la página)
                    if ($('#listaArchivos').length) {
                        $('#listaArchivos').load(window.location.href + ' #listaArchivos');
                    } else {
                        setTimeout(function() { location.reload(); }, 1500);
                    }
                },
                error: function() {
                    mostrarAlerta('Error al subir el archivo.', 'danger');
                }
            });
        });
    }

    // ==================== AGREGAR ALQUILER DE EQUIPO ====================
    const $modalAlquiler = $('#modalAgregarAlquiler');
    if ($modalAlquiler.length) {
        $(document).on('click', '.btn-agregar-alquiler', function(e) {
            e.preventDefault();
            const url = $(this).data('url');
            $.get(url, function(html) {
                $modalAlquiler.find('.modal-body').html(html);
                $modalAlquiler.modal('show');
                // Inicializar validación de disponibilidad dentro del modal
                inicializarVerificacionDisponibilidad();
            }).fail(function() {
                mostrarAlerta('Error al cargar formulario de alquiler.', 'danger');
            });
        });

        // Envío del formulario de alquiler
        $(document).on('submit', '#formAgregarAlquiler', function(e) {
            e.preventDefault();
            const $form = $(this);
            const equipoId = $form.find('select[name="equipoId"]').val();
            const fechaInicio = $form.find('input[name="fechaInicio"]').val();
            const fechaFin = $form.find('input[name="fechaFin"]').val();
            const cantidad = $form.find('input[name="cantidad"]').val();

            if (!equipoId || !fechaInicio || !fechaFin || !cantidad) {
                mostrarAlerta('Todos los campos son obligatorios.', 'danger');
                return;
            }
            if (new Date(fechaInicio) >= new Date(fechaFin)) {
                mostrarAlerta('La fecha de inicio debe ser anterior a la fecha de fin.', 'danger');
                return;
            }
            $.ajax({
                url: $form.attr('action'),
                method: 'POST',
                data: $form.serialize(),
                success: function() {
                    $modalAlquiler.modal('hide');
                    mostrarAlerta('Alquiler registrado correctamente.', 'success');
                    setTimeout(function() { location.reload(); }, 1500);
                },
                error: function(xhr) {
                    let errorMsg = 'Error al registrar alquiler.';
                    if (xhr.responseJSON && xhr.responseJSON.error) errorMsg = xhr.responseJSON.error;
                    mostrarAlerta(errorMsg, 'danger');
                }
            });
        });
    }

    // Verificación de disponibilidad en tiempo real (para alquiler)
    function inicializarVerificacionDisponibilidad() {
        const $equipoSelect = $('#equipoId');
        const $fechaInicio = $('#fechaInicioAlquiler');
        const $fechaFin = $('#fechaFinAlquiler');
        const $cantidad = $('#cantidadAlquiler');
        const $mensajeDisponibilidad = $('#disponibilidadMsg');

        function checkDisponibilidad() {
            const equipoId = $equipoSelect.val();
            const fechaInicio = $fechaInicio.val();
            const fechaFin = $fechaFin.val();
            const cantidad = $cantidad.val();
            if (!equipoId || !fechaInicio || !fechaFin || !cantidad) {
                $mensajeDisponibilidad.html('');
                return;
            }
            $.ajax({
                url: '/internal/inventario/api/disponibilidad-equipo',
                data: {
                    equipoId: equipoId,
                    fechaInicio: fechaInicio,
                    fechaFin: fechaFin,
                    cantidad: cantidad,
                    reservaId: ''
                },
                dataType: 'json',
                success: function(data) {
                    if (data.disponible) {
                        $mensajeDisponibilidad.html('<span class="text-success">✓ Disponible. Cantidad disponible: ' + data.cantidadDisponible + '</span>');
                    } else {
                        $mensajeDisponibilidad.html('<span class="text-danger">✗ No hay suficiente disponibilidad en el período. Disponible: ' + data.cantidadDisponible + '</span>');
                    }
                },
                error: function() {
                    $mensajeDisponibilidad.html('<span class="text-warning">No se pudo verificar disponibilidad.</span>');
                }
            });
        }

        $equipoSelect.on('change', checkDisponibilidad);
        $fechaInicio.on('change', checkDisponibilidad);
        $fechaFin.on('change', checkDisponibilidad);
        $cantidad.on('input', checkDisponibilidad);
    }

    // ==================== ELIMINAR ARCHIVO (si se implementa) ====================
    $(document).on('click', '.btn-eliminar-archivo', function(e) {
        e.preventDefault();
        const url = $(this).attr('href');
        if (confirm('¿Eliminar este archivo permanentemente?')) {
            $.ajax({
                url: url,
                method: 'DELETE',
                success: function() {
                    mostrarAlerta('Archivo eliminado.', 'success');
                    if ($('#listaArchivos').length) {
                        $('#listaArchivos').load(window.location.href + ' #listaArchivos');
                    } else {
                        location.reload();
                    }
                },
                error: function() {
                    mostrarAlerta('Error al eliminar archivo.', 'danger');
                }
            });
        }
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
});