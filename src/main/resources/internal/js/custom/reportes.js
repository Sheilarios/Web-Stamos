/**
 * reportes.js
 * Funcionalidades para los reportes del sistema STAMOS
 * Dependencias: jQuery, DataTables, Bootstrap
 */

$(document).ready(function() {
    // ==================== INICIALIZAR DATATABLES EN TABLAS DE REPORTES ====================
    // Tabla de pagos pendientes
    if ($('#tablaPagosPendientes').length) {
        $('#tablaPagosPendientes').DataTable({
            language: { url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json' },
            order: [[0, 'asc']],
            pageLength: 10,
            responsive: true,
            dom: 'Bfrtip',
            buttons: [] // Los botones de exportación se manejarán aparte (por los enlaces del backend)
        });
    }

    // Tabla de proyectos atrasados
    if ($('#tablaProyectosAtrasados').length) {
        $('#tablaProyectosAtrasados').DataTable({
            language: { url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json' },
            order: [[3, 'asc']], // ordenar por fecha estimada
            pageLength: 10,
            responsive: true
        });
    }

    // Tabla de equipos más alquilados
    if ($('#tablaEquiposMasAlquilados').length) {
        $('#tablaEquiposMasAlquilados').DataTable({
            language: { url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json' },
            order: [[2, 'desc']], // ordenar por cantidad alquilada
            pageLength: 10,
            responsive: true
        });
    }

    // ==================== EXPORTACIÓN A EXCEL Y PDF ====================
    // Para cualquier botón con clases .export-excel o .export-pdf
    $('.btn-export-excel').on('click', function(e) {
        e.preventDefault();
        const url = $(this).data('url') || $(this).attr('href');
        if (url) {
            window.location.href = url;
        } else {
            mostrarAlerta('No se encontró la URL de exportación.', 'danger');
        }
    });

    $('.btn-export-pdf').on('click', function(e) {
        e.preventDefault();
        const url = $(this).data('url') || $(this).attr('href');
        if (url) {
            window.location.href = url;
        } else {
            mostrarAlerta('No se encontró la URL de exportación.', 'danger');
        }
    });

    // También se puede usar directamente el atributo href de los enlaces que ya están en la vista,
    // así que este código es solo para los botones generados dinámicamente.

    // ==================== FILTROS DE INGRESOS (MENSUAL/TRIMESTRAL) ====================
    const $formIngresos = $('#formIngresos');
    const $resultadoIngresos = $('#ingresosResultado');
    const $loadingIngresos = $('#loadingIngresos');

    if ($formIngresos.length && $resultadoIngresos.length) {
        // Cambiar la visibilidad de campos según el tipo de período
        const $tipoPeriodo = $('#tipoPeriodo');
        const $grupoMes = $('#grupoMes');
        const $grupoTrimestre = $('#grupoTrimestre');

        function togglePeriodoFields() {
            if ($tipoPeriodo.val() === 'MENSUAL') {
                $grupoMes.show();
                $grupoTrimestre.hide();
            } else if ($tipoPeriodo.val() === 'TRIMESTRAL') {
                $grupoMes.hide();
                $grupoTrimestre.show();
            } else {
                $grupoMes.hide();
                $grupoTrimestre.hide();
            }
        }

        $tipoPeriodo.on('change', togglePeriodoFields);
        togglePeriodoFields();

        // Envío del formulario mediante AJAX
        $formIngresos.on('submit', function(e) {
            e.preventDefault();
            const tipoPeriodo = $tipoPeriodo.val();
            const anio = $('#anio').val();
            let params = { tipoPeriodo: tipoPeriodo, anio: anio };
            if (tipoPeriodo === 'MENSUAL') {
                params.mes = $('#mes').val();
                if (!params.mes) {
                    mostrarAlerta('Debe seleccionar un mes.', 'danger');
                    return;
                }
            } else if (tipoPeriodo === 'TRIMESTRAL') {
                params.trimestre = $('#trimestre').val();
                if (!params.trimestre) {
                    mostrarAlerta('Debe seleccionar un trimestre.', 'danger');
                    return;
                }
            } else {
                mostrarAlerta('Seleccione un tipo de período válido.', 'danger');
                return;
            }

            $loadingIngresos.show();
            $resultadoIngresos.hide();

            $.ajax({
                url: '/internal/reportes/ingresos/generar',
                method: 'GET',
                data: params,
                success: function(html) {
                    $resultadoIngresos.html(html).show();
                    // Si el resultado contiene una tabla, volver a inicializar DataTable (opcional)
                    if ($resultadoIngresos.find('.dataTable').length) {
                        $resultadoIngresos.find('.dataTable').DataTable({
                            language: { url: '//cdn.datatables.net/plug-ins/1.13.6/i18n/es-ES.json' },
                            pageLength: 10,
                            responsive: true
                        });
                    }
                    // Actualizar las URLs de exportación con los mismos parámetros
                    $resultadoIngresos.find('.btn-export-excel, .btn-export-pdf').each(function() {
                        let baseUrl = $(this).data('url') || $(this).attr('href');
                        if (baseUrl && params) {
                            const queryString = $.param(params);
                            const separator = baseUrl.includes('?') ? '&' : '?';
                            const newUrl = baseUrl + separator + queryString;
                            $(this).attr('href', newUrl);
                        }
                    });
                },
                error: function() {
                    mostrarAlerta('Error al generar el reporte de ingresos.', 'danger');
                },
                complete: function() {
                    $loadingIngresos.hide();
                }
            });
        });
    }

    // ==================== REFRESCAR REPORTES (BOTÓN RECARGAR) ====================
    $('.btn-refrescar').on('click', function(e) {
        e.preventDefault();
        location.reload();
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

    // Exponer función de alerta para uso global (opcional)
    window.reportes = { mostrarAlerta: mostrarAlerta };
});