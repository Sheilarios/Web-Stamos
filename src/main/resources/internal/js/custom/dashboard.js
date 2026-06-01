/**
 * dashboard.js
 * Funcionalidades para los paneles de control (dashboard) de STAMOS
 * Dependencias: jQuery, Chart.js, Bootstrap, Moment.js (opcional)
 */

$(document).ready(function() {
    // ==================== INICIALIZAR GRÁFICOS ====================
    let ingresosChart = null;
    let proyectosEstadoChart = null;

    // Cargar datos del dashboard
    cargarResumen();
    cargarIngresosMensuales();
    cargarProyectosPorEstado();
    cargarProyectosRecientes();
    cargarAlertasVencimiento();

    // ==================== FUNCIONES DE CARGA ====================
    function cargarResumen() {
        $.ajax({
            url: '/internal/dashboard/resumen',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                $('#totalClientes').text(data.totalClientes || 0);
                $('#totalProyectosActivos').text(data.totalProyectosActivos || 0);
                $('#totalPendientes').text('S/ ' + (data.totalPendientes || 0).toFixed(2));
                $('#totalAtrasados').text(data.totalAtrasados || 0);
            },
            error: function() {
                console.error('Error cargando resumen del dashboard');
                $('#totalClientes').text('--');
                $('#totalProyectosActivos').text('--');
                $('#totalPendientes').text('--');
                $('#totalAtrasados').text('--');
            }
        });
    }

    function cargarIngresosMensuales() {
        $.ajax({
            url: '/internal/dashboard/ingresos-mensuales',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                const ctx = document.getElementById('ingresosChart').getContext('2d');
                if (ingresosChart) ingresosChart.destroy();
                ingresosChart = new Chart(ctx, {
                    type: 'line',
                    data: {
                        labels: data.labels || ['Ene', 'Feb', 'Mar', 'Abr', 'May', 'Jun'],
                        datasets: [{
                            label: 'Ingresos (S/)',
                            data: data.values || [0, 0, 0, 0, 0, 0],
                            borderColor: '#1abc9c',
                            backgroundColor: 'rgba(26, 188, 156, 0.1)',
                            tension: 0.3,
                            fill: true
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            tooltip: {
                                callbacks: {
                                    label: function(context) {
                                        return 'S/ ' + context.raw.toFixed(2);
                                    }
                                }
                            }
                        }
                    }
                });
            },
            error: function() {
                console.error('Error cargando ingresos mensuales');
            }
        });
    }

    function cargarProyectosPorEstado() {
        $.ajax({
            url: '/internal/dashboard/proyectos-por-estado',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                const ctx = document.getElementById('proyectosEstadoChart').getContext('2d');
                if (proyectosEstadoChart) proyectosEstadoChart.destroy();
                proyectosEstadoChart = new Chart(ctx, {
                    type: 'doughnut',
                    data: {
                        labels: data.labels || ['Cotizado', 'Aprobado', 'En diseño', 'En producción', 'Instalación', 'Entregado', 'Anulado'],
                        datasets: [{
                            data: data.values || [0, 0, 0, 0, 0, 0, 0],
                            backgroundColor: ['#95a5a6', '#3498db', '#f39c12', '#e67e22', '#9b59b6', '#27ae60', '#e74c3c']
                        }]
                    },
                    options: {
                        responsive: true,
                        maintainAspectRatio: false,
                        plugins: {
                            legend: { position: 'bottom' }
                        }
                    }
                });
            },
            error: function() {
                console.error('Error cargando proyectos por estado');
            }
        });
    }

    function cargarProyectosRecientes() {
        $.ajax({
            url: '/internal/dashboard/proyectos-recientes',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                const tbody = $('#proyectosRecientes tbody');
                tbody.empty();
                if (data.length === 0) {
                    tbody.html('<tr><td colspan="4" class="text-center">No hay proyectos recientes</td></tr>');
                } else {
                    $.each(data, function(i, proyecto) {
                        const row = `
                            <tr>
                                <td>${proyecto.id}</td>
                                <td>${escapeHtml(proyecto.clienteNombre)}</td>
                                <td><span class="badge estado-${proyecto.estado.toLowerCase().replace('_', '-')}">${proyecto.estado}</span></td>
                                <td>${proyecto.fechaCreacion}</td>
                            </tr>
                        `;
                        tbody.append(row);
                    });
                }
            },
            error: function() {
                console.error('Error cargando proyectos recientes');
                $('#proyectosRecientes tbody').html('<tr><td colspan="4" class="text-center">Error al cargar datos</td></tr>');
            }
        });
    }

    function cargarAlertasVencimiento() {
        $.ajax({
            url: '/internal/dashboard/alertas-vencimiento',
            method: 'GET',
            dataType: 'json',
            success: function(data) {
                const $alertContainer = $('#alertasContainer');
                $alertContainer.empty();
                if (data.length === 0) {
                    $alertContainer.html('<div class="alert alert-success">No hay proyectos con pagos vencidos.</div>');
                } else {
                    $.each(data, function(i, alerta) {
                        const alertHtml = `
                            <div class="alert alert-warning alert-dismissible fade show" role="alert">
                                <strong>Proyecto #${alerta.proyectoId}</strong> - Cliente: ${escapeHtml(alerta.clienteNombre)}<br>
                                Saldo pendiente: S/ ${alerta.saldoPendiente.toFixed(2)}<br>
                                Fecha entrega estimada: ${alerta.fechaEntrega}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                            </div>
                        `;
                        $alertContainer.append(alertHtml);
                    });
                }
            },
            error: function() {
                console.error('Error cargando alertas de vencimiento');
                $('#alertasContainer').html('<div class="alert alert-danger">Error al cargar alertas</div>');
            }
        });
    }

    // ==================== FUNCIONES AUXILIARES ====================
    function escapeHtml(text) {
        if (!text) return '';
        return text
            .replace(/&/g, '&amp;')
            .replace(/</g, '&lt;')
            .replace(/>/g, '&gt;')
            .replace(/"/g, '&quot;')
            .replace(/'/g, '&#39;');
    }

    // Recargar datos cada 5 minutos (opcional)
    setInterval(function() {
        cargarResumen();
        cargarIngresosMensuales();
        cargarProyectosRecientes();
        cargarAlertasVencimiento();
    }, 300000);
});