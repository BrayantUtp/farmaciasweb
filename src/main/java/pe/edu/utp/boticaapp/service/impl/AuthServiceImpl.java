package pe.edu.utp.boticaapp.service.impl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.edu.utp.boticaapp.dto.RegistroForm;
import pe.edu.utp.boticaapp.entity.Usuario;
import pe.edu.utp.boticaapp.repository.UsuarioRepository;
import pe.edu.utp.boticaapp.service.AuthService;
@Service @RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
  private final UsuarioRepository repo; private final PasswordEncoder encoder;
  @Override public void registrar(RegistroForm form){
    if(repo.findByEmail(form.email()).isPresent()) throw new IllegalArgumentException("Email ya registrado");
    Usuario u = Usuario.builder().email(form.email()).passwordHash(encoder.encode(form.password())).nombres(form.nombres()).rol(Usuario.Rol.CLIENTE).build();
    repo.save(u);
  }
}
