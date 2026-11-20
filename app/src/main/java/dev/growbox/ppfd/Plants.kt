package dev.growbox.ppfd

/** Docelowe PPFD w µmol/m²/s na wysokości liści. Wartości orientacyjne z poradników hobbystycznych. */
data class Plant(
    val name: String,
    val latin: String,
    val minPpfd: Int,
    val maxPpfd: Int,
    val hours: Int,
    val care: List<Pair<String, String>>,
)

val PLANTS = listOf(
    Plant("Monstera", "Monstera deliciosa", 150, 300, 12, listOf(
        "Światło" to "Jasne, rozproszone. Poniżej ok. 100 PPFD rośnie słabo, a liście są małe i nie mają nacięć. Nacięcia pojawiają się od 150.",
        "Woda" to "Gdy wierzchnie 3–5 cm ziemi przeschnie (co 7–10 dni). Nigdy nie zostawiaj wody w osłonce.",
        "Powietrze" to "Wilgotność 50%+, ale zniesie zwykły pokój. Temperatura 18–29 °C.",
        "Ziemia i nawóz" to "Przepuszczalne podłoże (kora, perlit, kokos). Nawóz co 2–4 tygodnie w sezonie, w połowie dawki.",
        "Wskazówki" to "Daj palik z mchem. Korzenie powietrzne są normalne. Przecieraj liście z kurzu.",
    )),
    Plant("Epipremnum (pothos)", "Epipremnum aureum", 100, 250, 12, listOf(
        "Światło" to "Zniesie cień, ale pstre liście blakną poniżej ok. 75 PPFD. Najlepiej 100–250.",
        "Woda" to "Gdy wierzchnie 2–3 cm przeschnie. Przelanie (żółte liście, miękkie pędy) to główny zabójca.",
        "Powietrze" to "Zwykła wilgotność. Temperatura 15–30 °C.",
        "Wskazówki" to "Rozmnażanie: odetnij pęd pod węzłem i ukorzeń w wodzie (2–3 tygodnie). Trujący dla kotów i psów.",
    )),
    Plant("Filodendron", "Philodendron hederaceum", 100, 250, 12, listOf(
        "Światło" to "Jasne, rozproszone. Odmiany pstre potrzebują więcej.",
        "Woda" to "Gdy wierzchnie 3 cm przeschnie.",
        "Powietrze" to "Wilgotność 40–60%, temperatura 16–29 °C.",
        "Wskazówki" to "Nawóz raz w miesiącu wiosną i latem. Przycinaj końcówki, żeby był gęstszy.",
    )),
    Plant("Fikus sprężysty", "Ficus elastica", 150, 300, 12, listOf(
        "Światło" to "Jasne. Przy braku światła zrzuca dolne liście. Nie lubi przestawiania.",
        "Woda" to "Gdy wierzchnie 5 cm przeschnie, zimą rzadziej.",
        "Powietrze" to "Temperatura 16–28 °C.",
        "Wskazówki" to "Przecieraj duże liście. Sok drażni skórę.",
    )),
    Plant("Fikus lirolistny", "Ficus lyrata", 200, 400, 12, listOf(
        "Światło" to "Potrzebuje dużo, to najbardziej kapryśna roślina na liście. Poniżej 150 PPFD zrzuca liście.",
        "Woda" to "Równomiernie, gdy wierzchnie 5 cm przeschnie. Nigdy w błocie.",
        "Powietrze" to "Wilgotność 40%+, temperatura 18–27 °C. Unikaj przeciągów.",
        "Wskazówki" to "Obracaj co tydzień. Nie przestawiaj bez potrzeby.",
    )),
    Plant("Sansewieria", "Dracaena trifasciata", 50, 200, 10, listOf(
        "Światło" to "Bardzo tolerancyjna. Poniżej 50 PPFD prawie nie rośnie.",
        "Woda" to "Dopiero gdy ziemia całkiem wyschnie (co 2–4 tygodnie, zimą rzadko). Jedyne realne ryzyko to gnicie od przelania.",
        "Ziemia" to "Lekka, szybko przepuszczalna. Temperatura 12–32 °C.",
    )),
    Plant("Zamiokulkas", "Zamioculcas zamiifolia", 50, 150, 10, listOf(
        "Światło" to "Małe do średniego. Mocne słońce przypala.",
        "Woda" to "Co 2–3 tygodnie, po całkowitym przeschnięciu (kłącza magazynują wodę).",
        "Wskazówki" to "Prawie nie do zabicia. Żółknięcie oznacza za dużo wody. Trujący.",
    )),
    Plant("Skrzydłokwiat", "Spathiphyllum", 75, 150, 12, listOf(
        "Światło" to "Średnie. Gdy mu brakuje wody, mocno oklapuje. To sygnał do podlania.",
        "Woda" to "Gdy wierzchnie 2 cm przeschnie, najlepiej miękką wodą (brązowe końcówki to sole).",
        "Powietrze" to "Wilgotność 50%+, temperatura 18–28 °C. Kwitnie tylko przy odpowiednim świetle.",
    )),
    Plant("Kalatea / maranta", "Goeppertia / Maranta", 75, 150, 12, listOf(
        "Światło" to "Średnie, bez bezpośredniego słońca (liście blakną i się zwijają).",
        "Woda" to "Lekko wilgotne podłoże, miękka lub filtrowana woda.",
        "Powietrze" to "Wilgotność 60%+ (growbox jej to daje), temperatura 18–27 °C.",
    )),
    Plant("Zielistka", "Chlorophytum comosum", 100, 250, 12, listOf(
        "Światło" to "Jasne, rozproszone. Brązowe końcówki to sole w wodzie lub suche powietrze.",
        "Woda" to "Gdy wierzchnie 2–3 cm przeschnie. Temperatura 10–27 °C.",
        "Wskazówki" to "Robi rozsady na rozłogach. Posadź je w osobnych doniczkach.",
    )),
    Plant("Aloes / sukulenty", "Aloe vera, Haworthia", 300, 600, 12, listOf(
        "Światło" to "Dużo. Wyciąganie się roślin oznacza za mało światła.",
        "Woda" to "Zalej porządnie, potem pozwól całkiem przeschnąć (co 2–3 tygodnie).",
        "Ziemia" to "Mineralna, przepuszczalna. Temperatura 10–30 °C.",
    )),
)
