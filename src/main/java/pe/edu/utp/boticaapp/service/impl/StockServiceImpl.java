package pe.edu.utp.boticaapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.boticaapp.entity.DetallePedido;
import pe.edu.utp.boticaapp.entity.Pedido;
import pe.edu.utp.boticaapp.entity.Stock;
import pe.edu.utp.boticaapp.repository.ProductoRepository;
import pe.edu.utp.boticaapp.repository.StockRepository;
import pe.edu.utp.boticaapp.service.StockService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {
    
    private final ProductoRepository productoRepo;
    private final StockRepository stockRepo;
    
    @Override
    @Transactional
    public void descontarStockPorPedido(Pedido pedido) {
        log.info("📦 Descontando stock para pedido ID: {}", pedido.getId());
        
        for (DetallePedido detalle : pedido.getDetalles()) {
            Long productoId = detalle.getProducto().getId();
            int cantidadRequerida = detalle.getCantidad();
            
            // Obtener stocks disponibles ordenados por precio (más barato primero)
            List<Stock> stocks = stockRepo.findByProductoIdOrderByPrecioAsc(productoId);
            
            if (stocks.isEmpty()) {
                throw new RuntimeException("No hay stock disponible para: " + detalle.getProducto().getNombre());
            }
            
            int cantidadRestante = cantidadRequerida;
            
            // Descontar de las boticas con mejor precio primero
            for (Stock stock : stocks) {
                if (cantidadRestante <= 0) break;
                
                int stockDisponible = stock.getCantidad();
                int aDescontar = Math.min(stockDisponible, cantidadRestante);
                
                if (aDescontar > 0) {
                    stock.setCantidad(stockDisponible - aDescontar);
                    stockRepo.save(stock);
                    cantidadRestante -= aDescontar;
                    
                    log.info("  ✅ {} - Botica: {} - Descontado: {} - Quedan: {}", 
                        detalle.getProducto().getNombre(),
                        stock.getBotica().getNombre(),
                        aDescontar,
                        stock.getCantidad()
                    );
                }
            }
            
            if (cantidadRestante > 0) {
                throw new RuntimeException(
                    String.format("Stock insuficiente para: %s (Faltaron: %d unidades)",
                        detalle.getProducto().getNombre(),
                        cantidadRestante
                    )
                );
            }
        }
        
        log.info("✅ Stock descontado correctamente para pedido #{}", pedido.getId());
    }
    
    @Override
    public Integer obtenerCantidadTotalDisponible(Long productoId) {
        // Sumar stock de todas las boticas para este producto
        Integer stockTotal = stockRepo.findByProductoId(productoId)
            .stream()
            .mapToInt(Stock::getCantidad)
            .sum();
        
        log.info("📊 Stock total disponible del producto ID {}: {} unidades", productoId, stockTotal);
        
        return stockTotal;
    }
    
    @Override
    public boolean hayStockSuficiente(Long productoId, Integer cantidadSolicitada) {
        Integer stockDisponible = obtenerCantidadTotalDisponible(productoId);
        
        boolean suficiente = stockDisponible >= cantidadSolicitada;
        
        if (suficiente) {
            log.info("✅ Stock suficiente - Disponible: {}, Solicitado: {}", 
                stockDisponible, cantidadSolicitada);
        } else {
            log.warn("❌ Stock insuficiente - Disponible: {}, Solicitado: {}", 
                stockDisponible, cantidadSolicitada);
        }
        
        return suficiente;
    }
}