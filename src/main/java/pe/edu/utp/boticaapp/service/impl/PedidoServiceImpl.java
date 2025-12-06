package pe.edu.utp.boticaapp.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.edu.utp.boticaapp.repository.PedidoRepository;
import pe.edu.utp.boticaapp.entity.*;
import pe.edu.utp.boticaapp.service.PedidoService;
import java.util.List;
@Service @RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {
  private final PedidoRepository repo;
  @Override public List<Pedido> listar(Usuario u){ return repo.findByUsuarioOrderByCreadoEnDesc(u); }
}
