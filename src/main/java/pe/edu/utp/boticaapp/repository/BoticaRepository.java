package pe.edu.utp.boticaapp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pe.edu.utp.boticaapp.entity.Botica;

public interface BoticaRepository extends JpaRepository<Botica, Long> {
}
