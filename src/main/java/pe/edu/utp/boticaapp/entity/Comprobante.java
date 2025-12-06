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
@Table(name = "comprobante")
public class Comprobante {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Relación uno a uno con Pedido
    @OneToOne
    @JoinColumn(name = "pedido_id", nullable = false, unique = true)
    private Pedido pedido;
    
    // Número de comprobante (formato: COMP-000001)
    @Column(nullable = false, unique = true, length = 20)
    private String numeroComprobante;
    
    // Tipo de comprobante (BOLETA, FACTURA)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TipoComprobante tipo = TipoComprobante.BOLETA;
    
    // Datos del cliente (copiados al momento de generar)
    @Column(nullable = false)
    private String clienteNombre;
    
    @Column(nullable = false)
    private String clienteEmail;
    
    // Datos del pedido (copiados al momento de generar)
    @Column(nullable = false)
    private BigDecimal subtotal;
    
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Column(nullable = false)
    @Builder.Default
    private BigDecimal igv = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal total;
    
    // Método de pago usado
    @Column(length = 50)
    private String metodoPago;
    
    // Fecha y hora de emisión
    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime fechaEmision = LocalDateTime.now();
    
    // Estado del comprobante
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoComprobante estado = EstadoComprobante.EMITIDO;
    
    // RUC/DNI del cliente (opcional)
    @Column(length = 20)
    private String documentoCliente;
    
    // Dirección de entrega (opcional)
    @Column(length = 500)
    private String direccionEntrega;
    
    // Observaciones
    @Column(length = 1000)
    private String observaciones;
    
    // Enums
    public enum TipoComprobante {
        BOLETA,
        FACTURA
    }
    
    public enum EstadoComprobante {
        EMITIDO,
        ANULADO
    }
}
