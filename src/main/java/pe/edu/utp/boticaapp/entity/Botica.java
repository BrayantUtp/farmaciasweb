package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Botica {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String nombre;
  private String direccion;
}
