package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Table(name="carrito_item")
public class CarritoItem {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional=false) private Usuario usuario;
  @ManyToOne(optional=false) private Producto producto;

  @Column(nullable=false) private Integer cantidad;
}
