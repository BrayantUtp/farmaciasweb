package pe.edu.utp.boticaapp.config;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;
import pe.edu.utp.boticaapp.entity.*;
import pe.edu.utp.boticaapp.repository.*;
@Configuration
public class DataSeed {
  @Bean CommandLineRunner init(ProductoRepository prodRepo, UsuarioRepository userRepo, PasswordEncoder enc){
    return args -> {
      if(prodRepo.count()==0){
        prodRepo.save(Producto.builder().nombre("Paracetamol 500mg").precio(new BigDecimal("5.50")).categoria("Analgesico").build());
        prodRepo.save(Producto.builder().nombre("Ibuprofeno 400mg").precio(new BigDecimal("7.90")).categoria("Analgesico").build());
        prodRepo.save(Producto.builder().nombre("Omeprazol 20mg").precio(new BigDecimal("9.50")).categoria("Gastro").build());
      }
      if(userRepo.count()==0){
        userRepo.save(Usuario.builder().email("admin@utp.pe").passwordHash(enc.encode("admin123")).nombres("Admin").rol(Usuario.Rol.ADMIN).build());
        userRepo.save(Usuario.builder().email("cliente@utp.pe").passwordHash(enc.encode("cliente123")).nombres("Cliente").rol(Usuario.Rol.CLIENTE).build());
      }
    };
  }
}
