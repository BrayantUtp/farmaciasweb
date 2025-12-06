package pe.edu.utp.boticaapp.service;
import pe.edu.utp.boticaapp.entity.*;
import java.util.List;
public interface CartService {
  List<CarritoItem> listar(Usuario u);
  void agregar(Usuario u, Long productoId, Integer cantidad);
  void quitar(Usuario u, Long itemId);
  Long checkout(Usuario u);
}
