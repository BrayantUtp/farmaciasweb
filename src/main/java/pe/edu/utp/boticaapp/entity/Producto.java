package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    private BigDecimal precio;
    private String categoria;
    private String sku;
    
    // Campo temporal para mostrar stock (NO se guarda en BD)
    @Transient
    private Integer stock;
}