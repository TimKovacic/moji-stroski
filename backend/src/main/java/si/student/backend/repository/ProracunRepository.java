package si.student.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import si.student.backend.services.Proracun;
import java.time.LocalDate;

public interface ProracunRepository extends JpaRepository<Proracun, Long> {

    boolean existsByKategorijaAndDatumZacetkaLessThanEqualAndDatumKoncaGreaterThanEqual( //Query se samodejno ustvari na podlagi imena - preverjamo ali datumski intervali prekrivajo
            String kategorija,
            LocalDate datumKonca,
            LocalDate datumZacetka
    );

    boolean existsByKategorijaAndDatumZacetkaLessThanEqualAndDatumKoncaGreaterThanEqualAndIdNot( //za preverjanje ze obstojecih
            String kategorija,
            LocalDate datumKonca,
            LocalDate datumZacetka,
            Long id
    );

}
