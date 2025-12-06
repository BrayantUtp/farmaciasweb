package pe.edu.utp.boticaapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.edu.utp.boticaapp.dto.ResultadoPago;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class ValidadorPagoService {
    
    // Tarjetas de prueba VÁLIDAS (con saldo)
    private static final List<String> TARJETAS_VALIDAS = Arrays.asList(
        "4532015112830366",  // Visa
        "5425233430109903",  // Mastercard
        "378282246310005"    // American Express
    );
    
    // Tarjetas de prueba SIN SALDO
    private static final List<String> TARJETAS_SIN_SALDO = Arrays.asList(
        "4111111111111111",  // Visa sin saldo
        "5105105105105100"   // Mastercard sin saldo
    );
    
    // Códigos de operación válidos para Yape/Plin
    private static final List<String> CODIGOS_VALIDOS = Arrays.asList(
        "123456",
        "999888",
        "456789"
    );
    
    public ResultadoPago validarTarjeta(String numeroTarjeta, String nombreTitular, 
                                         String fechaExpiracion, String cvv) {
        
        log.info("💳 Validando tarjeta: {}", nombreTitular);
        
        if (numeroTarjeta == null || numeroTarjeta.length() < 13) {
            return ResultadoPago.error("Número de tarjeta inválido", "INVALID_CARD");
        }
        
        if (nombreTitular == null || nombreTitular.trim().isEmpty()) {
            return ResultadoPago.error("Nombre del titular es requerido", "INVALID_NAME");
        }
        
        if (cvv == null || cvv.length() < 3) {
            return ResultadoPago.error("CVV inválido", "INVALID_CVV");
        }
        
        if (fechaExpiracion == null || !fechaExpiracion.matches("\\d{2}/\\d{2}")) {
            return ResultadoPago.error("Fecha de expiración inválida", "INVALID_DATE");
        }
        
        if (TARJETAS_SIN_SALDO.contains(numeroTarjeta)) {
            log.warn("❌ Tarjeta sin saldo: {}", numeroTarjeta);
            return ResultadoPago.error("Saldo insuficiente", "INSUFFICIENT_FUNDS");
        }
        
        if (TARJETAS_VALIDAS.contains(numeroTarjeta)) {
            log.info("✅ Tarjeta válida");
            return ResultadoPago.exito("Pago procesado exitosamente");
        }
        
        log.warn("❌ Tarjeta no válida: {}", numeroTarjeta);
        return ResultadoPago.error("Tarjeta no válida", "INVALID_CARD");
    }
    
    public ResultadoPago validarYape(String telefono, String codigoOperacion) {
        
        log.info("📱 Validando Yape: {}", telefono);
        
        if (telefono == null || !telefono.matches("\\d{9}")) {
            return ResultadoPago.error("Número de teléfono inválido", "INVALID_PHONE");
        }
        
        if (codigoOperacion == null || !codigoOperacion.matches("\\d{6}")) {
            return ResultadoPago.error("Código de operación inválido", "INVALID_CODE");
        }
        
        if (CODIGOS_VALIDOS.contains(codigoOperacion)) {
            log.info("✅ Código Yape válido");
            return ResultadoPago.exito("Pago con Yape procesado exitosamente");
        }
        
        log.warn("❌ Código Yape inválido");
        return ResultadoPago.error("Código de operación Yape no válido", "INVALID_YAPE_CODE");
    }
    
    public ResultadoPago validarPlin(String telefono, String codigoOperacion) {
        
        log.info("📱 Validando Plin: {}", telefono);
        
        if (telefono == null || !telefono.matches("\\d{9}")) {
            return ResultadoPago.error("Número de teléfono inválido", "INVALID_PHONE");
        }
        
        if (codigoOperacion == null || !codigoOperacion.matches("\\d{6}")) {
            return ResultadoPago.error("Código de operación inválido", "INVALID_CODE");
        }
        
        if (CODIGOS_VALIDOS.contains(codigoOperacion)) {
            log.info("✅ Código Plin válido");
            return ResultadoPago.exito("Pago con Plin procesado exitosamente");
        }
        
        log.warn("❌ Código Plin inválido");
        return ResultadoPago.error("Código de operación Plin no válido", "INVALID_PLIN_CODE");
    }
    
    public ResultadoPago validarContraEntrega(String direccion, String telefono) {
        
        log.info("🚚 Validando contra entrega: {}", direccion);
        
        if (direccion == null || direccion.trim().length() < 10) {
            return ResultadoPago.error("Dirección muy corta", "INVALID_ADDRESS");
        }
        
        if (telefono == null || !telefono.matches("\\d{9}")) {
            return ResultadoPago.error("Número de teléfono inválido", "INVALID_PHONE");
        }
        
        log.info("✅ Datos de entrega válidos");
        return ResultadoPago.exito("Pedido confirmado para entrega contra pago");
    }
}