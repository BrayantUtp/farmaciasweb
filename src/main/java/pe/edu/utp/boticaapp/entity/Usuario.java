package pe.edu.utp.boticaapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {
  public enum Rol { CLIENTE, ADMIN }

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique=true)
  private String email;

  @Column(nullable=false)
  private String passwordHash;

  private String nombres;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  private Rol rol = Rol.CLIENTE;
}
