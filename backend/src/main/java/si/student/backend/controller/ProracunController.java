package si.student.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import si.student.backend.services.Proracun;
import si.student.backend.services.ProracunPregled;
import si.student.backend.services.ProracunService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api/proracuni")
public class ProracunController {

    private final ProracunService service;

    public ProracunController(ProracunService service) {
        this.service = service;
    }

    // Pridobi vse proračune skupaj s porabo in opozorili
    @GetMapping
    public List<ProracunPregled> vsiProracuni() {
        return service.pridobiPregledProracunov();
    }

    // Ustvari nov proračun
    @PostMapping
    public Proracun dodajProracun(@RequestBody Proracun proracun) {
        proracun.setId(null);

        try {
            return service.ustvariProracun(proracun);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    // Uredi obstoječi proračun
    @PutMapping("/{id}")
    public Proracun posodobiProracun(
            @PathVariable Long id,
            @RequestBody Proracun podatki) {

        try {
            return service.urediProracun(id, podatki);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, e.getMessage()
            );
        }
    }

    // Izbriši proračun
    @DeleteMapping("/{id}")
    public void izbrisiProracun(@PathVariable Long id) {

        try {
            service.izbrisiProracun(id);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, e.getMessage()
            );
        }
    }
}