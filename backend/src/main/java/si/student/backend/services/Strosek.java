package si.student.backend.services;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity //podatki se bodo shranjevali v bazo
public class Strosek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //enolicna stevilka posameznega stroska

    private String opis;
    private BigDecimal znesek;
    private String kategorija; //za kaj smo imeli ta strosek (npr. hrana, gorivo)
    private LocalDate datum;

    public Strosek() {
    }

    public Strosek(String opis, BigDecimal znesek, String kategorija, LocalDate datum) {
        this.opis = opis;
        this.znesek = znesek;
        this.kategorija = kategorija;
        this.datum = datum;
    }

    public Long getId() {
        return id;
    }

    public String getOpis() {
        return opis;
    }

    public BigDecimal getZnesek() {
        return znesek;
    }

    public String getKategorija() {
        return kategorija;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public void setZnesek(BigDecimal znesek) {
        this.znesek = znesek;
    }

    public void setKategorija(String kategorija) {
        this.kategorija = kategorija;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }
}
