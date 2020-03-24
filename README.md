<img alt="Logo" src="http://coderslab.pl/svg/logo-coderslab.svg" width="400">


# PortfolioLab projekt II
> Kilka ważnych informacji

## Jak zacząć?

1. Stwórz repozytorium dla projektu o dowolnej nazwie.
4. Zainicjuj repozytorium w swoim folderze z projektem (wystarczy folder src).
 Instrukcja krok po kroku zostanie wyświetlona na Github, po utworzeniu pustego repozytorium.
 Opcje z zakładki **Initialize this repository with a README** pozostaw puste.
5. Systematycznie wykonuj operacje commit oraz push.


----------------------------------------------------------------------------------------


#### Opis

Po zaimportowaniu do IDE, należy zmienić dane dostępowe do bazy w pliku **application.properties** znajdującym się w:
 `src/main/resources`

Na określonej w pliku **application.properties** bazie danych wykonaj zapytanie tworzące podstawową rolę w naszym systemie o nazwie **ROLE_USER**;
```sql 
INSERT INTO `role` (`role_id`, `role`) VALUES (NULL, 'ROLE_USER');
```
Po uruchomieniu aplikacji wywołaj akcję: 
`http://localhost:8080/create-user` (akcja ta znajduje się w kontrolerze: HomeController )
utworzy ona użytkownika (admin, admin) - login i hasło.

Ponowne wywołanie spowoduje błąd ze względu na unikalność loginu.
tak utworzony użytkownik może się już zalogować pod adresem: http://localhost:8080/login

Projekt zawiera przykładową Encję **Hero** - po zapoznaniu się z przykładowym kontrolerem, widokami, repozytoriami - można tą Encję oraz powiązane z nią klasy usunąć z projektu.
W kontrolerze mamy przykładową realizację paginacji (nie jest ona wymagana w projekcie), oraz przesłanie komunikatu z użyciem RedirectAttributes - również nie jest wymagane w projekcie

W katalogu webapp/resources znajdują się katalogi data, dist, vendor - są to składowe szablonu opartego na bootstrap (https://startbootstrap.com/template-overviews/sb-admin-2/),
w widokach index.jsp, header.jsp, footer.jsp znajduje się zaimplementowany szablon.


