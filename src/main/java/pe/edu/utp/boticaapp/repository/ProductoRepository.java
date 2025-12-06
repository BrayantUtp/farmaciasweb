package pe.edu.utp.boticaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.boticaapp.entity.Producto;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
  List<Producto> findByNombreContainingIgnoreCase(String q);
}
