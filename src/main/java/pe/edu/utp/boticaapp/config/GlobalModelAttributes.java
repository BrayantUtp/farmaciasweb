package pe.edu.utp.boticaapp.config;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final UsuarioRepository usuarioRepo;

    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = false;
        boolean isAuthenticated = false;
        
        if (auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken)) {
            isAuthenticated = true;
            Object principal = auth.getPrincipal();
            if (principal instanceof User) {
                String username = ((User) principal).getUsername();
                Usuario u = usuarioRepo.findByEmail(username).orElse(null);
                if (u != null && u.getRol() == Usuario.Rol.ADMIN) {
                    isAdmin = true;
                }
            }
        }
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("isAuthenticated", isAuthenticated);
    }
}
