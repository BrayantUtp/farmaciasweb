package pe.edu.utp.boticaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.boticaapp.entity.Pago;
import pe.edu.utp.boticaapp.entity.Pedido;
import pe.edu.utp.boticaapp.entity.Usuario;
import java.util.List;
import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    
    Optional<Pago> findByPedido(Pedido pedido);
    
    List<Pago> findByPedido_UsuarioOrderByFechaPagoDesc(Usuario usuario);
    
    Optional<Pago> findByCodigoTransaccion(String codigoTransaccion);
}
