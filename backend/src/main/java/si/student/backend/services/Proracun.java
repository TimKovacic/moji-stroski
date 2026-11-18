package si.student.backend.services;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "proracuni")
public class Proracun {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String kategorija;

    private BigDecimal znesek;

    private LocalDate datumZacetka;

    private LocalDate datumKonca;

    public Proracun() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKategorija() {
        return kategorija;
    }

    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    public BigDecimal getZnesek() {
        return znesek;
    }

    public void setZnesek(BigDecimal znesek) {
        this.znesek = znesek;
    }

    public LocalDate getDatumZacetka() {
        return datumZacetka;
    }

    public void setDatumZacetka(LocalDate datumZacetka) {
        this.datumZacetka = datumZacetka;
    }

    public LocalDate getDatumKonca() {
        return datumKonca;
    }

    public void setDatumKonca(LocalDate datumKonca) {
        this.datumKonca = datumKonca;
    }
}
