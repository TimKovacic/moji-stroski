package si.student.backend.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

import si.student.backend.repository.ProracunRepository;
import si.student.backend.repository.StrosekRepository;

@Service //razred s poslovno logiko
public class ProracunService {

    private final ProracunRepository proracunRepository;
    private final StrosekRepository strosekRepository;

    @Autowired
    public ProracunService(ProracunRepository proracunRepository,
                           StrosekRepository strosekRepository) {
        this.proracunRepository = proracunRepository;
        this.strosekRepository = strosekRepository;
    }

    private void preveriPodatke(Proracun proracun) {

        if (proracun.getKategorija() == null ||
                proracun.getKategorija().isBlank()) {
            throw new IllegalArgumentException("Kategorija je obvezna.");
        }

        if (proracun.getZnesek() == null ||
                proracun.getZnesek().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Znesek proračuna mora biti večji od 0.");
        }

        if (proracun.getDatumZacetka() == null ||
                proracun.getDatumKonca() == null) {
            throw new IllegalArgumentException("Začetni in končni datum sta obvezna.");
        }

        if (proracun.getDatumZacetka().isAfter(proracun.getDatumKonca())) {
            throw new IllegalArgumentException("Začetni datum ne sme biti poznejši od končnega.");
        }
    }

    private void preveriPrekrivanje(Proracun proracun) {

        boolean prekrivanje;

        if (proracun.getId() == null) {
            // Nov proračun
            prekrivanje = proracunRepository
                    .existsByKategorijaAndDatumZacetkaLessThanEqualAndDatumKoncaGreaterThanEqual(
                            proracun.getKategorija(),
                            proracun.getDatumKonca(),
                            proracun.getDatumZacetka()
                    );
        } else {
            // Urejanje obstoječega proračuna
            prekrivanje = proracunRepository
                    .existsByKategorijaAndDatumZacetkaLessThanEqualAndDatumKoncaGreaterThanEqualAndIdNot(
                            proracun.getKategorija(),
                            proracun.getDatumKonca(),
                            proracun.getDatumZacetka(),
                            proracun.getId()
                    );
        }

        if (prekrivanje) {
            throw new IllegalArgumentException(
                    "Za to kategorijo že obstaja proračun v izbranem obdobju."
            );
        }
    }

    public Proracun ustvariProracun(Proracun proracun) {

        preveriPodatke(proracun);
        preveriPrekrivanje(proracun);

        return proracunRepository.save(proracun);
    }

    public List<Proracun> pridobiVseProracune() {
        return proracunRepository.findAll();
    }

    public Proracun urediProracun(Long id, Proracun noviPodatki) {

        Proracun obstojeci = proracunRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Proračun s tem ID-jem ne obstaja."
                ));

        obstojeci.setKategorija(noviPodatki.getKategorija());
        obstojeci.setZnesek(noviPodatki.getZnesek());
        obstojeci.setDatumZacetka(noviPodatki.getDatumZacetka());
        obstojeci.setDatumKonca(noviPodatki.getDatumKonca());

        preveriPodatke(obstojeci);
        preveriPrekrivanje(obstojeci);

        return proracunRepository.save(obstojeci);
    }

    public void izbrisiProracun(Long id) {

        if (!proracunRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Proračun s tem ID-jem ne obstaja."
            );
        }

        proracunRepository.deleteById(id);
    }

    public BigDecimal izracunajPorabo(Proracun proracun) {

        return strosekRepository.izracunajSkupnoPorabo( //podatke o proracunu posljemo v repository kjer se izracuna za doloceno obdobje
                proracun.getKategorija(),
                proracun.getDatumZacetka(),
                proracun.getDatumKonca()
        );
    }

    public BigDecimal izracunajOdstotekPorabe(Proracun proracun) {

        BigDecimal poraba = izracunajPorabo(proracun);

        return poraba
                .multiply(BigDecimal.valueOf(100))
                .divide(proracun.getZnesek(), 2, java.math.RoundingMode.HALF_UP);
    }

    public String pridobiOpozorilo(Proracun proracun) {

        BigDecimal poraba = izracunajPorabo(proracun);
        BigDecimal odstotek = izracunajOdstotekPorabe(proracun);

        if (odstotek.compareTo(BigDecimal.valueOf(100)) >= 0) {

            BigDecimal prekoracitev = poraba.subtract(proracun.getZnesek());

            return "Proračun je presežen! Poraba: " + poraba
                    + " €, prekoračitev: " + prekoracitev + " €.";

        } else if (odstotek.compareTo(BigDecimal.valueOf(80)) >= 0) {

            return "Pozor! Porabili ste že " + odstotek
                    + " % proračuna.";

        }

        return null;
    }

    public List<ProracunPregled> pridobiPregledProracunov() {

        List<Proracun> proracuni = proracunRepository.findAll();
        List<ProracunPregled> pregled = new ArrayList<>();

        for (Proracun proracun : proracuni) {

            BigDecimal poraba = izracunajPorabo(proracun);
            BigDecimal odstotek = izracunajOdstotekPorabe(proracun);
            String opozorilo = pridobiOpozorilo(proracun);

            ProracunPregled podatki = new ProracunPregled(
                    proracun,
                    poraba,
                    odstotek,
                    opozorilo
            );

            pregled.add(podatki);
        }

        return pregled;
    }
}
