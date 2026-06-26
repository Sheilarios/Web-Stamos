package com.stamos.controller.publics;

import com.stamos.controller.BaseController;
import com.stamos.model.CategoriaProducto;
import com.stamos.model.Producto;
import com.stamos.service.CategoriaProductoService;
import com.stamos.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController extends BaseController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CategoriaProductoService categoriaProductoService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("empresa", "STAMOS LINARES S.R.L.");
        model.addAttribute("anio", Year.now().getValue());
        model.addAttribute("slogan", "Soluciones innovadoras en diseño publicitario, stands y producción para eventos");
        model.addAttribute("experiencia", "Más de 10 años de experiencia en el mercado");
        model.addAttribute("mision", "Brindar soluciones innovadoras en diseño publicitario, stands y producción para eventos, ofreciendo servicios de calidad que satisfagan las necesidades de los clientes mediante procesos eficientes y tecnología moderna.");
        model.addAttribute("vision", "Ser líderes del mercado a través de innovación, calidad, tiempo y costo para satisfacer con excelencia las necesidades de nuestros clientes y colaboradores.");
        model.addAttribute("productosDestacados", productoService.findTopActive(6));
        model.addAttribute("categorias", categoriaProductoService.findAllActive());
        model.addAttribute("titulo", "STAMOS S.L. - Soluciones en Stands y Mobiliario Ferial");
        model.addAttribute("descripcion", "Empresa líder en diseño, producción y alquiler de mobiliario ferial, stands publicitarios y soluciones visuales para eventos corporativos.");
        addCommonAttributes(model);
        return "public/index";
    }

    @GetMapping("/galeria")
    public String galeria(Model model) {
        model.addAttribute("proyectos", obtenerProyectosGaleria());
        model.addAttribute("categorias", categoriaProductoService.findAllActive());
        model.addAttribute("titulo", "Galería de Proyectos");
        model.addAttribute("descripcion", "Conoce algunos de nuestros proyectos más destacados realizados para clientes de diversos sectores.");
        model.addAttribute("empresa", "STAMOS LINARES S.R.L.");
        model.addAttribute("anio", Year.now().getValue());
        addCommonAttributes(model);
        return "public/galeria";
    }

    @GetMapping("/servicios")
    public String servicios(Model model) {
        model.addAttribute("productos", productoService.findActive());
        model.addAttribute("categorias", categoriaProductoService.findAllActive());
        model.addAttribute("servicios", obtenerServicios());
        model.addAttribute("titulo", "Servicios y Productos");
        model.addAttribute("descripcion", "Ofrecemos soluciones integrales para eventos corporativos y ferias.");
        model.addAttribute("empresa", "STAMOS LINARES S.R.L.");
        model.addAttribute("anio", Year.now().getValue());
        addCommonAttributes(model);
        return "public/servicios";
    }

    private List<ProyectoGaleria> obtenerProyectosGaleria() {
        List<ProyectoGaleria> proyectos = new ArrayList<>();
        
        proyectos.add(new ProyectoGaleria(
            "GSK", 
            "Stand farmacéutico 6×4 m con video wall curvo para presentaciones interactivas", 
            "Stands y estructuras feriales", 
            "gsk.jpg",
            "Proyecto realizado para GSK en feria de salud"
        ));
        
        proyectos.add(new ProyectoGaleria(
            "DINET", 
            "Stand tecnológico con pantallas táctiles y sistema de sonido envolvente", 
            "Equipamiento interactivo", 
            "dinet.jpg",
            "Stand interactivo para empresa de tecnología DINET"
        ));
        
        proyectos.add(new ProyectoGaleria(
            "CENFOTUR", 
            "Stand interactivo con juegos promocionales y realidad aumentada", 
            "Stands y estructuras feriales", 
            "cenfotur.jpg",
            "Proyecto para CENFOTUR en feria de turismo"
        ));
        
        proyectos.add(new ProyectoGaleria(
            "HUNTER", 
            "Mobiliario ferial y backings personalizados con diseño industrial", 
            "Mobiliario para eventos", 
            "hunter.jpg",
            "Mobiliario exclusivo para empresa HUNTER"
        ));
        
        proyectos.add(new ProyectoGaleria(
            "SOLDEXA", 
            "Stand modular con iluminación LED y estructura metálica", 
            "Stands y estructuras feriales", 
            "soldexa.jpg",
            "Stand para feria de construcción SOLDEXA"
        ));
        
        proyectos.add(new ProyectoGaleria(
            "ANDINO", 
            "Estructura de doble altura para feria de inversiones y negocios", 
            "Stands y estructuras feriales", 
            "andino.jpg",
            "Estructura para feria ANDINO de inversiones"
        ));
        
        return proyectos;
    }

    private List<ServicioItem> obtenerServicios() {
        List<ServicioItem> servicios = new ArrayList<>();
        
        servicios.add(new ServicioItem(
            "Diseño y producción de stands publicitarios", 
            "Diseño 3D, renders, planos y bocetos para stands publicitarios modulares, islas expositoras y mostradores. Incluye asesoría personalizada y seguimiento durante todo el proceso.",
            "ti ti-palette",
            "Ofrecemos soluciones de diseño a medida para stands que capturan la atención de tu público objetivo."
        ));
        
        servicios.add(new ServicioItem(
            "Alquiler de mobiliario ferial", 
            "Amplia variedad de mobiliario: sillas, mesas, taburetes, módulos expositores, barras, sofás, alfombras y decoración temática. Todo con la más alta calidad y mantenimiento.",
            "ti ti-chair",
            "Mobiliario de primera calidad para eventos corporativos, ferias y exposiciones."
        ));
        
        servicios.add(new ServicioItem(
            "Alquiler de equipamiento interactivo", 
            "Pantallas táctiles, video walls, kioscos digitales, proyectores, sistemas de sonido e iluminación LED. Tecnología de vanguardia para experiencias inmersivas.",
            "ti ti-device-tv",
            "Equipos interactivos que transforman tu stand en una experiencia memorable."
        ));
        
        servicios.add(new ServicioItem(
            "Publicidad gráfica", 
            "Lonas impresas, vinilos adhesivos, banners, roll-up, displays e impresión gran formato. Alta calidad de impresión y materiales duraderos.",
            "ti ti-printer",
            "Soluciones gráficas profesionales para comunicar tu marca de manera efectiva."
        ));
        
        servicios.add(new ServicioItem(
            "Logística integral", 
            "Transporte, montaje, desmontaje y soporte técnico durante el evento. Gestión completa para que te preocupes solo de tu negocio.",
            "ti ti-truck-delivery",
            "Logística profesional que garantiza la puntualidad y calidad en cada evento."
        ));
        
        return servicios;
    }

    public static class ProyectoGaleria {
        private String nombre;
        private String descripcion;
        private String categoria;
        private String imagen;
        private String descripcionLarga;

        public ProyectoGaleria(String nombre, String descripcion, String categoria, String imagen) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.categoria = categoria;
            this.imagen = imagen;
            this.descripcionLarga = descripcion;
        }

        public ProyectoGaleria(String nombre, String descripcion, String categoria, String imagen, String descripcionLarga) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.categoria = categoria;
            this.imagen = imagen;
            this.descripcionLarga = descripcionLarga;
        }

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getCategoria() { return categoria; }
        public String getImagen() { return imagen; }
        public String getDescripcionLarga() { return descripcionLarga; }
    }

    public static class ServicioItem {
        private String nombre;
        private String descripcion;
        private String icono;
        private String descripcionLarga;

        public ServicioItem(String nombre, String descripcion, String icono) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.icono = icono;
            this.descripcionLarga = descripcion;
        }

        public ServicioItem(String nombre, String descripcion, String icono, String descripcionLarga) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.icono = icono;
            this.descripcionLarga = descripcionLarga;
        }

        public String getNombre() { return nombre; }
        public String getDescripcion() { return descripcion; }
        public String getIcono() { return icono; }
        public String getDescripcionLarga() { return descripcionLarga; }
    }

    public int getAnioActual() {
        return Year.now().getValue();
    }

    public String getNombreEmpresa() {
        return "STAMOS LINARES S.R.L.";
    }

    public String getRuc() {
        return "20549500715";
    }

    public String getTelefonoContacto() {
        return "01 7046345";
    }

    public String getCelularContacto() {
        return "987819456";
    }

    public String getCelularSecundario() {
        return "987181168";
    }

    public String getEmailContacto() {
        return "info@stamos.com";
    }

    public String getDireccion() {
        return "Jr. Antisuyo 438, Urbanización Zárate, San Juan de Lurigancho, Lima, Perú";
    }

    public String getHorarioLunesViernes() {
        return "9:00 AM - 6:00 PM";
    }

    public String getHorarioSabado() {
        return "9:00 AM - 1:00 PM";
    }

    public String getHorarioCompleto() {
        return "Lunes a Viernes: 9:00 AM - 6:00 PM\nSábado: 9:00 AM - 1:00 PM";
    }

    public String getWhatsApp() {
        return "51987819456";
    }

    public int getAnioFundacion() {
        return 2014;
    }

    public int getTotalProyectos() {
        return 500;
    }

    public int getTotalClientes() {
        return 300;
    }

    public int getSatisfaccionCliente() {
        return 98;
    }

    public String formatearTelefono(String telefono) {
        if (telefono == null || telefono.isEmpty()) {
            return "";
        }
        if (telefono.matches("\\d{9}")) {
            return telefono.substring(0, 3) + " " + telefono.substring(3, 6) + " " + telefono.substring(6);
        }
        return telefono;
    }

    public RedesSociales getRedesSociales() {
        return new RedesSociales(
            "https://www.facebook.com/stamos",
            "https://www.instagram.com/stamos",
            "https://www.linkedin.com/company/stamos",
            "https://www.youtube.com/stamos"
        );
    }

    public static class RedesSociales {
        private final String facebook;
        private final String instagram;
        private final String linkedin;
        private final String youtube;

        public RedesSociales(String facebook, String instagram, String linkedin, String youtube) {
            this.facebook = facebook;
            this.instagram = instagram;
            this.linkedin = linkedin;
            this.youtube = youtube;
        }

        public String getFacebook() { return facebook; }
        public String getInstagram() { return instagram; }
        public String getLinkedin() { return linkedin; }
        public String getYoutube() { return youtube; }
    }

    public String getMapaUbicacion() {
        return "https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3902.123456789!2d-77.0312!3d-12.0453!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x9105c5f123456789%3A0x123456789abcdef!2sJr.%20Antisuyo%20438%2C%20San%20Juan%20de%20Lurigancho!5e0!3m2!1ses!2spe!4v1234567890";
    }

    public String getLatitud() {
        return "-12.0453";
    }

    public String getLongitud() {
        return "-77.0312";
    }

    public String getMensajeBienvenida() {
        return "Bienvenidos a STAMOS S.L., donde convertimos tus ideas en realidades visuales. " +
               "Somos especialistas en diseño, producción y alquiler de stands publicitarios, " +
               "mobiliario ferial y soluciones gráficas para eventos corporativos.";
    }

    public String getCallToAction() {
        return "Solicita tu cotización ahora";
    }
}