package pe.edu.utp.boticaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.boticaapp.entity.Stock;

import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
    
    /**
     * Busca todos los stocks de un producto (en todas las boticas)
     */
    List<Stock> findByProductoId(Long productoId);
    
    /**
     * Busca stocks de un producto ordenados por precio ascendente
     * (para implementar estrategia de precio más bajo primero)
     */
    List<Stock> findByProductoIdOrderByPrecioAsc(Long productoId);
    
    /**
     * Busca stocks de un producto con cantidad mayor a 0, ordenados por precio
     */
    @Query("SELECT s FROM Stock s WHERE s.producto.id = :productoId AND s.cantidad > 0 ORDER BY s.precio ASC")
    List<Stock> findStockDisponiblePorProducto(@Param("productoId") Long productoId);
    
    /**
     * Busca stock de un producto en una botica específica
     */
    Stock findByProductoIdAndBoticaId(Long productoId, Long boticaId);
    
    /**
     * Suma total de stock disponible de un producto
     */
    @Query("SELECT COALESCE(SUM(s.cantidad), 0) FROM Stock s WHERE s.producto.id = :productoId")
    Integer sumarStockTotal(@Param("productoId") Long productoId);
}