package pe.edu.utp.boticaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.boticaapp.entity.CarritoItem;
import pe.edu.utp.boticaapp.entity.Usuario;
import java.util.List;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {
  List<CarritoItem> findByUsuario(Usuario usuario);
  void deleteByUsuario(Usuario usuario);
}
