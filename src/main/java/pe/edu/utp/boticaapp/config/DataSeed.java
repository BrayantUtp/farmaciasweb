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
  @Bean CommandLineRunner init(ProductoRepository prodRepo, UsuarioRepository userRepo, PasswordEncoder enc, pe.edu.utp.boticaapp.repository.BoticaRepository boticaRepo, pe.edu.utp.boticaapp.repository.StockRepository stockRepo){
    return args -> {
      if(prodRepo.count()==0){
        var p1 = prodRepo.save(Producto.builder().sku("PARA500").nombre("Paracetamol 500mg").precio(new BigDecimal("5.50")).categoria("Analgesico").build());
        var p2 = prodRepo.save(Producto.builder().sku("IBUP400").nombre("Ibuprofeno 400mg").precio(new BigDecimal("7.90")).categoria("Analgesico").build());
        var p3 = prodRepo.save(Producto.builder().sku("OMEP20").nombre("Omeprazol 20mg").precio(new BigDecimal("9.50")).categoria("Gastro").build());
        // crear algunas boticas y stock de ejemplo
        var b1 = boticaRepo.save(pe.edu.utp.boticaapp.entity.Botica.builder().nombre("Botica Centro").direccion("Av. Principal 123").build());
        var b2 = boticaRepo.save(pe.edu.utp.boticaapp.entity.Botica.builder().nombre("Botica Norte").direccion("Jr. Norte 45").build());
        var b3 = boticaRepo.save(pe.edu.utp.boticaapp.entity.Botica.builder().nombre("Botica Sur").direccion("Calle Sur 99").build());
        // stock: algunos con cantidad 0 que no deben mostrarse
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p1).productoSku(p1.getSku()).botica(b1)
            .cantidad(10).precio(new BigDecimal("5.20")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p1).productoSku(p1.getSku()).botica(b2)
            .cantidad(0).precio(new BigDecimal("5.00")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p1).productoSku(p1.getSku()).botica(b3)
            .cantidad(5).precio(new BigDecimal("5.60")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p2).productoSku(p2.getSku()).botica(b1)
            .cantidad(3).precio(new BigDecimal("7.50")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p2).productoSku(p2.getSku()).botica(b2)
            .cantidad(6).precio(new BigDecimal("7.30")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p2).productoSku(p2.getSku()).botica(b3)
            .cantidad(0).precio(new BigDecimal("7.10")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p3).productoSku(p3.getSku()).botica(b1)
            .cantidad(0).precio(new BigDecimal("9.00")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p3).productoSku(p3.getSku()).botica(b2)
            .cantidad(2).precio(new BigDecimal("9.40")).build());
        stockRepo.save(pe.edu.utp.boticaapp.entity.Stock.builder()
            .producto(p3).productoSku(p3.getSku()).botica(b3)
            .cantidad(4).precio(new BigDecimal("9.10")).build());
      }
      if(userRepo.count()==0){
        userRepo.save(Usuario.builder().email("admin@utp.pe").passwordHash(enc.encode("admin123")).nombres("Admin").rol(Usuario.Rol.ADMIN).build());
        userRepo.save(Usuario.builder().email("cliente@utp.pe").passwordHash(enc.encode("cliente123")).nombres("Cliente").rol(Usuario.Rol.CLIENTE).build());
      }
    };
  }
}
