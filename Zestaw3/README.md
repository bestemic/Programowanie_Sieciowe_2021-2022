# **Programowanie sieciowe** <br/> **Zestaw 3**
<br>
<div style="text-align: right"><b>Przemysław Pawlik</b></div>

## **1.**
**(nieobowiązkowe, bo zajęcia są zdalne)**
<br>
 Dokończ pisanie par klient-serwer dla TCP/IPv4 oraz UDP/IPv4 (co razem daje cztery programy). Przetestuj czy działają poprawnie gdy klient i serwer są uruchomione na dwóch różnych komputerach w SPK. Wymaga to znajomości adresu IP przydzielonego komputerowi, na którym uruchamiany jest serwer — można go znaleźć w wynikach polecenia `ip address show`.

----------
<br>

## **2.**
**(nieobowiązkowe, bo zajęcia są zdalne)** 
<br>
Sprawdź co się dzieje, gdy podasz zły adres IP albo zły numer portu serwera. Czy jądro systemu operacyjnego daje nam w jakiś sposób o tym znać? Jeśli tak, to jak długo trzeba czekać, aż jądro poinformuje nasz proces o wystąpieniu błędu?

>Pamiętaj, że protokoły sieciowe z korekcją błędów wykonują wielokrotne retransmisje pakietów w zwiększających się odstępach czasu. Może to zająć nawet kilkadziesiąt minut. Nie pomyl sytuacji „proces zawiesza się na pięć minut zanim jądro zwróci -1” z sytuacją „zawiesza się na stałe”.

Jeśli któryś z klientów może się zawiesić czekając w nieskończoność na odpowiedź z nieistniejącego serwera, to popraw jego kod aby tego nie robił. W slajdach z wykładu są pokazane funkcje, które pozwalają na wykonywanie operacji we-wy z timeoutem (możecie go Państwo ustawić np. na 10 sekund).

----------
<br>

## **3.**
Przeanalizuj niniejszą specyfikację protokołu sumowania liczb *(uwaga, nie jest ona taka jak w przykładzie z automatem na wykładzie!)*. Czy jest ona jednoznaczna, czy też może zostawia pewne rzeczy niedopowiedziane?

Komunikacja pomiędzy klientem a serwerem odbywa się przy pomocy datagramów. Klient wysyła datagram zawierający liczby, serwer odpowiada datagramem zawierającym pojedynczą liczbę (obliczoną sumę) bądź komunikat o błędzie.

Zawartość datagramów interpretujemy jako tekst w ASCII. Ciągi cyfr ASCII interpretujemy jako liczby dziesiętne. Datagram może zawierać albo cyfry i spacje, albo pięć znaków składających się na słowo „ERROR”; żadne inne znaki nie są dozwolone (ale patrz następny akapit).

Aby ułatwić ręczne testowanie serwera przy pomocy ncat, serwer może również akceptować datagramy mające na końcu dodatkowy znak \n (czyli bajt o wartości 13) albo dwa znaki \r\n (bajty 10, 13). Serwer może wtedy, ale nie musi, dodać \r\n do zwracanej odpowiedzi.

----------
<br>

## **4.**
Napisz serwer UDP/IPv4 nasłuchujący na porcie nr `2020` i implementujący powyższy protokół.

Serwer musi weryfikować odebrane dane i zwracać komunikat o błędzie jeśli są one nieprawidłowe w sensie zgodności ze specyfikacją protokołu.

W kodzie używaj zmiennych roboczych któregoś ze standardowych typów całkowitoliczbowych (`int`, `unsigned long int`, `uint32_t`, itd.). Co za tym idzie, odebrany ciąg cyfr będzie mógł reprezentować liczbę zbyt dużą, aby dało się ją zapisać w zmiennej wybranego typu. Podobnie może się zdarzyć, że podczas dodawania wystąpi przepełnienie (ang. overflow). Serwer ma obowiązek wykrywać takie sytuacje i zwracać błąd. Uwadze Państwa polecam pliki nagłówkowe `limits.h` oraz `stdint.h`, w których znaleźć można m.in. stałą `INT_MAX` oraz stałą `UINT32_MAX`.

----------
<br>

