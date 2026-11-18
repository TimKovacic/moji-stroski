package si.student.backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProracunPregled {

    private Long id;
    private String kategorija;
    private BigDecimal znesek;
    private LocalDate datumZacetka;
    private LocalDate datumKonca;

    private BigDecimal poraba;
    private BigDecimal odstotekPorabe;
    private String opozorilo;

    public ProracunPregled(Proracun proracun,
                           BigDecimal poraba,
                           BigDecimal odstotekPorabe,
                           String opozorilo) {

        this.id = proracun.getId();
        this.kategorija = proracun.getKategorija();
        this.znesek = proracun.getZnesek();
        this.datumZacetka = proracun.getDatumZacetka();
        this.datumKonca = proracun.getDatumKonca();

        this.poraba = poraba;
        this.odstotekPorabe = odstotekPorabe;
        this.opozorilo = opozorilo;
    }

    public Long getId() {
        return id;
    }

    public String getKategorija() {
        return kategorija;
    }

    public BigDecimal getZnesek() {
        return znesek;
    }

    public LocalDate getDatumZacetka() {
        return datumZacetka;
    }

    public LocalDate getDatumKonca() {
        return datumKonca;
    }

    public BigDecimal getPoraba() {
        return poraba;
    }

    public BigDecimal getOdstotekPorabe() {
        return odstotekPorabe;
    }

    public String getOpozorilo() {
        return opozorilo;
    }
}