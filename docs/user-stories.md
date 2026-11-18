# Uporabniške zgodbe – Moji stroški

## PU-06 – Nastavi proračun po kategoriji

**Odgovorni član:** Tim Kovačič

**Uporabniška zgodba:**

Kot uporabnik želim za izbrano kategorijo stroškov določiti najvišji dovoljeni znesek in časovno obdobje njegove veljavnosti ter spremljati porabo, da lahko nadzorujem svoje izdatke in se izognem preseganju načrtovanega proračuna.

### Kriteriji sprejemljivosti (Acceptance Criteria)

**AC-01 – Nastavitev proračuna**

- **Given:** Uporabnik je v aplikaciji in ima na voljo kategorije stroškov.
- **When:** Izbere kategorijo, vnese veljaven znesek ter začetni in končni datum.
- **Then:** Aplikacija shrani proračun in ga prikaže v pregledu proračunov.

**AC-02 – Preverjanje veljavnosti podatkov**

- **Given:** Uporabnik nastavlja nov proračun.
- **When:** Vnese neveljaven znesek (0, negativno ali neštevilsko vrednost), neveljavno časovno obdobje ali obdobje, ki se prekriva z obstoječim proračunom iste kategorije.
- **Then:** Aplikacija proračuna ne shrani in uporabniku prikaže ustrezno opozorilo.

**AC-03 – Spremljanje porabe in opozorila**

- **Given:** Uporabnik ima nastavljen proračun.
- **When:** Skupna poraba doseže 80 % oziroma 100 % proračuna.
- **Then:** Aplikacija pri 80 % prikaže opozorilo o približevanju omejitvi, pri 100 % ali več pa opozorilo o doseženem ali preseženem proračunu ter trenutno porabo.

**AC-04 – Posodabljanje porabe in novo obdobje**

- **Given:** Uporabnik ima nastavljen proračun in evidentirane stroške.
- **When:** Doda, spremeni ali izbriše strošek, odpre pregled proračuna ali nastavi proračun za novo obdobje.
- **Then:** Aplikacija ponovno izračuna porabo samo za ustrezno časovno obdobje, posodobi opozorila in ohrani zgodovino stroškov.

### Tehnične podrobnosti implementacije (Implementation Details)

**Podatkovna baza:**
- Dodana bo nova tabela `proracuni`.
- Tabela bo vsebovala: `id`, `kategorija`, `znesek`, `datum_zacetka` in `datum_konca`.
- Kategorija bo shranjena kot besedilo, enako kot pri obstoječih stroških.

**Backend (Spring Boot):**
- `Proracun.java` – entiteta za predstavitev proračuna.
- `ProracunRepository.java` – dostop do podatkovne baze.
- `ProracunService.java` – poslovna logika, validacija in izračun porabe.
- `ProracunController.java` – API za upravljanje proračunov.

Backend bo omogočal ustvarjanje, pregledovanje, urejanje in brisanje proračunov. Preverjal bo tudi veljavnost podatkov in preprečeval prekrivanje časovnih obdobij za isto kategorijo. Pri urejanju obstoječega proračuna ne bo upošteval prekrivanja z njim samim.

**Izračun porabe:**

Poraba se bo izračunala kot vsota stroškov izbrane kategorije znotraj začetnega in končnega datuma proračuna. Oba datuma bosta vključena v izračun.

Odstotek porabe:

`(skupna poraba / najvišji dovoljeni znesek) × 100`

**Opozorila:**
- Pri 80 % porabe se prikaže opozorilo o približevanju omejitvi.
- Pri 100 % ali več se prikaže opozorilo o doseženem ali preseženem proračunu.
- Opozorila se izračunajo glede na trenutno porabo in nastavljeni znesek.
- Opozorila bodo prikazana znotraj aplikacije.

**Frontend (React):**
- Dodana bo ločena stran oziroma zavihek »Proračuni«.
- Uporabnik bo lahko ustvarjal, pregledoval, urejal in brisal proračune.
- Prikazani bodo poraba, preostali znesek, časovno obdobje in opozorila.
- Pred brisanjem proračuna bo aplikacija zahtevala potrditev.

**Ohranjanje zgodovine:**

Brisanje ali potek proračuna ne bo izbrisal evidentiranih stroškov. Poraba novega proračuna se bo izračunavala samo na podlagi stroškov znotraj njegovega obdobja.

## PU-07 – Izvozi poročilo o stroških

**Odgovorni član:** Luka Poprask

**Uporabniška zgodba:**
Kot uporabnik želim svoje stroške izvoziti v poročilo PDF, da imam pregled nad njimi tudi zunaj aplikacije.

### Kriteriji sprejemljivosti (Acceptance Criteria)

**AC-01 – Izvoz stroškov v PDF**

- **Given:** V podatkovni bazi je vsaj en strošek.
- **When:** Uporabnik izbere izvoz poročila.
- **Then:** Aplikacija pridobi stroške iz podatkovne baze, pripravi datoteko PDF s podatki o stroških (opis, znesek, kategorija in datum) ter sproži prenos datoteke na uporabnikovo napravo.

**AC-02 – Brez stroškov ni mogoče ustvariti poročila**

- **Given:** V podatkovni bazi ni zabeležen noben strošek.
- **When:** Uporabnik izbere izvoz poročila.
- **Then:** Aplikacija ne ustvari in ne prenese PDF-ja ter uporabnika obvesti, da poročila ni mogoče ustvariti, ker ni zabeleženih stroškov.

**AC-03 – Napaka pri pridobivanju ali prenosu**

- **Given:** Pri pridobivanju stroškov iz podatkovne baze ali pripravi oziroma prenosu PDF-ja pride do napake.
- **When:** Uporabnik poskusi izvoziti poročilo.
- **Then:** Aplikacija ne prikaže nepopolnega poročila in uporabnika obvesti, da izvoz ni uspel.

### Tehnične podrobnosti implementacije (Implementation Details)

**Podatkovna baza:**
- Poročilo uporablja obstoječe stroške iz tabele `strosek`; nova tabela ni potrebna.
- Stroški se pridobijo iz podatkovne baze ob zahtevi za izvoz, da poročilo vsebuje aktualne podatke.

**Backend (Spring Boot):**
- Backend pridobi stroške prek obstoječega `StrosekRepository` in pripravi PDF z opisom, zneskom v EUR, kategorijo in datumom vsakega stroška.
- Če je seznam stroškov prazen, backend ne ustvari datoteke in vrne ustrezen odgovor, ki frontendu omogoča prikaz obvestila.
- Odgovor za uspešen izvoz vrne PDF kot datoteko za prenos. Napake pri dostopu do podatkov ali ustvarjanju PDF-ja se obravnavajo kot neuspešen izvoz.

**Frontend (React):**
- V pregledu stroškov je uporabniku na voljo možnost »Izvozi v PDF«.
- Ob uspešnem odgovoru frontend sproži prenos PDF-ja na uporabnikovo napravo. Če ni stroškov ali izvoz ne uspe, prikaže ustrezno obvestilo.

**Ohranjanje zgodovine:**
- Izvoz ne spreminja ali briše stroškov v podatkovni bazi; PDF je kopija podatkov za ogled zunaj aplikacije.
