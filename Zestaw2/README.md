# **Programowanie sieciowe** <br/> **Zestaw 2**
<br>
<div style="text-align: right"><b>Przemysław Pawlik</b></div>

## **1.**
Linuksowe dystrybucje zazwyczaj zawierają program netcat (może być też dostępny pod nazwą nc) lub jego ulepszoną wersję, ncat. Pozwala on m.in. nawiązać połączenie ze wskazanym serwerem, a następnie wysyłać do niego znaki wpisywane z klawiatury; odpowiedzi zwracane przez serwer są drukowane na ekranie. Pozwala też uruchomić się w trybie serwera czekającego na połączenie na wskazanym numerze portu.
<br>
<br>
Otwórz dwa okna terminalowe, w pierwszym z nich uruchom

``ncat -v -l 20123``

a w drugim

``ncat -v 127.0.0.1 20123``

>(adres 127.0.0.1 to taki magiczny adres IPv4, który zawsze oznacza lokalny komputer) 

<br>
Jeśli wszystko poszło dobrze i netcaty nawiązały połączenie, to linie wpisywane w jednym z okien powinny pojawiać się w drugim. Aby przerwać działanie netcata użyj kombinacji klawiszy Ctrl-C.
<br>
<br>
Sprawdź co się dzieje, jeśli spróbujesz uruchomić klienta (netcat bez opcji -l) jako pierwszego. Sprawdź, w jaki sposób netcat-klient oraz netcat-serwer reagują, gdy proces na drugim końcu połączenia zostanie zabity przez Ctrl-C. Proszę nie uogólniać tych obserwacji na wszystkie programy korzystające z gniazdek, inne programy mogą się zachowywać trochę inaczej niż netcat.
<br>
<br>

>Uwaga: jeśli pracujesz zdalnie na spk-ssh, to uruchomiony przez Ciebie netcat może wejść w kolizję z netcatami uruchomionymi w tym samym czasie przez innych studentów. Wybierz wtedy numer portu inny niż 20123, ale większy niż 1024.

----------
<br>

## **2.**
Wszystkie wersje netcata domyślnie korzystają z TCP. Trzeba im podać w linii komend opcję -u, aby zamiast gniazdka TCP utworzone zostało gniazdko UDP. Powtórz eksperymenty używając poleceń

``ncat -v -u -l 20123``

``ncat -v -u 127.0.0.1 20123``

Sprawdź co się teraz będzie działo, gdy jeden z działających netcatów zabijesz przez Ctrl-C. Co się zmieniło w porównaniu do eksperymentów z TCP? I czy treść wyświetlanych komunikatów o błędach jest taka sama?

----------
<br>

## **3.**
Przejrzyj dokumentację netcata, upewnij się co do znaczenia opcji `-v`, `-l` oraz `-u`. Sprawdź też co robi opcja `-C`, czyli `--crlf`. W jakich sytuacjach może ona być potrzebna?

----------
<br>

## **4.**
*(nieobowiązkowe)* Jeśli oprócz polecenia ncat dostępna jest również któraś z odmian polecenia nc albo polecenie socat, to sprawdź czy za jego pomocą też da się wykonać powyższe eksperymenty. Może to wymagać zmiany lub dodania opcji w poleceniach uruchamiających serwer i klienta.

----------
<br>

## **5.**
Napisz prosty serwer zwracający wizytówkę. Powinien tworzyć gniazdko TCP nasłuchujące na porcie o numerze podanym jako `argv[1]` (użyj `socket`, `bind` i `listen`), następnie w pętli czekać na przychodzące połączenia (`accept`), wysyłać ciąg bajtów `Hello, world!\r\n` jako swoją wizytówkę, zamykać odebrane połączenie i wracać na początek pętli. Pętla ma działać w nieskończoność, aby przerwać działanie programu trzeba będzie użyć `Ctrl-C`.

----------
<br>

## **6.**
Przetestuj netcatem stworzony serwer.

----------
<br>

## **7.**
Napisz prostego klienta, który łączy się (użyj `socket` i `connect`) z usługą wskazaną argumentami podanymi w linii komend (adres IPv4 w `argv[1]`, numer portu TCP w `argv[2]`), drukuje na ekranie wizytówkę zwróconą przez serwer i kończy pracę. Pamiętaj o zasadzie ograniczonego zaufania i weryfikuj, czy odebrane bajty to na pewno drukowalne znaki ASCII zanim je prześlesz na `stdout`.

----------
<br>

## **8.**
Sprawdź czy program-klient poprawnie współdziała z programem-serwerem.

----------
<br>

## **9.**
Spróbuj napisać parę klient-serwer komunikującą się przy pomocy protokołu UDP. 

>Pamiętaj o tym, że UDP nie nawiązuje połączeń, więc to klient będzie musiał jako pierwszy wysłać jakiś datagram, a serwer na niego odpowie. 

Sprawdź, czy możliwe jest wysyłanie pustych datagramów (tzn. o długości zero bajtów) — jeśli tak, to może niech klient właśnie taki wysyła?

----------
<br>

## **10.**
*(nieobowiązkowe)* Przepisz powyższe rozwiązania w innym języku, np. w Javie lub Pythonie. Porównaj obie wersje i oceń, czy nowy kod jest krótszy i / lub czytelniejszy od starego.

----------
<br>