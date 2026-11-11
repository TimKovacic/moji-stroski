package si.student.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import si.student.backend.services.Strosek;

public interface StrosekRepository extends JpaRepository<Strosek, Long> {

    List<Strosek> findByDatumBetween(LocalDate od, LocalDate doDatuma);


    List<Strosek> findByDatumBetweenAndKategorija(LocalDate od, LocalDate doDatuma,
                                                  String kategorija
    );

}// vsebovane funkcije save(),findAll(), findById(), deleteById()