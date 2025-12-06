package pe.edu.utp.boticaapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.utp.boticaapp.entity.Pedido;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.PedidoRepository;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;
import pe.edu.utp.boticaapp.service.PedidoService;
import pe.edu.utp.boticaapp.entity.Comprobante;
import pe.edu.utp.boticaapp.service.ComprobanteService;
@Controller
@RequestMapping("/pedidos")
@RequiredArgsConstructor

public class PedidoController {
    
    private final PedidoService pedidos;
    private final UsuarioRepository users;
    private final PedidoRepository pedidoRepo;
    
    @GetMapping
    public String listar(@AuthenticationPrincipal User p, Model model) {
        var u = users.findByEmail(p.getUsername()).orElseThrow();
        model.addAttribute("pedidos", pedidos.listar(u));
        return "pedidos";
    }
    
    @GetMapping("/comprobante/{pedidoId}")
    public String comprobante(@PathVariable Long pedidoId,
                              @AuthenticationPrincipal User principal,
                              Model model) {
        Usuario usuario = users.findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        Pedido pedido = pedidoRepo.findById(pedidoId)
            .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));
        
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Pedido no autorizado");
        }
        
        // Forzar carga de detalles
        pedido.getDetalles().size();
        
        model.addAttribute("pedido", pedido);
        model.addAttribute("usuario", usuario);
        
        return "pedido-comprobante";
    }
}