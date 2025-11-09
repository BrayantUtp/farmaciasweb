package pe.edu.utp.boticaapp.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.boticaapp.dto.AddCartItemForm;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;
import pe.edu.utp.boticaapp.service.CartService;
@Controller @RequestMapping("/cart") @RequiredArgsConstructor
public class CartController {
  private final CartService cart; private final UsuarioRepository users;
  private Usuario cu(User p){ return users.findByEmail(p.getUsername()).orElseThrow(); }
  @GetMapping
  public String view(@AuthenticationPrincipal User p, Model model) {
    var u = cu(p);
    var items = cart.listar(u);
    model.addAttribute("items", items);
    //total del carrito
    java.math.BigDecimal total = items.stream()
      .map(i -> i.getProducto().getPrecio().multiply(java.math.BigDecimal.valueOf(i.getCantidad())))
      .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
    model.addAttribute("totalCarrito", total);
    return "cart";
  }

  @PostMapping("/add") public String add(@AuthenticationPrincipal User p, @ModelAttribute @Valid AddCartItemForm form){
    cart.agregar(cu(p), form.productoId(), form.cantidad()); return "redirect:/cart";
  }
  @PostMapping("/delete/{id}") public String del(@AuthenticationPrincipal User p, @PathVariable Long id){ cart.quitar(cu(p), id); return "redirect:/cart"; }
  @PostMapping("/checkout") public String checkout(@AuthenticationPrincipal User p){ Long id=cart.checkout(cu(p)); return "redirect:/pedidos"; }
}
