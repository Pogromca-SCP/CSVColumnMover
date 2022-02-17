# Instrukcja
## Pobranie programu
Do prawidłowego działania program potrzebuje plików `Przesuwacz.jar` i `polisy.json` umieszczonych w tym samym folderze. Trzeba też mieć zainstalowaną Javę w wersji 8 lub nowszej.
Żeby uruchomić program uruchom plik `Przesuwacz.jar`.

## Dodawanie wsparcia dla nowej polisy
Plik `polisy.json` zawiera wszystkie informacje, których potrzebuje program do przetwarzania polis. Dodawanie wsparcia dla nowych dokumentów jest bardzo łatwe, ponieważ
jest to zwykły plik tekstowy, który można edytować nawet w zwykłym notatniku.

### Podstawy edycji pliku JSON
JSON to specjalny format tekstowy do przechowywania danych. Żeby obsługiwać wsparcie dla polis należy znać kilka zasad/reguł:
- Nowe linie, spacje i inne podobne znaki są ignorowane, więc można ich używać w celu poprawienia czytelności pliku.
- Obiekt służy do przechowywania wielu nazwanych elementów. Zawartość obiektu jest umieszczana w nawiasach klamrowych `{}`. Poszczególne elementy są umieszczane wraz z ich nazwą
`"nazwa elementu": element`. Jeśli elementów jest więcej, niż jeden trzeba je oddzielać przecinkami `{"element1": null, "element2": null}`. 
- Tablica służy do przechowywani wielu elementów bez nadawania im nazwy. Zawartość tablicy jest umieszczana w nawiasach kwadratowych `[]`. Jeśli elementów jest więcej, niż
jeden trzeba je oddzielać przecinkami `["Ania", "ma", "kota"]`.
- Przechowywane napisy trzeba umieszczać w cudzysłowiach `"Przykładowy napis"`.

### Dodawanie polisy
- 1 Do ogólnej tablicy dodaj nowy obiekt.
- 2 W element o nazwie `"name"` wpisz nazwę nowej polisy (nazwa będzie użyta przy wyborze polisy, powinna być łatwo rozponawalna).
- 3 W element o nazwie `"columns"` wstaw nową tablicę. Każdy obiekt w tej tablicy będzie reprezentował nową kolumnę w pliku wyjściowym. Kolejność będzie taka sama!

#### Dla każdej kolumny:
- 1 Dodaj element o nazwie `"name"`. Tutaj wstaw nazwę kolumny w pliku wyjściowym (zawartość pierwszego wiersza w pliku).
- 2 Opcjonalnie możesz dodać element `"from"`. Tutaj możesz dać nazwę kolumny z pliku wejściowego, jeśli program ma skopiować z niej dane (nazwa musi być identyczna!).

### Usuwanie polisy
Aby usunąć wsparcie dla dokumentu, usuń z tablicy obiekt, który go reprezentuje.
