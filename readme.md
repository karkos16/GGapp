# Gadu-Gadu

## Opis działania + opis protokołu

Serwer inicjalizuje pusty ciąg allMessages, który będzie przechowywał wszystkie wiadomości do wysłania. Wykorzystany został protokół TCP do komunikacji klient-serwer.

Klient, aby uzyskać swój numer, wysyła do serwera o numerze portu 1234 wiadomość z treścią "0000". Serwer odpowiada wiadomością z numerem klienta.
Wówczas klient może już wysyłać wiadomości do serwera, który je dodaje do allMessages.
Aby "dodać do kontaktów", klient wysyła do serwera wiadomość z treścią "0001", a serwer odpowiada wiadomością 1 w przypadku, gdy klient o podanym numerze istnieje, a 0 w przeciwnym wypadku. Wówczas klient może wysyłać wiadomości do innego klienta, który je odbiera.
Do wysyłania wiadomości należy wysłać do serwera wiadomość z treścią "0002", numerem nadawcy i odbiorcy oraz treścią wiadomości. Serwer dodaje wiadomość do wektora z wiadomości dla danej pary. Gdy nie uda się znaleźć pary, to zwracana jest pusta odpowiedź.

W celu uzyskania wiadomości, klient wysyła do serwera wiadomość z treścią "0003" i numerem klienta oraz numerem odbiorcy. Serwer odpowiada wiadomością zawierającą wszystkie wiadomości dla danego klienta. W przypadku, gdy nie ma wiadomości, serwer zwraca pustą wiadomość.

Można też dodawać numery do kontaktów po stronie serwera. W tym celu należy wysłać do serwera wiadomość z treścią "0004" i numerem klienta. Serwer zapisuje wszystkie numery, z którym klient rozmawiał.
## Szkielet wiadomości

```text
odbiorcaklfjaklfsjalkfsjafklsajTreśćkjasdflksajklafjkll\n"
```

## Szkielet dodawania do kontaktów
    
```text
nr_kontaktuoiaiudusj\n
```

## Implementacja funkcji _write i _read

Funkcja _read charakteryzuje się tym, że czyta dopóki nie natknie się na podwójny znak nowej linii charakteryzujący koniec wiadomości. Wówczas zwraca odczytaną wiadomość. Funkcja _write charakteryzuje się tym, że dopisuje do wiadomości dwa razy znak nowej linii.