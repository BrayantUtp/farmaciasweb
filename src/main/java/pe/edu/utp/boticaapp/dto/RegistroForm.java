package pe.edu.utp.boticaapp.dto;
import jakarta.validation.constraints.*;
public record RegistroForm(
  @Email @NotBlank String email,
  @Size(min=6) String password,
  @NotBlank String nombres
) {}
