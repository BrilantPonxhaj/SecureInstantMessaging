# Secure Instant Messaging Protocol

## Universiteti
Universiteti i Prishtinës "Hasan Prishtina"

## Fakulteti
Fakulteti i inxhinierisë elektrike dhe kompjuterike
Programi: Inxhinieri kompjuterike dhe softuerike

## Titulli i Projektit
Secure Instant Messaging Protocol

## Studentët të cilët kanë punuar këtë projekt:
- Brikenda Zogaj  [@Brikenda-Zogaj](https://github.com/Brikenda-Zogaj)
- Brilant Ponxhaj [@BrilantPonxhaj](https://github.com/BrilantPonxhaj)
- Brineta Uka     [@BrinetaUka](https://github.com/BrinetaUka)
- Çlirimtar Çitaku[@clirimtarcitaku](https://github.com/clirimtarcitaku)

## Asistenti i Lëndës
Mërgim Hoti

## Përshkrimi i Projektit
Projekti "Secure Instant Messaging" synon të dizajnojë dhe zbatojë një protokoll të sigurt të mesazheve të çastit që ofron enkriptimin nga skaji në skaj (end-to-end) dhe fshehësi përparë (forward secrecy). 
Ky protokoll është zhvilluar në një gjuhë programuese të përshtatshme për qëllimet e sigurisë dhe efikasitetit, duke garantuar që komunikimet midis përdoruesve të jenë të sigurta dhe të mbrojtura nga qasje të paautorizuara.

# Ideja e Projektit
Ideja kryesore e projektit është të krijojë një aplikacion të sigurt për mesazhe të çastit që siguron privatësinë dhe sigurinë e komunikimit midis përdoruesve. 
Duke u përqendruar në enkriptimin nga skaji në skaj (end-to-end encryption) dhe fshehësinë përparë (forward secrecy), aplikacioni do të sigurojë që vetëm dërguesi dhe marrësi i autorizuar mund të lexojnë përmbajtjen e mesazheve.

Për të nisur projektin në pajisjen tuaj, ndiqni këto hapa:

### Kërkesat paraprake

Para se të filloni, sigurohuni që keni plotësuar kërkesat e mëposhtme:
- Instaloni Java Development Kit (JDK) në sistemin tuaj.
- Çdo IDE që mbështet Java (p.sh., IntelliJ IDEA, Eclipse).
- Biblioteka JavaFX për aplikacionin GUI të klientit.

### Instalimi

1. Klononi repository-n:
    ```sh
    git clone https://github.com/BrilantPonxhaj/SecureInstantMessaging.git
    cd SIMP-Secure-Chat
    ```

2. Hapni projektin në IDE-n tuaj për Java.

3. Sigurohuni që JavaFX është konfiguruar si duhet në IDE-n tuaj për aplikacionin GUI.

### Nisja e Serverit

Për të nisur serverin, ndiqni këto hapa në IDE-n tuaj:

1. Shkoni te klasa `SIMPServer` në paketën `app`.
2. Ekzekutoni klasën `SIMPServer`.
3. Serveri do të fillojë dhe do të dëgjojë për lidhjet e klientëve në portin `6868`.

### Nisja e Klientit

Për të nisur aplikacionin GUI të klientit, ndiqni këto hapa:

1. Shkoni te klasa `SIMPGUI` në paketën `app`.
2. Ekzekutoni klasën `SIMPGUI`.
3. Kur t'ju kërkohet, shkruani emrin tuaj për t'u bashkuar me chat-in.
4. Klienti do të përpiqet të lidhet me serverin në adresën IP të specifikuar (`192.168.1.9`) dhe portin (`6868`).

### Konfigurimi

Nëse duhet të ndryshoni adresën IP të serverit ose portin:

1. Hapni klasën `SIMPGUI`.
2. Modifikoni konstantet `HOST` dhe `PORT` në fillim të klasës për të reflektuar adresën dhe portin e duhur të serverit.

### Testimi

Për të testuar aplikacionin:

1. Nisni `SIMPServer`.
2. Nisni disa instance të klientit `SIMPGUI`.
3. Dërgoni mesazhe nga një klient; mesazhet duhet të transmetohen te të gjithë klientët e lidhur.

## Rezultatet

Pas konfigurimit dhe ekzekutimit të suksesshëm, përdoruesit duhet të jenë në gjendje të:

- Lidhen me serverin.
- Shkëmbëjnë mesazhe në mënyrë të sigurt me klientët e tjerë të lidhur.
- Shikojnë mesazhet e tyre dhe të tjerëve të shfaqura në GUI.

### Shembull

- Kur një klient dërgon "Përshëndetje!", klientët e tjerë do të shohin "username: Përshëndetje!" në zonën e mesazheve të tyre.

## Pritshmëritë e Ardhshme

Në të ardhmen, planifikojmë të:

- Shtojmë më shumë funksione, si kohën e mesazheve dhe statuset e përdoruesve.
- Përmirësojmë ndërfaqen grafike për një përvojë më të mirë përdoruesi.
- Zbatojmë më shumë metoda enkriptimi dhe veçori sigurie.