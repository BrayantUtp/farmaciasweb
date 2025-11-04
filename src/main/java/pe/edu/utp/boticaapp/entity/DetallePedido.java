package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DetallePedido {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @ManyToOne(optional=false) private Pedido pedido;
  @ManyToOne(optional=false) private Producto producto;
  private Integer cantidad;
  private BigDecimal precioUnitario;
}
