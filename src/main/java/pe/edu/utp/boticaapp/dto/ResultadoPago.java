package pe.edu.utp.boticaapp.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoPago {
    
    private boolean exitoso;
    private String mensaje;
    private String codigoError;
    
    public static ResultadoPago exito(String mensaje) {
        return ResultadoPago.builder()
            .exitoso(true)
            .mensaje(mensaje)
            .build();
    }
    
    public static ResultadoPago error(String mensaje, String codigoError) {
        return ResultadoPago.builder()
            .exitoso(false)
            .mensaje(mensaje)
            .codigoError(codigoError)
            .build();
    }
}