package pe.edu.utp.boticaapp.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pe.edu.utp.boticaapp.entity.Stock;
import java.util.List;

public interface StockRepository extends JpaRepository<Stock, Long> {
  @Query("SELECT s FROM Stock s WHERE s.producto.id = :productoId AND s.cantidad > 0 ORDER BY s.precio ASC")
  List<Stock> findTopByProductoId(@Param("productoId") Long productoId, Pageable pageable);
  @Query("SELECT s FROM Stock s WHERE s.productoSku = :sku AND s.cantidad > 0 ORDER BY s.precio ASC")
  List<Stock> findTopByProductoSku(@Param("sku") String sku, Pageable pageable);
}
