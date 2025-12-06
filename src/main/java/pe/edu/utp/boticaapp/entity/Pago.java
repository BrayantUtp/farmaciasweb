package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class Pago {
    
    public enum MetodoPago { 
        TARJETA_CREDITO, 
        TARJETA_DEBITO, 
        YAPE, 
        PLIN 
    }
    
    public enum EstadoPago { 
        PENDIENTE, 
        APROBADO, 
        RECHAZADO, 
        CANCELADO 
    }
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    private Pedido pedido;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MetodoPago metodoPago = MetodoPago.TARJETA_CREDITO;
    
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;
    
    private BigDecimal monto;
    
    @Builder.Default
    private LocalDateTime fechaPago = LocalDateTime.now();
    
    // Datos de la tarjeta (últimos 4 dígitos)
    private String numeroTarjetaUltimos4;
    
    // Código de transacción único
    private String codigoTransaccion;
    
    // Mensaje de respuesta del procesador
    private String mensajeRespuesta;
}
