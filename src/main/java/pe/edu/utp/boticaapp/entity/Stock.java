package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Stock {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // en Stock.java
  @Column(name = "producto_sku")
  private String productoSku;
  
  @ManyToOne(optional = false)
  private Producto producto;

  @ManyToOne(optional = false)
  private Botica botica;

  private Integer cantidad;

  private BigDecimal precio;
}
