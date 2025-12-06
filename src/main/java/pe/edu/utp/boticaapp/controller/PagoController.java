package pe.edu.utp.boticaapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.boticaapp.dto.ResultadoPago;
import pe.edu.utp.boticaapp.entity.Comprobante;
import pe.edu.utp.boticaapp.entity.Pedido;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.PedidoRepository;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;
import pe.edu.utp.boticaapp.service.ComprobanteService;
import pe.edu.utp.boticaapp.service.StockService;
import pe.edu.utp.boticaapp.service.ValidadorPagoService;

@Controller
@RequestMapping("/pago")
@RequiredArgsConstructor
@Slf4j
public class PagoController {
    
    private final PedidoRepository pedidoRepo;
    private final UsuarioRepository userRepo;
    private final ValidadorPagoService validadorPago;
    private final StockService stockService;
    private final ComprobanteService comprobanteService;
    
    /**
     * Mostrar página de pago
     */
    @GetMapping("/{pedidoId}")
    public String mostrarPago(@PathVariable Long pedidoId,
                              @AuthenticationPrincipal User principal,
                              Model model) {
        
        Usuario usuario = userRepo.findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Pedido no autorizado");
        }
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuario", usuario);
        
        return "pago";
    }
    @GetMapping("/comprobante/{pedidoId}")
    public String verComprobante(@PathVariable Long pedidoId,
                                 @AuthenticationPrincipal User principal,
                                 Model model) {
        
        Usuario usuario = userRepo.findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Verificar que el pedido pertenece al usuario
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Pedido no autorizado");
        }
        
        // Forzar carga de detalles (lazy loading)
        pedido.getDetalles().size();
        
        // Obtener comprobante
        Comprobante comprobante = comprobanteService.obtenerPorPedido(pedido);
        
        if (comprobante == null) {
            throw new RuntimeException("No se encontró comprobante para este pedido");
        }
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("comprobante", comprobante);
        
        return "pedido-comprobante";
    }
    /**
     * Procesar pago con TARJETA
     */
    @PostMapping("/{pedidoId}/procesar-tarjeta")
    public String procesarTarjeta(@PathVariable Long pedidoId,
                                  @RequestParam String numeroTarjeta,
                                  @RequestParam String nombreTitular,
                                  @RequestParam String fechaExpiracion,
                                  @RequestParam String cvv,
                                  Model model) {
        
        log.info("💳 Procesando pago con tarjeta para pedido ID: {}", pedidoId);
        log.info("   Tarjeta: {}", numeroTarjeta);
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Validar tarjeta
        ResultadoPago resultado = validadorPago.validarTarjeta(
            numeroTarjeta.replace(" ", ""), nombreTitular, fechaExpiracion, cvv
        );
        
        if (resultado.isExitoso()) {
            // Marcar como pagado
            pedido.setEstado(Pedido.Estado.PAGADO);
            pedidoRepo.save(pedido);
            
            // Descontar stock
            stockService.descontarStockPorPedido(pedido);
            
            // Generar comprobante
            String ultimos4 = numeroTarjeta.replace(" ", "");
            ultimos4 = ultimos4.substring(ultimos4.length() - 4);
            
            Comprobante comprobante = comprobanteService.generarComprobante(
                pedido, 
                "Tarjeta **** " + ultimos4
            );
            
            log.info("✅ Pago exitoso - Comprobante: {}", comprobante.getNumeroComprobante());
            
            return "redirect:/pago/confirmacion/" + pedidoId;
        } else {
            // ✅ CORRECCIÓN: Enviar errorTitulo y errorMensaje
            log.warn("❌ Pago rechazado: {}", resultado.getMensaje());
            
            model.addAttribute("errorTitulo", "Error en el Pago");
            model.addAttribute("errorMensaje", resultado.getMensaje());
            model.addAttribute("pedido", pedido);
            
            return "pago";
        }
    }
    
    /**
     * Procesar pago con YAPE
     */
    @PostMapping("/{pedidoId}/procesar-yape")
    public String procesarYape(@PathVariable Long pedidoId,
                               @RequestParam String telefono,
                               @RequestParam String codigoOperacion,
                               Model model) {
        
        log.info("📱 Procesando pago con Yape para pedido ID: {}", pedidoId);
        log.info("   Teléfono: {}, Código: {}", telefono, codigoOperacion);
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Validar código Yape
        ResultadoPago resultado = validadorPago.validarYape(
            telefono.replace(" ", ""), 
            codigoOperacion
        );
        
        if (resultado.isExitoso()) {
            pedido.setEstado(Pedido.Estado.PAGADO);
            pedidoRepo.save(pedido);
            
            stockService.descontarStockPorPedido(pedido);
            
            Comprobante comprobante = comprobanteService.generarComprobante(
                pedido, 
                "Yape - " + telefono.replace(" ", "")
            );
            
            log.info("✅ Pago Yape exitoso - Comprobante: {}", comprobante.getNumeroComprobante());
            
            return "redirect:/pago/confirmacion/" + pedidoId;
        } else {
            // ✅ CORRECCIÓN: Enviar errorTitulo y errorMensaje
            log.warn("❌ Pago Yape rechazado: {}", resultado.getMensaje());
            
            model.addAttribute("errorTitulo", "Error en el Pago con Yape");
            model.addAttribute("errorMensaje", resultado.getMensaje());
            model.addAttribute("pedido", pedido);
            
            return "pago";
        }
    }
    
    /**
     * Procesar pago con PLIN
     */
    @PostMapping("/{pedidoId}/procesar-plin")
    public String procesarPlin(@PathVariable Long pedidoId,
                               @RequestParam String telefono,
                               @RequestParam String codigoOperacion,
                               Model model) {
        
        log.info("📱 Procesando pago con Plin para pedido ID: {}", pedidoId);
        log.info("   Teléfono: {}, Código: {}", telefono, codigoOperacion);
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Validar código Plin
        ResultadoPago resultado = validadorPago.validarPlin(
            telefono.replace(" ", ""), 
            codigoOperacion
        );
        
        if (resultado.isExitoso()) {
            pedido.setEstado(Pedido.Estado.PAGADO);
            pedidoRepo.save(pedido);
            
            stockService.descontarStockPorPedido(pedido);
            
            Comprobante comprobante = comprobanteService.generarComprobante(
                pedido, 
                "Plin - " + telefono.replace(" ", "")
            );
            
            log.info("✅ Pago Plin exitoso - Comprobante: {}", comprobante.getNumeroComprobante());
            
            return "redirect:/pago/confirmacion/" + pedidoId;
        } else {
            // ✅ CORRECCIÓN: Enviar errorTitulo y errorMensaje
            log.warn("❌ Pago Plin rechazado: {}", resultado.getMensaje());
            
            model.addAttribute("errorTitulo", "Error en el Pago con Plin");
            model.addAttribute("errorMensaje", resultado.getMensaje());
            model.addAttribute("pedido", pedido);
            
            return "pago";
        }
    }
    
    /**
     * Procesar pago CONTRA ENTREGA
     */
    @PostMapping("/{pedidoId}/procesar-contra-entrega")
    public String procesarContraEntrega(@PathVariable Long pedidoId,
                                        @RequestParam String direccion,
                                        @RequestParam String telefono,
                                        Model model) {
        
        log.info("🚚 Procesando pago contra entrega para pedido ID: {}", pedidoId);
        log.info("   Dirección: {}, Teléfono: {}", direccion, telefono);
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        // Contra entrega - validar datos básicos
        ResultadoPago resultado = validadorPago.validarContraEntrega(
            direccion, 
            telefono.replace(" ", "")
        );
        
        if (resultado.isExitoso()) {
            pedido.setEstado(Pedido.Estado.PAGADO);
            pedidoRepo.save(pedido);
            
            stockService.descontarStockPorPedido(pedido);
            
            Comprobante comprobante = comprobanteService.generarComprobante(
                pedido, 
                "Contra Entrega"
            );
            
            // Guardar dirección en el comprobante
            comprobante.setDireccionEntrega(direccion + " - Tel: " + telefono.replace(" ", ""));
            
            log.info("✅ Pago contra entrega confirmado - Comprobante: {}", comprobante.getNumeroComprobante());
            
            return "redirect:/pago/confirmacion/" + pedidoId;
        } else {
            // ✅ CORRECCIÓN: Enviar errorTitulo y errorMensaje
            log.warn("❌ Pago contra entrega rechazado: {}", resultado.getMensaje());
            
            model.addAttribute("errorTitulo", "Error en Contra Entrega");
            model.addAttribute("errorMensaje", resultado.getMensaje());
            model.addAttribute("pedido", pedido);
            
            return "pago";
        }
    }
    
    /**
     * Página de confirmación de pago
     */
    @GetMapping("/confirmacion/{pedidoId}")
    public String confirmacion(@PathVariable Long pedidoId,
                               @AuthenticationPrincipal User principal,
                               Model model) {
        
        Usuario usuario = userRepo.findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Pedido no autorizado");
        }
        
        // Obtener comprobante generado
        Comprobante comprobante = comprobanteService.obtenerPorPedido(pedido);
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("comprobante", comprobante);
        
        return "pago-confirmacion";
    }
}