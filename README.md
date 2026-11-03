# Moji stroški

## O projektu

**Moji stroški** je spletna aplikacija za beleženje in pregled osebnih stroškov. Uporabniku omogoča dodajanje, pregledovanje, urejanje in brisanje stroškov ter analizo porabe glede na izbrano časovno obdobje in kategorijo.

Aplikacija je sestavljena iz dveh glavnih delov:
- **Frontend:** uporabniški vmesnik, izdelan s knjižnico React.
- **Backend:** strežniški del, izdelan s Spring Boot, ki skrbi za obdelavo zahtev in komunikacijo s podatkovno bazo PostgreSQL.

## Uporabljene tehnologije

### Frontend
- React 19.3.1 – izdelava uporabniškega vmesnika.
- React DOM 19.3.1 – prikaz React komponent.
- Vite 8.3.4 – razvojno okolje in orodje za gradnjo aplikacije.
- Axios 1.20.0 – komunikacija z backend API-jem.
- Node.js 24.11.1 in npm 11.6.2 – izvajanje in upravljanje odvisnosti.

### Backend
- Java 25 – programski jezik.
- Spring Boot 4.1.1 – razvoj strežniškega dela aplikacije.
- Spring Data JPA – dostop do podatkovne baze.
- Spring Web MVC – izdelava REST API-ja.
- Hibernate – preslikava objektov v relacijsko podatkovno bazo.
- PostgreSQL 18 – relacijska podatkovna baza.
- Springdoc OpenAPI 3.1.1 – dokumentacija in preizkušanje API-ja.
- Maven Wrapper – upravljanje in gradnja projekta.

### Ostala orodja
- IntelliJ IDEA – razvojno okolje.
- Docker Desktop in Docker Compose – zagon PostgreSQL podatkovne baze.
- DataGrip – pregledovanje in upravljanje podatkovne baze.
- Postman – preizkušanje API zahtev.
- Git in GitHub – upravljanje različic izvorne kode.

## Struktura projekta

Projekt je razdeljen na naslednje glavne mape:

```text
SpringBoot/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/si/student/backend/
│   │   │   └── resources/
│   │   ├── test/
│   │   └── ...
│   ├── pom.xml
│   ├── mvnw
│   └── mvnw.cmd
├── frontend/
│   ├── public/
│   ├── src/
│   │   ├── assets/
│   │   ├── services/
│   │   ├── App.jsx
│   │   ├── App.css
│   │   ├── index.css
│   │   └── main.jsx
│   ├── package.json
│   └── vite.config.js
├── database/
│   ├── docker-compose.yml
│   └── .env.example
├── postman/
│   ├── collections/
│   ├── documents/
│   ├── environments/
│   ├── flows/
│   ├── globals/
│   ├── mocks/
│   └── specs/
├── .gitignore
└── README.md
```

**Opis map:**
- `backend/` – izvorna koda strežniškega dela aplikacije.
- `frontend/` – izvorna koda uporabniškega vmesnika.
- `database/` – konfiguracija za zagon PostgreSQL podatkovne baze z Docker Compose.
- `postman/` – zbirke in konfiguracije za preizkušanje API-ja.

## Namestitev in zagon

### Predpogoji

Za zagon projekta potrebujemo:
- Java JDK 25 ali novejšo združljivo različico.
- Node.js in npm.
- Docker Desktop.
- Git.

### 1. Prenos projekta

Projekt prenesemo iz GitHub repozitorija:

```bash
git clone https://github.com/TimKovacic/moji-stroski.git
```

Nato odpremo mapo projekta.

### 2. Nastavitev in zagon podatkovne baze

V mapi `database/` se nahaja datoteka `docker-compose.yml`.

Najprej ustvarimo lokalno datoteko `.env` iz predloge `.env.example`:

V PowerShellu:

```powershell
Copy-Item .env.example .env
```

V datoteki `.env` nastavimo lastno geslo:

```env
DB_PASSWORD=izbrano_geslo
```

Datoteka `.env` vsebuje lokalne podatke in se ne sme dodajati v Git repozitorij.

Nato v mapi `database/` zaženemo podatkovno bazo:

```bash
docker compose up -d
```

Podatkovna baza uporablja naslednje nastavitve:

| Nastavitev | Vrednost |
|---|---|
| Host | localhost |
| Port | 5433 |
| Database | stroski |
| Username | postgres |
| Password | Vrednost spremenljivke DB_PASSWORD |

### 3. Zagon backenda

V IntelliJ IDEA odpremo projekt v mapi `backend/`.

Pred zagonom nastavimo okoljsko spremenljivko `DB_PASSWORD`, ki mora vsebovati enako geslo kot v datoteki `database/.env`.

Backend zaženemo z zagonom razreda `BackendApplication`.

Lahko pa ga zaženemo tudi prek terminala v mapi `backend/`:

```powershell
.\mvnw.cmd spring-boot:run
```

Backend se zažene na naslovu:

`http://localhost:8080`

API je dostopen na naslovu:

`http://localhost:8080/api/stroski`

Dokumentacija API-ja Swagger UI:

`http://localhost:8080/swagger-ui/index.html`

### 4. Zagon frontenda

V terminalu odpremo mapo `frontend/`:

```bash
cd frontend
```

Namestimo odvisnosti:

```bash
npm install
```

Zaženemo razvojni strežnik:

```bash
npm run dev
```

Frontend je nato dostopen na naslovu:

`http://localhost:5173`

Za pravilno delovanje aplikacije morajo biti zagnani Docker vsebnik s PostgreSQL podatkovno bazo, backend in frontend.

## Navodila za razvijalce

Projekt uporablja Git in GitHub za upravljanje izvorne kode.

### Osnovni potek dela

1. Prenesemo repozitorij:

```bash
git clone https://github.com/TimKovacic/moji-stroski.git
```

2. Pred začetkom dela ustvarimo novo vejo:

```bash
git checkout -b feature/opis-spremembe
```

3. Po opravljenih spremembah preverimo stanje:

```bash
git status
```

4. Dodamo spremembe:

```bash
git add .
```

5. Ustvarimo commit z opisnim sporočilom:

```bash
git commit -m "Opis spremembe"
```

6. Pošljemo vejo na GitHub:

```bash
git push -u origin feature/opis-spremembe
```

7. Na GitHubu ustvarimo Pull Request za združitev sprememb v vejo `main`.

## Standardi kodiranja

Pri razvoju projekta upoštevamo naslednje standarde:

### Java
- Imena razredov zapisujemo v obliki `UpperCamelCase`.
- Imena metod in spremenljivk zapisujemo v obliki `lowerCamelCase`.
- Imena paketov zapisujemo z malimi črkami.
- Uporabljamo opisna imena razredov, metod in spremenljivk.
- Kodo razdelimo v smiselne razrede in metode.

### JavaScript in React
- Komponente React poimenujemo z uporabo `PascalCase`.
- Funkcije in spremenljivke poimenujemo z uporabo `camelCase`.
- API-klice hranimo v mapi `src/services/`.
- Uporabljamo dosledno zamikanje kode.
- Pred oddajo sprememb preverimo kodo z ukazom:

```bash
npm run lint
```

### Splošna pravila
- Spremembe opisujemo z jasnimi in razumljivimi commit sporočili.
- Gesel in drugih občutljivih podatkov ne shranjujemo v Git repozitorij.
- Lokalna konfiguracija se hrani v datoteki `.env`.
- Za konfiguracijo okolja je v repozitoriju na voljo predloga `.env.example`.
- Pred združitvijo sprememb preverimo pravilnost delovanja aplikacije.