package pe.edu.utp.boticaapp.service;

import pe.edu.utp.boticaapp.dto.PagoForm;
import pe.edu.utp.boticaapp.entity.Pago;
import pe.edu.utp.boticaapp.entity.Usuario;
import java.util.List;

public interface PagoService {
    
    /**
     * Procesa un pago para un pedido
     * @param form Datos del formulario de pago
     * @param usuario Usuario que realiza el pago
     * @return El pago procesado
     */
    Pago procesarPago(PagoForm form, Usuario usuario);
    
    /**
     * Obtiene el historial de pagos de un usuario
     * @param usuario Usuario
     * @return Lista de pagos
     */
    List<Pago> obtenerHistorialPagos(Usuario usuario);
    
    /**
     * Busca un pago por código de transacción
     * @param codigo Código de transacción
     * @return Pago encontrado
     */
    Pago buscarPorCodigoTransaccion(String codigo);
    
    /**
     * Obtiene el pago asociado a un pedido
     * @param pedidoId ID del pedido
     * @return Pago si existe
     */
    Pago obtenerPagoPorPedido(Long pedidoId);
}
