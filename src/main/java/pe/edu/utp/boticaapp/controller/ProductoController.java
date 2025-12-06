package pe.edu.utp.boticaapp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pe.edu.utp.boticaapp.entity.Producto;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.ProductoRepository;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;
import pe.edu.utp.boticaapp.service.CartService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/catalogo")
@RequiredArgsConstructor
@Slf4j
public class ProductoController {
    
    private final ProductoRepository productoRepo;
    private final UsuarioRepository usuarioRepo;
    private final CartService cartService;
    
    /**
     * Muestra el detalle de un producto
     */
    @GetMapping("/producto/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        log.info("📄 Mostrando detalle del producto ID: {}", id);
        
        // Obtener el producto
        Producto producto = productoRepo.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        // Obtener productos relacionados (misma categoría, máximo 4)
        List<Producto> relacionados = productoRepo.findAll().stream()
            .filter(p -> p.getCategoria().equals(producto.getCategoria()))
            .filter(p -> !p.getId().equals(producto.getId()))
            .limit(4)
            .collect(Collectors.toList());
        
        model.addAttribute("producto", producto);
        model.addAttribute("relacionados", relacionados);
        
        return "producto-detalle";
    }
    
    /**
     * Agrega producto al carrito desde la página de detalle
     * Redirige de vuelta al detalle con mensaje de éxito
     */
    @PostMapping("/producto/{id}/agregar")
    public String agregarAlCarrito(
            @PathVariable Long id,
            @RequestParam Integer cantidad,
            @AuthenticationPrincipal User principal,
            RedirectAttributes redirectAttributes) {
        
        log.info("🛒 Agregando producto {} al carrito - Cantidad: {}", id, cantidad);
        
        // Obtener usuario
        Usuario usuario = usuarioRepo.findByEmail(principal.getUsername())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        // Agregar al carrito
        cartService.agregar(usuario, id, cantidad);
        
        log.info("✅ Producto agregado exitosamente al carrito del usuario: {}", usuario.getEmail());
        
        // Redirigir al detalle del producto con parámetro de éxito
        return "redirect:/catalogo/producto/" + id + "?added=true";
    }
}
