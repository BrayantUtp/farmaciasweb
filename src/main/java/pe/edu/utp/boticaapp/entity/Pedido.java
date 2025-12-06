package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Pedido {
  public enum Estado { PENDIENTE, PAGADO, ENTREGADO, CANCELADO }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional=false) private Usuario usuario;

  @Builder.Default
  @Enumerated(EnumType.STRING) private Estado estado = Estado.PENDIENTE;
  @Builder.Default
  private BigDecimal total = BigDecimal.ZERO;
  @Builder.Default
  private LocalDateTime creadoEn = LocalDateTime.now();

  @OneToMany(mappedBy="pedido", cascade=CascadeType.ALL, orphanRemoval=true)
  @Builder.Default
  private List<DetallePedido> detalles = new ArrayList<>();
}
