package com.stamos.service;

import com.stamos.model.*;
import com.stamos.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para la gestión de cotizaciones.
 * 
 * <p>Este servicio proporciona métodos para gestionar las cotizaciones de
 * STAMOS S.L., incluyendo creación, versionado, aprobación y anulación.</p>
 * 
 * <p>Implementa los requerimientos:</p>
 * <ul>
 *   <li>RN-04: Toda cotización debe incluir al menos un producto o servicio</li>
 *   <li>RN-05: Solo personal autorizado puede emitir, modificar o anular cotizaciones</li>
 *   <li>RN-06: Cotización aprobada no puede modificarse, se crea nueva versión</li>
 *   <li>RN-20: Una cotización aprobada genera exactamente un proyecto</li>
 *   <li>RN-22: Cotizaciones ya emitidas mantienen su tasa IGV</li>
 *   <li>RF-05: Crear cotizaciones con al menos un ítem</li>
 *   <li>RF-06: Calcular automáticamente subtotal, IGV y total</li>
 *   <li>RF-07: Permitir registro de precios base o negociados</li>
 *   <li>RF-08: Registrar versiones de cotizaciones modificadas</li>
 *   <li>RF-09: Bloquear modificación de cotizaciones aprobadas</li>
 *   <li>RF-10: Generar proyecto automáticamente al aprobar cotización</li>
 *   <li>RF-11: Gestionar catálogo de productos y servicios</li>
 * </ul>
 * 
 * @author STAMOS S.L.
 * @version 1.0
 */
@Service
public class CotizacionService {

    @Autowired
    private CotizacionRepository cotizacionRepository;

    @Autowired
    private CotizacionVersionRepository cotizacionVersionRepository;

    @Autowired
    private CotizacionDetalleRepository cotizacionDetalleRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private EstadoProyectoRepository estadoProyectoRepository;

    @Autowired
    private ConfiguracionSistemaService configuracionService;

    // ============================================
    // CREACIÓN DE COTIZACIONES
    // ============================================

    /**
     * Crea una nueva cotización.
     * 
     * @param cotizacion Datos de la cotización
     * @param productosIds IDs de los productos
     * @param cantidades Cantidades de cada producto
     * @param preciosUnitarios Precios unitarios de cada producto
     * @param descuentos Descuentos de cada producto
     * @param idUsuarioCreador ID del usuario que crea
     * @return Cotización creada
     * @throws RuntimeException si la cotización no tiene ítems o los datos son inválidos
     */
    @Transactional
    public Cotizacion crearCotizacion(Cotizacion cotizacion, List<Integer> productosIds,
                                      List<Integer> cantidades, List<BigDecimal> preciosUnitarios,
                                      List<BigDecimal> descuentos, Integer idUsuarioCreador) {
        // Validar que la cotización tenga al menos un ítem (RN-04, RF-05)
        if (productosIds == null || productosIds.isEmpty()) {
            throw new RuntimeException("La cotización debe incluir al menos un producto o servicio");
        }

        // Validar que las listas tengan el mismo tamaño
        if (productosIds.size() != cantidades.size() ||
            productosIds.size() != preciosUnitarios.size()) {
            throw new RuntimeException("Error en los datos de los productos");
        }

        // Validar que el cliente existe
        Cliente cliente = clienteRepository.findById(cotizacion.getIdCliente())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Validar que el vendedor existe
        Usuario vendedor = usuarioRepository.findById(cotizacion.getIdUsuarioVendedor())
                .orElseThrow(() -> new RuntimeException("Vendedor no encontrado"));

        // Obtener tasa IGV actual (RN-22, RNF-07)
        BigDecimal tasaIgv = configuracionService.getIgv();

        // Crear la cotización
        cotizacion.setEstado("Pendiente");
        cotizacion.setVersionActual(1);
        cotizacion.setFechaEmision(LocalDateTime.now());
        cotizacion.setFechaActualizacion(LocalDateTime.now());
        cotizacion.setTasaIgv(tasaIgv);

        // Guardar cotización
        Cotizacion cotizacionGuardada = cotizacionRepository.save(cotizacion);

        // Crear la primera versión
        CotizacionVersion version = crearVersion(cotizacionGuardada, 1, idUsuarioCreador, null);

        // Crear los detalles de la versión
        List<CotizacionDetalle> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < productosIds.size(); i++) {
            Integer idProducto = productosIds.get(i);
            Integer cantidad = cantidades.get(i);
            BigDecimal precioUnitario = preciosUnitarios.get(i);
            BigDecimal descuento = descuentos != null && i < descuentos.size() ? 
                                   descuentos.get(i) : BigDecimal.ZERO;

            // Validar que el producto existe
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

            // Crear detalle
            CotizacionDetalle detalle = new CotizacionDetalle();
            detalle.setIdVersion(version.getIdVersion());
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setDescuento(descuento);
            detalle.calcularSubtotal();

            // Enriquecer con datos del producto
            detalle.setNombreProducto(producto.getNombreItem());
            detalle.setDescripcionProducto(producto.getDescripcion());
            detalle.setTipoProducto(producto.getTipo());
            detalle.setImagenUrl(producto.getImagenUrl());
            detalle.setPrecioBase(producto.getPrecioBase());

            // Guardar detalle
            CotizacionDetalle detalleGuardado = cotizacionDetalleRepository.save(detalle);
            detalles.add(detalleGuardado);

            // Acumular subtotal
            subtotal = subtotal.add(detalle.getSubtotalLinea());
        }

        // Calcular IGV y total
        BigDecimal igv = subtotal.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        // Actualizar la versión con los totales
        version.setSubtotal(subtotal);
        version.setIgv(igv);
        version.setTotal(total);
        version.setCantidadItems(detalles.size());
        version.setDetalles(detalles);
        cotizacionVersionRepository.save(version);

        // Actualizar la cotización con los totales
        cotizacionGuardada.setSubtotal(subtotal);
        cotizacionGuardada.setIgv(igv);
        cotizacionGuardada.setTotal(total);
        cotizacionGuardada.setCantidadItems(detalles.size());
        cotizacionGuardada.setDetalles(detalles);

        return cotizacionRepository.save(cotizacionGuardada);
    }

    // ============================================
    // VERSIONADO DE COTIZACIONES
    // ============================================

    /**
     * Crea una nueva versión de una cotización (RF-08).
     * 
     * @param idCotizacion ID de la cotización
     * @param cotizacion Datos actualizados de la cotización
     * @param productosIds IDs de los productos
     * @param cantidades Cantidades de cada producto
     * @param preciosUnitarios Precios unitarios de cada producto
     * @param descuentos Descuentos de cada producto
     * @param motivoCambio Motivo del cambio
     * @param idUsuarioCreador ID del usuario que crea
     * @return Nueva versión creada
     * @throws RuntimeException si la cotización está aprobada (RF-09)
     */
    @Transactional
    public CotizacionVersion crearVersion(Integer idCotizacion, Cotizacion cotizacion,
                                          List<Integer> productosIds, List<Integer> cantidades,
                                          List<BigDecimal> preciosUnitarios, List<BigDecimal> descuentos,
                                          String motivoCambio, Integer idUsuarioCreador) {
        // Verificar que la cotización existe
        Cotizacion cotizacionExistente = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        // Verificar que la cotización no esté aprobada (RF-09)
        if ("Aprobada".equals(cotizacionExistente.getEstado())) {
            throw new RuntimeException("No se puede modificar una cotización aprobada. " +
                                      "Cree una nueva versión desde el historial.");
        }

        // Validar que tenga ítems (RN-04)
        if (productosIds == null || productosIds.isEmpty()) {
            throw new RuntimeException("La cotización debe incluir al menos un producto o servicio");
        }

        // Obtener la tasa IGV de la cotización existente (RN-22)
        BigDecimal tasaIgv = cotizacionExistente.getTasaIgv() != null ? 
                            cotizacionExistente.getTasaIgv() : configuracionService.getIgv();

        // Incrementar número de versión
        int nuevoNumeroVersion = cotizacionExistente.getVersionActual() + 1;

        // Crear la nueva versión
        CotizacionVersion version = new CotizacionVersion();
        version.setIdCotizacion(idCotizacion);
        version.setNumeroVersion(nuevoNumeroVersion);
        version.setTasaIgv(tasaIgv);
        version.setIdUsuarioCreador(idUsuarioCreador);
        version.setMotivoCambio(motivoCambio);
        version.setFechaCreacion(LocalDateTime.now());

        // Guardar versión
        CotizacionVersion versionGuardada = cotizacionVersionRepository.save(version);

        // Crear los detalles
        List<CotizacionDetalle> detalles = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        for (int i = 0; i < productosIds.size(); i++) {
            Integer idProducto = productosIds.get(i);
            Integer cantidad = cantidades.get(i);
            BigDecimal precioUnitario = preciosUnitarios.get(i);
            BigDecimal descuento = descuentos != null && i < descuentos.size() ? 
                                   descuentos.get(i) : BigDecimal.ZERO;

            // Validar que el producto existe
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + idProducto));

            // Crear detalle
            CotizacionDetalle detalle = new CotizacionDetalle();
            detalle.setIdVersion(versionGuardada.getIdVersion());
            detalle.setIdProducto(idProducto);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setDescuento(descuento);
            detalle.calcularSubtotal();

            // Enriquecer con datos del producto
            detalle.setNombreProducto(producto.getNombreItem());
            detalle.setDescripcionProducto(producto.getDescripcion());
            detalle.setTipoProducto(producto.getTipo());
            detalle.setImagenUrl(producto.getImagenUrl());
            detalle.setPrecioBase(producto.getPrecioBase());

            // Guardar detalle
            CotizacionDetalle detalleGuardado = cotizacionDetalleRepository.save(detalle);
            detalles.add(detalleGuardado);

            // Acumular subtotal
            subtotal = subtotal.add(detalle.getSubtotalLinea());
        }

        // Calcular IGV y total
        BigDecimal igv = subtotal.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(igv);

        // Actualizar la versión con los totales
        versionGuardada.setSubtotal(subtotal);
        versionGuardada.setIgv(igv);
        versionGuardada.setTotal(total);
        versionGuardada.setCantidadItems(detalles.size());
        versionGuardada.setDetalles(detalles);
        cotizacionVersionRepository.save(versionGuardada);

        // Actualizar la cotización con la nueva versión
        cotizacionExistente.setVersionActual(nuevoNumeroVersion);
        cotizacionExistente.setFechaActualizacion(LocalDateTime.now());
        cotizacionExistente.setSubtotal(subtotal);
        cotizacionExistente.setIgv(igv);
        cotizacionExistente.setTotal(total);
        cotizacionExistente.setCantidadItems(detalles.size());
        cotizacionExistente.setDetalles(detalles);
        cotizacionRepository.save(cotizacionExistente);

        return versionGuardada;
    }

    /**
     * Crea una versión de una cotización.
     * 
     * @param cotizacion Cotización
     * @param numeroVersion Número de versión
     * @param idUsuarioCreador ID del usuario que crea
     * @param motivoCambio Motivo del cambio
     * @return Versión creada
     */
    private CotizacionVersion crearVersion(Cotizacion cotizacion, Integer numeroVersion,
                                          Integer idUsuarioCreador, String motivoCambio) {
        CotizacionVersion version = new CotizacionVersion();
        version.setIdCotizacion(cotizacion.getIdCotizacion());
        version.setNumeroVersion(numeroVersion);
        version.setTasaIgv(cotizacion.getTasaIgv());
        version.setIdUsuarioCreador(idUsuarioCreador);
        version.setMotivoCambio(motivoCambio);
        version.setFechaCreacion(LocalDateTime.now());
        return cotizacionVersionRepository.save(version);
    }

    // ============================================
    // APROBACIÓN DE COTIZACIONES
    // ============================================

    /**
     * Aprueba una cotización y genera automáticamente un proyecto (RF-10, RN-20).
     * 
     * @param idCotizacion ID de la cotización
     * @param idUsuarioAprobacion ID del usuario que aprueba
     * @return Proyecto generado
     * @throws RuntimeException si la cotización no existe o ya está aprobada
     */
    @Transactional
    public Proyecto aprobarCotizacion(Integer idCotizacion, Integer idUsuarioAprobacion) {
        // Verificar que la cotización existe
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        // Verificar que esté pendiente
        if (!"Pendiente".equals(cotizacion.getEstado())) {
            throw new RuntimeException("La cotización ya está " + cotizacion.getEstado().toLowerCase());
        }

        // Verificar que tenga detalles
        if (!cotizacion.tieneDetalles()) {
            throw new RuntimeException("La cotización no tiene detalles");
        }

        // Verificar que no tenga un proyecto asociado (RN-20)
        if (proyectoRepository.existsByIdCotizacion(idCotizacion)) {
            throw new RuntimeException("La cotización ya tiene un proyecto asociado");
        }

        // Aprobar la cotización
        cotizacion.aprobar(idUsuarioAprobacion);
        cotizacionRepository.save(cotizacion);

        // Obtener el estado "Aprobado"
        EstadoProyecto estadoAprobado = estadoProyectoRepository.findByNombreEstado("Aprobado")
                .orElseThrow(() -> new RuntimeException("Estado 'Aprobado' no encontrado"));

        // Crear el proyecto (RF-10, RN-20)
        Proyecto proyecto = new Proyecto();
        proyecto.setIdCotizacion(idCotizacion);
        proyecto.setIdCliente(cotizacion.getIdCliente());
        proyecto.setNombreProyecto("Proyecto - " + cotizacion.getCodigoCotizacion());
        proyecto.setIdEstadoActual(estadoAprobado.getIdEstado());
        proyecto.setMontoTotal(cotizacion.getTotal());
        proyecto.setFechaInicio(LocalDate.now());
        proyecto.setFechaCreacion(LocalDateTime.now());
        proyecto.setIdUsuarioCreacion(idUsuarioAprobacion);
        proyecto.setIdUsuarioActualizacion(idUsuarioAprobacion);

        // Calcular fecha de vencimiento (30 días después de inicio)
        proyecto.setFechaVencimiento(LocalDate.now().plusDays(30));

        // Guardar proyecto
        return proyectoRepository.save(proyecto);
    }

    // ============================================
    // ANULACIÓN DE COTIZACIONES
    // ============================================

    /**
     * Anula una cotización.
     * 
     * @param idCotizacion ID de la cotización
     * @param motivo Motivo de la anulación
     * @param idUsuarioAnulacion ID del usuario que anula
     * @throws RuntimeException si la cotización no existe o ya está aprobada
     */
    @Transactional
    public void anularCotizacion(Integer idCotizacion, String motivo, Integer idUsuarioAnulacion) {
        Cotizacion cotizacion = cotizacionRepository.findById(idCotizacion)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada"));

        // Verificar que no esté aprobada
        if ("Aprobada".equals(cotizacion.getEstado())) {
            throw new RuntimeException("No se puede anular una cotización aprobada");
        }

        // Verificar que no esté ya anulada
        if ("Anulada".equals(cotizacion.getEstado())) {
            throw new RuntimeException("La cotización ya está anulada");
        }

        cotizacion.anular(idUsuarioAnulacion);
        cotizacion.setObservaciones((cotizacion.getObservaciones() != null ? 
                                   cotizacion.getObservaciones() + "\n" : "") + 
                                   "Motivo de anulación: " + (motivo != null ? motivo : "Sin motivo"));
        cotizacionRepository.save(cotizacion);
    }

    // ============================================
    // CONSULTAS DE COTIZACIONES
    // ============================================

    /**
     * Busca una cotización por ID.
     * 
     * @param idCotizacion ID de la cotización
     * @return Cotización encontrada, o null si no existe
     */
    public Cotizacion findById(Integer idCotizacion) {
        return cotizacionRepository.findById(idCotizacion).orElse(null);
    }

    /**
     * Obtiene todas las cotizaciones con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cotizaciones
     */
    public Page<Cotizacion> findAll(Pageable pageable) {
        return cotizacionRepository.findAll(pageable);
    }

    /**
     * Obtiene cotizaciones por estado.
     * 
     * @param estado Estado de la cotización
     * @param pageable Configuración de paginación
     * @return Página de cotizaciones
     */
    public Page<Cotizacion> findByEstado(String estado, Pageable pageable) {
        return cotizacionRepository.findByEstado(estado, pageable);
    }

    /**
     * Obtiene cotizaciones por cliente.
     * 
     * @param idCliente ID del cliente
     * @param pageable Configuración de paginación
     * @return Página de cotizaciones
     */
    public Page<Cotizacion> findByCliente(Integer idCliente, Pageable pageable) {
        return cotizacionRepository.findByIdCliente(idCliente, pageable);
    }

    /**
     * Obtiene cotizaciones pendientes.
     * 
     * @return Lista de cotizaciones pendientes
     */
    public List<Cotizacion> findPendientes() {
        return cotizacionRepository.findPendientes();
    }

    /**
     * Obtiene cotizaciones pendientes con paginación.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cotizaciones pendientes
     */
    public Page<Cotizacion> findPendientes(Pageable pageable) {
        return cotizacionRepository.findPendientes(pageable);
    }

    /**
     * Obtiene cotizaciones aprobadas.
     * 
     * @param pageable Configuración de paginación
     * @return Página de cotizaciones aprobadas
     */
    public Page<Cotizacion> findAprobadas(Pageable pageable) {
        return cotizacionRepository.findAprobadas(pageable);
    }

    /**
     * Obtiene cotizaciones vencidas (fecha de validez vencida y estado pendiente).
     * 
     * @return Lista de cotizaciones vencidas
     */
    public List<Cotizacion> findVencidas() {
        return cotizacionRepository.findVencidas(LocalDate.now());
    }

    /**
     * Busca cotizaciones por texto.
     * 
     * @param searchText Texto a buscar
     * @param pageable Configuración de paginación
     * @return Página de cotizaciones
     */
    public Page<Cotizacion> search(String searchText, Pageable pageable) {
        return cotizacionRepository.search(searchText, pageable);
    }

    /**
     * Obtiene las últimas N cotizaciones.
     * 
     * @param limit Número máximo de resultados
     * @return Lista de cotizaciones
     */
    public List<Cotizacion> findTopN(int limit) {
        return cotizacionRepository.findTopN(limit);
    }

    /**
     * Obtiene las últimas N cotizaciones pendientes.
     * 
     * @param limit Número máximo de resultados
     * @return Lista de cotizaciones pendientes
     */
    public List<Cotizacion> findTopNPendientes(int limit) {
        return cotizacionRepository.findTopNPendientes(limit);
    }

    // ============================================
    // CONSULTAS DE VERSIONES
    // ============================================

    /**
     * Obtiene todas las versiones de una cotización.
     * 
     * @param idCotizacion ID de la cotización
     * @return Lista de versiones
     */
    public List<CotizacionVersion> getVersionesByCotizacion(Integer idCotizacion) {
        List<CotizacionVersion> versiones = cotizacionVersionRepository.findByIdCotizacionOrderByNumeroVersionDesc(idCotizacion);
        
        // Marcar la versión actual
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion != null) {
            for (CotizacionVersion version : versiones) {
                if (version.getNumeroVersion().equals(cotizacion.getVersionActual())) {
                    version.setEsVersionActual(true);
                }
            }
        }
        
        return versiones;
    }

    /**
     * Busca una versión por ID.
     * 
     * @param idVersion ID de la versión
     * @return Versión encontrada, o null si no existe
     */
    public CotizacionVersion findVersionById(Integer idVersion) {
        return cotizacionVersionRepository.findById(idVersion).orElse(null);
    }

    /**
     * Obtiene los detalles de una cotización.
     * 
     * @param idCotizacion ID de la cotización
     * @return Lista de detalles
     */
    public List<CotizacionDetalle> getDetallesByCotizacion(Integer idCotizacion) {
        return cotizacionDetalleRepository.findDetallesByCotizacion(idCotizacion);
    }

    /**
     * Obtiene los detalles de una versión.
     * 
     * @param idVersion ID de la versión
     * @return Lista de detalles
     */
    public List<CotizacionDetalle> getDetallesByVersion(Integer idVersion) {
        return cotizacionDetalleRepository.findByIdVersion(idVersion);
    }

    // ============================================
    // MÉTRICAS Y ESTADÍSTICAS
    // ============================================

    /**
     * Cuenta el número total de cotizaciones.
     * 
     * @return Número de cotizaciones
     */
    public long count() {
        return cotizacionRepository.count();
    }

    /**
     * Cuenta cotizaciones por estado.
     * 
     * @param estado Estado de la cotización
     * @return Número de cotizaciones
     */
    public long countByEstado(String estado) {
        return cotizacionRepository.countByEstado(estado);
    }

    /**
     * Cuenta cotizaciones por cliente.
     * 
     * @param idCliente ID del cliente
     * @return Número de cotizaciones
     */
    public long countByCliente(Integer idCliente) {
        return cotizacionRepository.countByIdCliente(idCliente);
    }

    /**
     * Obtiene el monto total de cotizaciones aprobadas.
     * 
     * @return Suma de totales
     */
    public BigDecimal sumTotalAprobadas() {
        return cotizacionRepository.sumTotalAprobadas();
    }

    /**
     * Obtiene el monto total de cotizaciones por cliente.
     * 
     * @param idCliente ID del cliente
     * @return Suma de totales
     */
    public BigDecimal sumTotalByCliente(Integer idCliente) {
        return cotizacionRepository.sumTotalByCliente(idCliente);
    }

    /**
     * Obtiene el resumen de cotizaciones por estado.
     * 
     * @return Lista de objetos con estado y conteo
     */
    public List<Object[]> countByEstadoResumen() {
        return cotizacionRepository.countByEstadoResumen();
    }

    // ============================================
    // VERIFICACIONES
    // ============================================

    /**
     * Verifica si una cotización tiene proyecto asociado.
     * 
     * @param idCotizacion ID de la cotización
     * @return true si tiene proyecto, false en caso contrario
     */
    public boolean hasProyecto(Integer idCotizacion) {
        return proyectoRepository.existsByIdCotizacion(idCotizacion);
    }

    /**
     * Verifica si una cotización puede ser editada.
     * 
     * @param idCotizacion ID de la cotización
     * @return true si puede ser editada, false en caso contrario
     */
    public boolean canEdit(Integer idCotizacion) {
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion == null) {
            return false;
        }
        return cotizacion.puedeEditar();
    }

    /**
     * Verifica si una cotización puede ser aprobada.
     * 
     * @param idCotizacion ID de la cotización
     * @return true si puede ser aprobada, false en caso contrario
     */
    public boolean canApprove(Integer idCotizacion) {
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion == null) {
            return false;
        }
        return cotizacion.puedeAprobar() && !hasProyecto(idCotizacion);
    }

    /**
     * Verifica si una cotización puede ser anulada.
     * 
     * @param idCotizacion ID de la cotización
     * @return true si puede ser anulada, false en caso contrario
     */
    public boolean canAnular(Integer idCotizacion) {
        Cotizacion cotizacion = findById(idCotizacion);
        if (cotizacion == null) {
            return false;
        }
        return cotizacion.puedeAnular();
    }

    // ============================================
    // MÉTODOS DE UTILIDAD
    // ============================================

    /**
     * Calcula el subtotal de una cotización.
     * 
     * @param detalles Lista de detalles
     * @return Subtotal
     */
    public BigDecimal calcularSubtotal(List<CotizacionDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return detalles.stream()
                .map(CotizacionDetalle::getSubtotalLinea)
                .filter(s -> s != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calcula el IGV de una cotización.
     * 
     * @param subtotal Subtotal
     * @param tasaIgv Tasa IGV
     * @return IGV
     */
    public BigDecimal calcularIgv(BigDecimal subtotal, BigDecimal tasaIgv) {
        if (subtotal == null || tasaIgv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.multiply(tasaIgv)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el total de una cotización.
     * 
     * @param subtotal Subtotal
     * @param igv IGV
     * @return Total
     */
    public BigDecimal calcularTotal(BigDecimal subtotal, BigDecimal igv) {
        if (subtotal == null || igv == null) {
            return BigDecimal.ZERO;
        }
        return subtotal.add(igv);
    }

    /**
     * Obtiene el código de cotización formateado.
     * 
     * @param idCotizacion ID de la cotización
     * @return Código formateado
     */
    public String getCodigoCotizacion(Integer idCotizacion) {
        if (idCotizacion == null) {
            return "COT-00000";
        }
        return "COT-" + String.format("%05d", idCotizacion);
    }

    /**
     * Obtiene el estado de una cotización en formato legible.
     * 
     * @param estado Estado de la cotización
     * @return Estado legible
     */
    public String getEstadoLegible(String estado) {
        if (estado == null) {
            return "Sin estado";
        }
        switch (estado) {
            case "Pendiente":
                return "Pendiente";
            case "Aprobada":
                return "Aprobada";
            case "Anulada":
                return "Anulada";
            default:
                return estado;
        }
    }

    /**
     * Obtiene la clase CSS para el badge del estado.
     * 
     * @param estado Estado de la cotización
     * @return Clase CSS
     */
    public String getEstadoBadgeClass(String estado) {
        if (estado == null) {
            return "status-pendiente";
        }
        switch (estado) {
            case "Pendiente":
                return "status-pendiente";
            case "Aprobada":
                return "status-validado";
            case "Anulada":
                return "status-rechazado";
            default:
                return "status-pendiente";
        }
    }

    /**
     * Obtiene el color del estado.
     * 
     * @param estado Estado de la cotización
     * @return Color en hexadecimal
     */
    public String getEstadoColor(String estado) {
        if (estado == null) {
            return "#6B7280";
        }
        switch (estado) {
            case "Pendiente":
                return "#F59E0B";
            case "Aprobada":
                return "#10B981";
            case "Anulada":
                return "#EF4444";
            default:
                return "#6B7280";
        }
    }

    public List<Cotizacion> findTop10ByEstadoOrderByFechaEmisionDesc(String pendiente) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    public List<Cotizacion> findPendientesAntiguas(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}