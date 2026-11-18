package si.student.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import si.student.backend.services.Strosek;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StrosekRepository extends JpaRepository<Strosek, Long> {

    List<Strosek> findByDatumBetween(LocalDate od, LocalDate doDatuma);


    List<Strosek> findByDatumBetweenAndKategorija(LocalDate od, LocalDate doDatuma,
                                                  String kategorija
    );

    @Query("SELECT COALESCE(SUM(s.znesek), 0) FROM Strosek s " +
            "WHERE s.kategorija = :kategorija " +
            "AND s.datum BETWEEN :od AND :doDatuma")
    BigDecimal izracunajSkupnoPorabo(
            @Param("kategorija") String kategorija,
            @Param("od") LocalDate od,
            @Param("doDatuma") LocalDate doDatuma
    );

}// vsebovane funkcije save(),findAll(), findById(), deleteById()