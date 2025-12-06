package pe.edu.utp.boticaapp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.utp.boticaapp.entity.Comprobante;
import pe.edu.utp.boticaapp.entity.Pedido;
import pe.edu.utp.boticaapp.repository.ComprobanteRepository;
import pe.edu.utp.boticaapp.service.ComprobanteService;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComprobanteServiceImpl implements ComprobanteService {
    
    private final ComprobanteRepository comprobanteRepo;
    
    @Override
    @Transactional
    public Comprobante generarComprobante(Pedido pedido, String metodoPago) {
        log.info("📄 Generando comprobante para pedido ID: {}", pedido.getId());
        
        // Verificar si ya existe un comprobante para este pedido
        if (comprobanteRepo.existsByPedido(pedido)) {
            log.warn("⚠️ Ya existe un comprobante para el pedido ID: {}", pedido.getId());
            return comprobanteRepo.findByPedido(pedido).orElseThrow();
        }
        
        // Generar número de comprobante único
        String numeroComprobante = generarNumeroComprobante();
        
        // Calcular IGV (18%)
        BigDecimal igv = pedido.getTotal()
            .multiply(new BigDecimal("0.18"))
            .setScale(2, RoundingMode.HALF_UP);
        
        // Calcular subtotal (total sin IGV)
        BigDecimal subtotal = pedido.getTotal().subtract(igv);
        
        // Crear comprobante
        Comprobante comprobante = Comprobante.builder()
            .pedido(pedido)
            .numeroComprobante(numeroComprobante)
            .tipo(Comprobante.TipoComprobante.BOLETA)
            .clienteNombre(pedido.getUsuario().getNombres())
            .clienteEmail(pedido.getUsuario().getEmail())
            .subtotal(subtotal)
            .descuento(BigDecimal.ZERO)
            .igv(igv)
            .total(pedido.getTotal())
            .metodoPago(metodoPago)
            .estado(Comprobante.EstadoComprobante.EMITIDO)
            .build();
        
        comprobante = comprobanteRepo.save(comprobante);
        
        log.info("✅ Comprobante generado: {} - Total: S/ {}", 
            numeroComprobante, pedido.getTotal());
        
        return comprobante;
    }
    
    @Override
    public Comprobante obtenerPorPedido(Pedido pedido) {
        return comprobanteRepo.findByPedido(pedido)
            .orElseThrow(() -> new RuntimeException("Comprobante no encontrado para pedido ID: " + pedido.getId()));
    }
    
    @Override
    public Comprobante buscarPorNumero(String numeroComprobante) {
        return comprobanteRepo.findByNumeroComprobante(numeroComprobante)
            .orElseThrow(() -> new RuntimeException("Comprobante no encontrado: " + numeroComprobante));
    }
    
    @Override
    @Transactional
    public void anularComprobante(Long comprobanteId) {
        Comprobante comprobante = comprobanteRepo.findById(comprobanteId)
            .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));
        
        comprobante.setEstado(Comprobante.EstadoComprobante.ANULADO);
        comprobanteRepo.save(comprobante);
        
        log.info("🚫 Comprobante anulado: {}", comprobante.getNumeroComprobante());
    }
    
    /**
     * Genera un número de comprobante único en formato: COMP-000001
     */
    private String generarNumeroComprobante() {
        String ultimoNumero = comprobanteRepo.findUltimoNumeroComprobante()
            .orElse("COMP-000000");
        
        // Extraer el número
        int numero = Integer.parseInt(ultimoNumero.substring(5)) + 1;
        
        // Formatear con ceros a la izquierda (6 dígitos)
        return String.format("COMP-%06d", numero);
    }
}
