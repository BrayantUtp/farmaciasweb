package pe.edu.utp.boticaapp.service;

import pe.edu.utp.boticaapp.entity.Comprobante;
import pe.edu.utp.boticaapp.entity.Pedido;

public interface ComprobanteService {
    
    /**
     * Genera un comprobante automáticamente al pagar un pedido
     */
    Comprobante generarComprobante(Pedido pedido, String metodoPago);
    
    /**
     * Obtiene el comprobante de un pedido
     */
    Comprobante obtenerPorPedido(Pedido pedido);
    
    /**
     * Busca un comprobante por su número
     */
    Comprobante buscarPorNumero(String numeroComprobante);
    
    /**
     * Anula un comprobante
     */
    void anularComprobante(Long comprobanteId);
}
