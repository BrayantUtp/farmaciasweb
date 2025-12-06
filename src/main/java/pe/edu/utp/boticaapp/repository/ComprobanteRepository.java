package pe.edu.utp.boticaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pe.edu.utp.boticaapp.entity.Comprobante;
import pe.edu.utp.boticaapp.entity.Pedido;
import java.util.Optional;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {
    
    // Buscar comprobante por pedido
    Optional<Comprobante> findByPedido(Pedido pedido);
    
    // Buscar por número de comprobante
    Optional<Comprobante> findByNumeroComprobante(String numeroComprobante);
    
    // Verificar si existe comprobante para un pedido
    boolean existsByPedido(Pedido pedido);
    
    // Obtener el último número de comprobante generado
    @Query("SELECT c.numeroComprobante FROM Comprobante c ORDER BY c.id DESC LIMIT 1")
    Optional<String> findUltimoNumeroComprobante();
}
