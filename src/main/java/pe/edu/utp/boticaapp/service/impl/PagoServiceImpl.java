package pe.edu.utp.boticaapp.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.boticaapp.dto.PagoForm;
import pe.edu.utp.boticaapp.entity.Pago;
import pe.edu.utp.boticaapp.entity.Pedido;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.PagoRepository;
import pe.edu.utp.boticaapp.repository.PedidoRepository;
import pe.edu.utp.boticaapp.service.PagoService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements PagoService {
    
    private final PagoRepository pagoRepo;
    private final PedidoRepository pedidoRepo;
    private final Random random = new Random();
    
    @Override
    @Transactional
    public Pago procesarPago(PagoForm form, Usuario usuario) {
        // Buscar el pedido
        Pedido pedido = pedidoRepo.findById(form.pedidoId())
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        
        // Verificar que el pedido pertenece al usuario
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new IllegalArgumentException("El pedido no pertenece al usuario");
        }
        
        // Verificar que el pedido está pendiente
        if (pedido.getEstado() != Pedido.Estado.PENDIENTE) {
            throw new IllegalStateException("El pedido ya fue procesado");
        }
        
        // Verificar que no existe un pago previo
        if (pagoRepo.findByPedido(pedido).isPresent()) {
            throw new IllegalStateException("Ya existe un pago para este pedido");
        }
        
        // Simular procesamiento de pago
        boolean pagoExitoso = simularProcesadorPago(form);
        
        // Obtener últimos 4 dígitos de la tarjeta
        String ultimos4 = form.numeroTarjeta().substring(12);
        
        // Generar código de transacción único
        String codigoTransaccion = generarCodigoTransaccion();
        
        // Crear el pago
        Pago pago = Pago.builder()
            .pedido(pedido)
            .metodoPago(Pago.MetodoPago.valueOf(form.metodoPago()))
            .monto(pedido.getTotal())
            .numeroTarjetaUltimos4(ultimos4)
            .codigoTransaccion(codigoTransaccion)
            .fechaPago(LocalDateTime.now())
            .build();
        
        if (pagoExitoso) {
            pago.setEstado(Pago.EstadoPago.APROBADO);
            pago.setMensajeRespuesta("Pago aprobado exitosamente");
            
            // Actualizar estado del pedido
            pedido.setEstado(Pedido.Estado.PAGADO);
            pedidoRepo.save(pedido);
        } else {
            pago.setEstado(Pago.EstadoPago.RECHAZADO);
            pago.setMensajeRespuesta("Pago rechazado - Fondos insuficientes o tarjeta inválida");
        }
        
        return pagoRepo.save(pago);
    }
    
    @Override
    public List<Pago> obtenerHistorialPagos(Usuario usuario) {
        return pagoRepo.findByPedido_UsuarioOrderByFechaPagoDesc(usuario);
    }
    
    @Override
    public Pago buscarPorCodigoTransaccion(String codigo) {
        return pagoRepo.findByCodigoTransaccion(codigo)
            .orElseThrow(() -> new IllegalArgumentException("Transacción no encontrada"));
    }
    
    @Override
    public Pago obtenerPagoPorPedido(Long pedidoId) {
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
        
        return pagoRepo.findByPedido(pedido)
            .orElse(null);
    }
    
    /**
     * Simula el procesamiento de pago con un procesador real
     * En producción, aquí se haría la llamada a la API de Culqi, MercadoPago, etc.
     */
    private boolean simularProcesadorPago(PagoForm form) {
        // Simulación: 90% de probabilidad de éxito
        // Puedes cambiar la lógica según necesites
        
        // Tarjetas de prueba que siempre aprueban
        if (form.numeroTarjeta().startsWith("4111") || 
            form.numeroTarjeta().startsWith("5555")) {
            return true;
        }
        
        // Tarjetas de prueba que siempre rechazan
        if (form.numeroTarjeta().startsWith("4000")) {
            return false;
        }
        
        // Para otras tarjetas, 90% de éxito
        return random.nextInt(100) < 90;
    }
    
    /**
     * Genera un código de transacción único
     */
    private String generarCodigoTransaccion() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
