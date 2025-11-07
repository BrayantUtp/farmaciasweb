package pe.edu.utp.boticaapp.service;
import pe.edu.utp.boticaapp.entity.Producto;
import pe.edu.utp.boticaapp.entity.Stock;
import java.util.List;
public interface CatalogoService {
  List<Producto> listar(String q);
  byte[] exportarExcel();
  List<Stock> topStockPorProducto(Long productoId, int limit);
}
