package pe.edu.utp.boticaapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.boticaapp.entity.*;
import pe.edu.utp.boticaapp.repository.*;
import pe.edu.utp.boticaapp.service.CartService;
import pe.edu.utp.boticaapp.service.StockService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {
    
    private final CarritoItemRepository cartRepo;
    private final ProductoRepository prodRepo;
    private final PedidoRepository pedidoRepo;
    private final StockService stockService;
    
    @Override
    public List<CarritoItem> listar(Usuario u) {
        return cartRepo.findByUsuario(u);
    }
    
    @Override
    public void agregar(Usuario u, Long productoId, Integer cantidad) {
        var p = prodRepo.findById(productoId).orElseThrow();
        
        // Verificar stock disponible
        Integer stockDisponible = stockService.obtenerCantidadTotalDisponible(productoId);
        
        if (cantidad > stockDisponible) {
            throw new IllegalStateException(
                String.format("Stock insuficiente. Disponible: %d, Solicitado: %d", 
                    stockDisponible, cantidad)
            );
        }
        
        cartRepo.save(CarritoItem.builder()
            .usuario(u)
            .producto(p)
            .cantidad(cantidad)
            .build());
    }
    
    @Override
    public void quitar(Usuario u, Long itemId) {
        cartRepo.deleteById(itemId);
    }
    
    @Override
    @Transactional
    public Long checkout(Usuario u) {
        var items = cartRepo.findByUsuario(u);
        
        if (items.isEmpty()) {
            throw new IllegalStateException("Carrito vacío");
        }
        
        // VERIFICAR STOCK ANTES DE CREAR PEDIDO
        for (var item : items) {
            if (!stockService.hayStockSuficiente(item.getProducto().getId(), item.getCantidad())) {
                throw new IllegalStateException(
                    String.format("Stock insuficiente para: %s", item.getProducto().getNombre())
                );
            }
        }
        
        // Crear pedido
        var pedido = Pedido.builder().usuario(u).build();
        pedido = pedidoRepo.save(pedido);
        
        BigDecimal total = BigDecimal.ZERO;
        
        // Crear detalles
        for (var it : items) {
            var det = DetallePedido.builder()
                .pedido(pedido)
                .producto(it.getProducto())
                .cantidad(it.getCantidad())
                .precioUnitario(it.getProducto().getPrecio())
                .build();
            
            pedido.getDetalles().add(det);
            total = total.add(it.getProducto().getPrecio().multiply(new BigDecimal(it.getCantidad())));
        }
        
        pedido.setTotal(total);
        pedidoRepo.save(pedido);
        
        // NO DESCONTAMOS AQUÍ - Se descuenta al PAGAR
        
        // Limpiar carrito
        cartRepo.deleteByUsuario(u);
        
        log.info("Pedido creado ID: {} por usuario: {}. Total: {}", pedido.getId(), u.getEmail(), total);
        
        return pedido.getId();
    }
}