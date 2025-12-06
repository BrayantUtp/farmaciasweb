package pe.edu.utp.boticaapp.service;

import pe.edu.utp.boticaapp.entity.Pedido;

/**
 * Servicio para gestionar el stock de productos en múltiples boticas
 */
public interface StockService {
    
    /**
     * Descuenta el stock de los productos de un pedido
     * Selecciona automáticamente la botica con el mejor precio
     */
    void descontarStockPorPedido(Pedido pedido);
    
    /**
     * Obtiene la cantidad total disponible de un producto sumando todas las boticas
     */
    Integer obtenerCantidadTotalDisponible(Long productoId);
    
    /**
     * Verifica si hay stock suficiente para una cantidad solicitada
     */
    boolean hayStockSuficiente(Long productoId, Integer cantidadSolicitada);
}