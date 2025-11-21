package pe.edu.utp.boticaapp.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.boticaapp.repository.PedidoRepository;
import pe.edu.utp.boticaapp.entity.*;
import pe.edu.utp.boticaapp.service.PedidoService;
import java.util.List;
import java.util.Collections;
@Service @RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
  private final PedidoRepository repo;
  @Override public List<Pedido> listar(Usuario u){ 
    List<Pedido> lista = repo.findByUsuarioOrderByCreadoEnDesc(u);
    Collections.reverse(lista);
    return lista;
  }
}
