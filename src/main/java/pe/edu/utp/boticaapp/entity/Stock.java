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
public class Stock {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(optional = false)
    private Botica botica;
    
    @ManyToOne(optional = false)
    private Producto producto;
    
    private String productoSku;
    
    private Integer cantidad;
    
    private BigDecimal precio;
}