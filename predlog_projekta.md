# Univerzitet u Novom Sadu

## Fakultet tehničkih nauka

### Predlog projekta

## Sistem za detekciju stresa i prevenciju burnouta kod softverskih inženjera

**Student:** Milan Arežina, SV55/2021

**Predmet:** Sistemi bazirani na znanju

---

## Motivacija

Softverski inženjeri često rade duge sate, multitasking i noću, što dovodi do visokog stresa i rizika od burnouta. Trenutna rešenja uglavnom prate samo radno vreme ili samoprocenu stresa, ali ne analiziraju uzroke i dugoročne obrasce.

Cilj projekta je kreiranje sistema koji:

* Automatski detektuje trenutni nivo stresa (`stress_level`)
* Generiše akcije za trenutno smanjenje stresa
* Analizira uzroke stresa i predlaže preventivne mere
* Praćenjem događaja korisnika (CEP) prepoznaje dugoročne obrasce visokog stresa

---

## Pregled problema

* Postojeća rešenja fokusirana su na trenutni stres, ne analiziraju kombinacije faktora ili dugoročne obrasce.
* Naše rešenje kombinuje **forward chaining**, **backward chaining** i **CEP** za sveobuhvatnu detekciju i prevenciju stresa.

---

## Ulazi u sistem

| Ulaz             | Način prikupljanja | Opis                                                   |
| ---------------- | ------------------ | ------------------------------------------------------ |
| `current_hours`  | Softverski agent   | Broj sati kontinuiranog rada (računato iz WORK\_EVENT) |
| `current_tasks`  | Task tracker       | Broj aktivnih zadataka, uz intenzitet svakog           |
| `time`           | Softverski agent   | Trenutno vreme (DAY/NIGHT)                             |
| `self_report`    | Kratka anketa      | Samoprocena stresa (1–5)                               |
| `work_intensity` | CEP agregacija     | Ukupna intenzivnost rada na svim zadacima              |
| `day_type`       | Kalendar           | Radni dan ili vikend                                   |

---

## Forward chaining – detekcija trenutnog stresa

**Cilj:** Odrediti `stress_level` i predložiti trenutne akcije.

### Pravila

1. **Trajanje rada (`current_hours`):**

   * 0–2h → +1 poen
   * 2–5h → +2 poena
   * > 5h → +3 poena

2. **Noćni rad (`time`):**

   * NIGHT → `current_hours * 1.5`

3. **Vikend rad (`day_type`):**

   * Vikend → +2 poena

4. **Multitasking (`current_tasks`):**

   * 3–5 zadataka → +1 poen
   * > 5 zadataka → +2 poena

5. **Self-report (`self_report`):**

   * 1–5 → poeni jednaki oceni

6. **Work intensity (`work_intensity`):**

   * direktno dodaje u `stress_score`

7. **Interakcije:**

   * `current_hours>5` **i** `current_tasks>5` → +2 dodatna poena
   * `night_hours>2` i `self_report≤2` → +1 dodatni poen

### Pragovi stress\_level

| Score | Nivo stresa |
| ----- | ----------- |
| 0–5   | Nizak       |
| 6–10  | Srednji     |
| 11–15 | Visok       |
| ≥16   | Kritičan    |

### Output forward chaininga

* `stress_level`
* Predložene akcije: pauza 15–30min, fokus period 1–2h, redistribucija zadataka, kratka šetnja

---

## Backward chaining – analiza uzroka i simptoma

**Cilj:** Odrediti simptome i uzroke stresa.

### Simptomi i lančana pravila

1. **Burnout:**

   ```
   Cilj: Burnout
   - stress_level ≥ Visok
     - work_intensity često visok (CEP)
       - pauze < 2h ukupno dnevno
         - night_hours > 2h više puta u nedelji
           => Burnout rizik
   ```

2. **Hronični umor:**

   ```
   Cilj: Hronični umor
   - stress_level ≥ Srednji
     - current_hours > 4h više dana zaredom
       - break_count < 2 dnevno
         => Hronični umor prisutan
   ```

3. **Smanjen fokus:**

   ```
   Cilj: Smanjen fokus
   - self_report ≤ 2 često
     - multitasking > 5
       => Fokus rizik prisutan
   ```

**Lančani tok:**
Cilj → pitanje 3 → pitanje 2 → pitanje 1 → inputi → potvrda simptoma

**Output backward chaininga:**

* Lista simptoma
* Preporuke za prevenciju: planiranje radnih blokova, pauze, mentor/tim razgovor, reorganizacija zadataka

---

## CEP – detekcija dugoročnih obrazaca

**Događaji:**

| Event        | Opis                                           |
| ------------ | ---------------------------------------------- |
| WORK\_EVENT  | Vreme početka i završetka zadatka + intenzitet |
| BREAK\_EVENT | Vreme početka i završetka pauze                |

### Analiza trendova i agregacija

CEP omogućava sistemu da detektuje dugoročne obrasce i trendove korisničkog rada, koji se koriste i u forward i u backward chainingu.

**Trendovi:**

1. **Ukupno vreme rada:** identifikuje duge periode kontinuiranog rada i njihovu frekvenciju
2. **Pauze:** dužina i učestalost pauza, prekidi između zadataka
3. **Noćni rad i vikend rad:** učestalost rada u rizičnim periodima
4. **Multitasking:** broj simultanih zadataka i njihova intenzivnost
5. **Work intensity:** kumulativna vrednost intenziteta rada tokom dana/nedelje

**Korišćenje trendova:**

* Ako korisnik često radi duže periode bez pauze → signal za visok stress\_level u forward chainingu
* Ako su multitasking i work\_intensity konstantno visoki više dana zaredom → signal za potencijalni burnout u backward chainingu
* Praćenje noćnog rada i vikenda omogućava personalizovane preporuke za prevenciju

---

## Primer rezonovanja

**Input:**

```text
current_hours = 6
current_tasks = 6
time = NIGHT
self_report = 2
work_intensity = 7
break_count = 1
day_type = radni dan
```

**Forward chaining:**

* Poeni: 6h → +3, 6 tasks → +2, NIGHT → \*1.5 → +1.5, self\_report=2 → +2, work\_intensity=7 → +7
* Interakcije: +2 dodatna poena
* Ukupno: 16.5 → **stress\_level = Kritična**
* Predložene akcije: pauza 30min, fokus period 2h, redistribucija zadataka

**CEP:**

* High intensity noćni rad više dana → signal za backward chaining

**Backward chaining:**

* Cilj: Burnout

  * stress\_level ≥ Visok → zadovoljen
  * work\_intensity često visok → zadovoljen
  * pauze <2h → zadovoljen
  * night\_hours >2h više puta → zadovoljen
* Zaključak: Burnout rizik prisutan
* Preventivne preporuke: reorganizacija zadataka, planirani radni blokovi, razgovor sa mentorom
