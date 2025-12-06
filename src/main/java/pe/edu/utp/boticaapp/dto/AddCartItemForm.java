package pe.edu.utp.boticaapp.dto;
import jakarta.validation.constraints.*;
public record AddCartItemForm(@NotNull Long productoId, @Min(1) Integer cantidad) {}
