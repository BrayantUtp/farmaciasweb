package pe.edu.utp.boticaapp.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.boticaapp.entity.*;
import pe.edu.utp.boticaapp.repository.*;
import pe.edu.utp.boticaapp.service.CartService;
import java.math.BigDecimal;
import java.util.List;
@Service @RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final CarritoItemRepository cartRepo; private final ProductoRepository prodRepo; private final PedidoRepository pedidoRepo;
  @Override public List<CarritoItem> listar(Usuario u){ return cartRepo.findByUsuario(u); }
  @Override public void agregar(Usuario u, Long productoId, Integer cantidad){ var p=prodRepo.findById(productoId).orElseThrow(); cartRepo.save(CarritoItem.builder().usuario(u).producto(p).cantidad(cantidad).build()); }
  @Override public void quitar(Usuario u, Long itemId){ cartRepo.deleteById(itemId); }
  @Override @Transactional public Long checkout(Usuario u){
    var items = cartRepo.findByUsuario(u); if(items.isEmpty()) throw new IllegalStateException("Carrito vacío");
    var pedido = Pedido.builder().usuario(u).build(); pedido = pedidoRepo.save(pedido);
    BigDecimal total = BigDecimal.ZERO;
    for(var it: items){ var det = DetallePedido.builder().pedido(pedido).producto(it.getProducto()).cantidad(it.getCantidad()).precioUnitario(it.getProducto().getPrecio()).build(); 
      pedido.getDetalles().add(det); total = total.add(it.getProducto().getPrecio().multiply(new BigDecimal(it.getCantidad()))); }
    pedido.setTotal(total); pedidoRepo.save(pedido); cartRepo.deleteByUsuario(u); return pedido.getId();
  }
}
