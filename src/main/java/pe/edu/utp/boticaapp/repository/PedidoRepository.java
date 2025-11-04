package pe.edu.utp.boticaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.boticaapp.entity.Pedido;
import pe.edu.utp.boticaapp.entity.Usuario;
import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {
  List<Pedido> findByUsuarioOrderByCreadoEnDesc(Usuario usuario);
}
