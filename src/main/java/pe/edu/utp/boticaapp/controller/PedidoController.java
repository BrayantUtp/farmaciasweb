package pe.edu.utp.boticaapp.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;
import pe.edu.utp.boticaapp.service.PedidoService;
@Controller @RequestMapping("/pedidos") @RequiredArgsConstructor
public class PedidoController {
  private final PedidoService pedidos; private final UsuarioRepository users;
  @GetMapping public String listar(@AuthenticationPrincipal User p, Model model){
    var u = users.findByEmail(p.getUsername()).orElseThrow();
    model.addAttribute("pedidos", pedidos.listar(u));
    return "pedidos";
  }
}
