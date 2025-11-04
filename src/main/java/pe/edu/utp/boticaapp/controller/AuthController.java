package pe.edu.utp.boticaapp.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import pe.edu.utp.boticaapp.dto.RegistroForm;
import pe.edu.utp.boticaapp.service.AuthService;
@Controller @RequiredArgsConstructor
public class AuthController {
  private final AuthService auth;
  @GetMapping("/login") public String login() { return "login"; }
  @GetMapping("/register") public String registerForm(Model model){ model.addAttribute("form", new RegistroForm("", "", "")); return "register"; }
  @PostMapping("/register") public String register(@ModelAttribute("form") @Valid RegistroForm form, BindingResult br){
    if(br.hasErrors()) return "register";
    auth.registrar(form);
    return "redirect:/login?registered";
  }
}
