package pe.edu.utp.boticaapp.dto;

import jakarta.validation.constraints.*;

public record PagoForm(
    @NotNull(message = "El pedido es requerido")
    Long pedidoId,
    
    @NotBlank(message = "El número de tarjeta es requerido")
    @Pattern(regexp = "\\d{16}", message = "El número de tarjeta debe tener 16 dígitos")
    String numeroTarjeta,
    
    @NotBlank(message = "El nombre del titular es requerido")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String nombreTitular,
    
    @NotBlank(message = "La fecha de vencimiento es requerida")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Formato debe ser MM/YY")
    String fechaVencimiento,
    
    @NotBlank(message = "El CVV es requerido")
    @Pattern(regexp = "\\d{3,4}", message = "El CVV debe tener 3 o 4 dígitos")
    String cvv,
    
    @NotNull(message = "El método de pago es requerido")
    String metodoPago
) {}
