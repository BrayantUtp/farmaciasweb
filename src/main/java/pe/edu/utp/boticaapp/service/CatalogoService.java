package pe.edu.utp.boticaapp.service;
import pe.edu.utp.boticaapp.entity.Producto;
import java.util.List;
public interface CatalogoService {
  List<Producto> listar(String q);
  byte[] exportarExcel();
}
