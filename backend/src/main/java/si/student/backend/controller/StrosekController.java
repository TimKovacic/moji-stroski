package si.student.backend.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import si.student.backend.repository.StrosekRepository;
import si.student.backend.services.Strosek;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173") //Reactu omogoči dostop do API-ja na drugem naslovu
@RequestMapping("/api/stroski") //koncna API tocka: http://localhost:8080/api/stroski
public class StrosekController {

    private final StrosekRepository repository;

    public StrosekController(StrosekRepository repository) {
        this.repository = repository;
    }

    // Pridobi vse stroške
    @GetMapping
    public List<Strosek> vsiStroski() {
        return repository.findAll();
    }

    // Pridobi en strošek po ID-ju
    @GetMapping("/{id}")
    public Strosek enStrosek(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Strošek ni najden"));
    }

    // Dodaj nov strošek
    @PostMapping
    public Strosek dodajStrosek(@RequestBody Strosek strosek) {
        strosek.setId(null); //ker je indeks stroskov autoincrement moremo tu zagotoviti da nima slucajno v naprej dolocenega ID-ja
        return repository.save(strosek);
    }

    // Posodobi obstoječi strošek
    @PutMapping("/{id}")
    public Strosek posodobiStrosek(
            @PathVariable Long id,
            @RequestBody Strosek podatki) {

        Strosek obstojeci = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Strošek ni najden"));

        obstojeci.setOpis(podatki.getOpis());
        obstojeci.setZnesek(podatki.getZnesek());
        obstojeci.setKategorija(podatki.getKategorija());
        obstojeci.setDatum(podatki.getDatum());

        return repository.save(obstojeci);
    }

    // Izbriši strošek
    @DeleteMapping("/{id}")
    public void izbrisiStrosek(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Strošek ni najden");
        }

        repository.deleteById(id);
    }

    //za analizo stroskov po datumu in/ali kategoriji
    @GetMapping("/analiza")
    public Map<String, Object> analizaStroskov(
            @RequestParam("od")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate od,

            @RequestParam("do")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate doDatuma,

            @RequestParam(value = "kategorija", required = false)
            String kategorija) {

        if (od.isAfter(doDatuma)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Začetni datum ne sme biti za končnim datumom"
            );
        }

        List<Strosek> rezultat;

        if (kategorija == null || kategorija.isBlank()
                || kategorija.equals("Vse kategorije")) {
            rezultat = repository.findByDatumBetween(od, doDatuma);
        } else {
            rezultat = repository.findByDatumBetweenAndKategorija(
                    od, doDatuma, kategorija
            );
        }

        BigDecimal skupaj = rezultat.stream()
                .map(Strosek::getZnesek)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return Map.of(
                "stroski", rezultat,
                "skupaj", skupaj
        );
    }

    //za izvoz poročila v obliki pdf datoteke
    @GetMapping("/izvoz/pdf")
    public ResponseEntity<ByteArrayResource> izvoziPdf(
        @RequestParam("od")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate od,

        @RequestParam("do")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate doDatuma,

        @RequestParam(value = "kategorija", required = false)
        String kategorija) {
    if (od.isAfter(doDatuma)) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Začetni datum ne sme biti za končnim datumom."
        );
    }

    List<Strosek> stroski;

    if (kategorija == null || kategorija.isBlank()
            || kategorija.equals("Vse kategorije")) {
        stroski = repository.findByDatumBetween(od, doDatuma);
    } else {
        stroski = repository.findByDatumBetweenAndKategorija(
                od, doDatuma, kategorija
        );
    }

        if (stroski.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Poročila ni mogoče ustvariti, ker ni zabeleženih stroškov na tem profilu."
            );
        }

        try {
            ByteArrayOutputStream izhod = new ByteArrayOutputStream();

            Document dokument = new Document();
            PdfWriter.getInstance(dokument, izhod);

            dokument.open();

            Font naslovnaPisava = FontFactory.getFont(
                    FontFactory.HELVETICA_BOLD, 18
            );

            dokument.add(new Paragraph(
                    "Poročilo o stroških",
                    naslovnaPisava
            ));

            dokument.add(new Paragraph(" "));

            PdfPTable tabela = new PdfPTable(4);
            tabela.setWidthPercentage(100);
            tabela.setWidths(new float[]{3, 2, 2, 2});

            String[] glave = {"Opis", "Kategorija", "Datum", "Znesek"};

            for (String glava : glave) {
                PdfPCell celica = new PdfPCell(new Phrase(glava));
                tabela.addCell(celica);
            }

            for (Strosek strosek : stroski) {
                tabela.addCell(strosek.getOpis());
                tabela.addCell(strosek.getKategorija());
                tabela.addCell(strosek.getDatum().toString());
                tabela.addCell(strosek.getZnesek() + " EUR");
            }

            dokument.add(tabela);
            dokument.close();

            ByteArrayResource pdf = new ByteArrayResource(izhod.toByteArray());

            return ResponseEntity.ok().header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=porocilo-o-stroskih.pdf"
            ).contentType(MediaType.APPLICATION_PDF).contentLength(pdf.contentLength()).body(pdf);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, "Poročila ni bilo mogoče ustvariti."
            );
        }
    }
}