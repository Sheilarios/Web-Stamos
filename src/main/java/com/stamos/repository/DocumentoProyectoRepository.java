package com.stamos.repository;

import com.stamos.model.DocumentoProyecto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DocumentoProyectoRepository extends JpaRepository<DocumentoProyecto, Integer> {

    List<DocumentoProyecto> findByIdProyecto(Integer idProyecto);

    Page<DocumentoProyecto> findByIdProyecto(Integer idProyecto, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto = :idProyecto ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findByIdProyectoOrderByFechaSubidaDesc(@Param("idProyecto") Integer idProyecto);

    List<DocumentoProyecto> findByIdProyectoAndTipoDocumento(Integer idProyecto, String tipoDocumento);

    Page<DocumentoProyecto> findByIdProyectoAndTipoDocumento(Integer idProyecto, String tipoDocumento, Pageable pageable);

    long countByIdProyecto(Integer idProyecto);

    long countByIdProyectoAndTipoDocumento(Integer idProyecto, String tipoDocumento);

    List<DocumentoProyecto> findByTipoDocumento(String tipoDocumento);

    Page<DocumentoProyecto> findByTipoDocumento(String tipoDocumento, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = :tipo ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findByTipoDocumentoOrderByFechaSubidaDesc(@Param("tipo") String tipoDocumento);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Render' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findRenders();

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Render' ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findRenders(Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Plano' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findPlanos();

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Plano' ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findPlanos(Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Boceto' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findBocetos();

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Boceto' ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findBocetos(Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Contrato' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findContratos();

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = 'Contrato' ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findContratos(Pageable pageable);

    long countByTipoDocumento(String tipoDocumento);

    @Query("SELECT d.tipoDocumento, COUNT(d) as total FROM DocumentoProyecto d GROUP BY d.tipoDocumento ORDER BY d.tipoDocumento ASC")
    List<Object[]> countByTipoDocumentoResumen();

    List<DocumentoProyecto> findByIdUsuarioSubio(Integer idUsuarioSubio);

    Page<DocumentoProyecto> findByIdUsuarioSubio(Integer idUsuarioSubio, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idUsuarioSubio = :idUsuario ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findByIdUsuarioSubioOrderByFechaSubidaDesc(@Param("idUsuario") Integer idUsuarioSubio);

    long countByIdUsuarioSubio(Integer idUsuarioSubio);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.fechaSubida BETWEEN :fechaInicio AND :fechaFin ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findByFechaSubidaBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.fechaSubida BETWEEN :fechaInicio AND :fechaFin ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findByFechaSubidaBetween(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin,
                                                     Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE DATE(d.fechaSubida) = CURRENT_DATE ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findSubidosHoy();

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.fechaSubida >= :fecha ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findSubidosUltimosDias(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.fechaSubida >= :fecha ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findSubidosUltimosDias(@Param("fecha") LocalDateTime fecha, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tamanoBytes >= :tamano ORDER BY d.tamanoBytes DESC")
    List<DocumentoProyecto> findByTamanoBytesGreaterThanEqual(@Param("tamano") Long tamanoBytes);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tamanoBytes >= :tamano ORDER BY d.tamanoBytes DESC")
    Page<DocumentoProyecto> findByTamanoBytesGreaterThanEqual(@Param("tamano") Long tamanoBytes, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tamanoBytes BETWEEN :tamanoMin AND :tamanoMax ORDER BY d.tamanoBytes DESC")
    List<DocumentoProyecto> findByTamanoBytesBetween(@Param("tamanoMin") Long tamanoMin,
                                                     @Param("tamanoMax") Long tamanoMax);

    @Query("SELECT d FROM DocumentoProyecto d WHERE LOWER(d.nombreArchivo) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findByNombreArchivoContaining(@Param("nombre") String nombreArchivo);

    @Query("SELECT d FROM DocumentoProyecto d WHERE LOWER(d.nombreArchivo) LIKE LOWER(CONCAT('%', :nombre, '%')) ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findByNombreArchivoContaining(@Param("nombre") String nombreArchivo, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE LOWER(d.nombreArchivo) LIKE LOWER(CONCAT('%.', :extension)) ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findByExtension(@Param("extension") String extension);

    @Query("SELECT d FROM DocumentoProyecto d WHERE LOWER(d.nombreArchivo) LIKE LOWER(CONCAT('%.', :extension)) ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findByExtension(@Param("extension") String extension, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE " +
           "LOWER(d.nombreArchivo) LIKE '%.jpg' OR LOWER(d.nombreArchivo) LIKE '%.jpeg' OR " +
           "LOWER(d.nombreArchivo) LIKE '%.png' OR LOWER(d.nombreArchivo) LIKE '%.gif' OR " +
           "LOWER(d.nombreArchivo) LIKE '%.webp' OR LOWER(d.nombreArchivo) LIKE '%.svg' OR " +
           "LOWER(d.nombreArchivo) LIKE '%.bmp' OR LOWER(d.nombreArchivo) LIKE '%.ico' " +
           "ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findImages();

    @Query("SELECT d FROM DocumentoProyecto d WHERE LOWER(d.nombreArchivo) LIKE '%.pdf' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findPdfs();

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto = :idProyecto AND d.tipoDocumento = :tipo ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findByIdProyectoAndTipoDocumentoOrderByFechaSubidaDesc(@Param("idProyecto") Integer idProyecto,
                                                                                   @Param("tipo") String tipoDocumento);

    List<DocumentoProyecto> findByIdProyectoAndNombreArchivoContaining(Integer idProyecto, String nombreArchivo);

    List<DocumentoProyecto> findByIdProyectoAndIdUsuarioSubio(Integer idProyecto, Integer idUsuarioSubio);

    List<DocumentoProyecto> findByTipoDocumentoAndIdUsuarioSubio(String tipoDocumento, Integer idUsuarioSubio);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto IN (SELECT p.idProyecto FROM Proyecto p WHERE p.idCliente = :idCliente) ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findDocumentosByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto IN (SELECT p.idProyecto FROM Proyecto p WHERE p.idCliente = :idCliente) ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> findDocumentosByCliente(@Param("idCliente") Integer idCliente, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto IN (SELECT p.idProyecto FROM Proyecto p WHERE p.idCliente = :idCliente) AND d.tipoDocumento = :tipo ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findDocumentosByClienteAndTipo(@Param("idCliente") Integer idCliente,
                                                           @Param("tipo") String tipoDocumento);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto IN (SELECT p.idProyecto FROM Proyecto p WHERE p.idCliente = :idCliente) AND d.tipoDocumento = 'Render' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findRendersByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto IN (SELECT p.idProyecto FROM Proyecto p WHERE p.idCliente = :idCliente) AND d.tipoDocumento = 'Plano' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findPlanosByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto IN (SELECT p.idProyecto FROM Proyecto p WHERE p.idCliente = :idCliente) AND d.tipoDocumento = 'Boceto' ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> findBocetosByCliente(@Param("idCliente") Integer idCliente);

    @Query("SELECT d FROM DocumentoProyecto d WHERE " +
           "LOWER(d.nombreArchivo) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(d.tipoDocumento) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY d.fechaSubida DESC")
    List<DocumentoProyecto> search(@Param("searchText") String searchText);

    @Query("SELECT d FROM DocumentoProyecto d WHERE " +
           "LOWER(d.nombreArchivo) LIKE LOWER(CONCAT('%', :searchText, '%')) OR " +
           "LOWER(d.tipoDocumento) LIKE LOWER(CONCAT('%', :searchText, '%')) " +
           "ORDER BY d.fechaSubida DESC")
    Page<DocumentoProyecto> search(@Param("searchText") String searchText, Pageable pageable);

    @Query("SELECT d FROM DocumentoProyecto d ORDER BY d.fechaSubida DESC LIMIT :limit")
    List<DocumentoProyecto> findTopN(@Param("limit") int limit);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto = :idProyecto ORDER BY d.fechaSubida DESC LIMIT :limit")
    List<DocumentoProyecto> findTopNByProyecto(@Param("idProyecto") Integer idProyecto, @Param("limit") int limit);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.tipoDocumento = :tipo ORDER BY d.fechaSubida DESC LIMIT :limit")
    List<DocumentoProyecto> findTopNByTipo(@Param("tipo") String tipoDocumento, @Param("limit") int limit);

    @Query("SELECT d FROM DocumentoProyecto d WHERE d.idProyecto IN (SELECT p.idProyecto FROM Proyecto p WHERE p.idCliente = :idCliente) ORDER BY d.fechaSubida DESC LIMIT :limit")
    List<DocumentoProyecto> findTopNByCliente(@Param("idCliente") Integer idCliente, @Param("limit") int limit);

    @Query("SELECT COUNT(d) FROM DocumentoProyecto d WHERE MONTH(d.fechaSubida) = :month AND YEAR(d.fechaSubida) = :year")
    long countByMonthAndYear(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(d) FROM DocumentoProyecto d WHERE d.tipoDocumento = :tipo AND MONTH(d.fechaSubida) = :month AND YEAR(d.fechaSubida) = :year")
    long countByTipoAndMonthAndYear(@Param("tipo") String tipoDocumento,
                                    @Param("month") int month,
                                    @Param("year") int year);

    @Query("SELECT DATE(d.fechaSubida) as fecha, COUNT(d) as total " +
           "FROM DocumentoProyecto d " +
           "WHERE d.fechaSubida >= :fechaInicio " +
           "GROUP BY DATE(d.fechaSubida) " +
           "ORDER BY DATE(d.fechaSubida) DESC")
    List<Object[]> countSubidasByDay(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COALESCE(SUM(d.tamanoBytes), 0) FROM DocumentoProyecto d WHERE d.idProyecto = :idProyecto")
    Long sumTamanoByProyecto(@Param("idProyecto") Integer idProyecto);

    @Query("SELECT COALESCE(SUM(d.tamanoBytes), 0) FROM DocumentoProyecto d WHERE d.tipoDocumento = :tipo")
    Long sumTamanoByTipo(@Param("tipo") String tipoDocumento);

    @Query("SELECT COALESCE(AVG(d.tamanoBytes), 0) FROM DocumentoProyecto d WHERE d.idProyecto = :idProyecto")
    Double avgTamanoByProyecto(@Param("idProyecto") Integer idProyecto);

    boolean existsByIdProyecto(Integer idProyecto);

    boolean existsByIdProyectoAndNombreArchivo(Integer idProyecto, String nombreArchivo);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DocumentoProyecto d " +
           "WHERE d.idDocumento = :idDocumento AND (" +
           "LOWER(d.nombreArchivo) LIKE '%.jpg' OR LOWER(d.nombreArchivo) LIKE '%.jpeg' OR " +
           "LOWER(d.nombreArchivo) LIKE '%.png' OR LOWER(d.nombreArchivo) LIKE '%.gif' OR " +
           "LOWER(d.nombreArchivo) LIKE '%.webp' OR LOWER(d.nombreArchivo) LIKE '%.svg')")
    boolean isImage(@Param("idDocumento") Integer idDocumento);

    @Modifying
    @Transactional
    @Query("DELETE FROM DocumentoProyecto d WHERE d.idProyecto = :idProyecto")
    void deleteByIdProyecto(@Param("idProyecto") Integer idProyecto);

    @Modifying
    @Transactional
    @Query("DELETE FROM DocumentoProyecto d WHERE d.tipoDocumento = :tipo")
    void deleteByTipoDocumento(@Param("tipo") String tipoDocumento);

    @Modifying
    @Transactional
    @Query("DELETE FROM DocumentoProyecto d WHERE d.idUsuarioSubio = :idUsuario")
    void deleteByIdUsuarioSubio(@Param("idUsuario") Integer idUsuarioSubio);

    @Modifying
    @Transactional
    @Query("DELETE FROM DocumentoProyecto d WHERE d.fechaSubida < :fecha")
    int deleteOlderThan(@Param("fecha") LocalDateTime fecha);

    @Modifying
    @Transactional
    @Query("UPDATE DocumentoProyecto d SET d.urlArchivo = :nuevaUrl, d.fechaSubida = CURRENT_TIMESTAMP WHERE d.idDocumento = :idDocumento")
    void updateUrlArchivo(@Param("idDocumento") Integer idDocumento,
                          @Param("nuevaUrl") String nuevaUrl);

    @Modifying
    @Transactional
    @Query("UPDATE DocumentoProyecto d SET d.nombreArchivo = :nuevoNombre, d.fechaSubida = CURRENT_TIMESTAMP WHERE d.idDocumento = :idDocumento")
    void updateNombreArchivo(@Param("idDocumento") Integer idDocumento,
                             @Param("nuevoNombre") String nuevoNombre);

    @Modifying
    @Transactional
    @Query("UPDATE DocumentoProyecto d SET d.tipoDocumento = :nuevoTipo, d.fechaSubida = CURRENT_TIMESTAMP WHERE d.idDocumento = :idDocumento")
    void updateTipoDocumento(@Param("idDocumento") Integer idDocumento,
                             @Param("nuevoTipo") String nuevoTipo);
}