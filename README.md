# ReisenderChallenge

Das "traveler salesman problem" (Handelsreisender Problem) gehört in die Gruppe der
NP-Problemen. Das heißt um die beste Lösung zu finden, müsste man alle Lösungen
ausprobieren. Das ist aber aufgrund der immensen Größe an möglichen Lösungen
schier unmöglich (mit derzeitiger Technik). Man kann jedoch annäherungsweise
ein gutes, bzw. sehr gutes Ergebnis erzielen.

# Das Problem:
Stellt euch vor ihr wollt 10 Städte bereisen. In welcher Reihenfolge ihr
die Städte besucht ist euch egal; Hauptsache ihr wart in jeder Stadt einmal.
Um Sprittgeld zu sparen wollt ihr die kürzeste Strecke fahren und doch
alle Städte besuchen. Welche Route müsst ihr dafür nehmen?

Die Städte werden im Programm durch zufällige Punkte repräsentiert.
Die Route verfasst man durch einzelne Linien zwischen diesen Punkten.
In der Klasse Reisender innerhalb der Funktion berechneRoute() wird die Route aufgestellt.
Dort sollt ihr eure Lösung erstellen.

# Der Ablauf:
Damit der Vergleich zwischen den einzelnen Teilnehmern vergleichbar bleibt,
werden Zufallszahlen mit einem Seed erzeugt.
Damit werden 10 unterschiedliche Städtekonstellationen erzeugt.
Diese sind wie folgt aufgeteilt:
- 6x 16 Städte
- 3x 40 Städte
- 1x 209 Städte

Für die Berechnung der Route existiert ein Zeitlimit. Dieses wird wie folgt
berechnet:

Zeitlimit = Städteanzahl * Städteanzahl * 6 Millisekunden

Das Berechnen aller Routen darf somit maximal 5 Minuten dauern. 
Beim Überschreiten des Zeitlimits wird das Ergebnis nicht gezählt.
Nach der Deadline werden alle eingereichten Lösungen auf dem gleichen Computer
mit dem gleichen Startseed getestet. Sollten das Ergebnis knapp ausfallen, so wird
mit weiteren Seeds ein Durchschnitt errechnet um den endgültigen Gewinner zu
ermitteln.

# Der Score:
Eure abgegebene Lösung wird mit einem Score bewertet.
Der Score setzt sich aus zwei Faktoren zusammen:
1. Wie gut ist euer gefundener Weg gegenüber einem zufällig Gewählten?
2. Wie viel Zeit von eurer Rechenzeit ist übrig.

Generell gilt:
1. Ein besserer Pfad wird besser bewertet, als wenig Rechenzeit verbraucht.
2. Je mehr Städte eine Route beinhaltet, desto mehr Punkte können erreicht werden.

Die genaue Formel findet ihr im Source Code.
Sollte nicht jede Stadt besucht, keine zusammenhängende Route gefunden oder eure Rechenzeit
überschritten sein, so wird diese Route mit 0 bewertet.
Der Score aller 10 Routen wird aufsummiert und bildet euren Endscore.

# Die Deadline:
Lösungen können bis zum 30.09.2018 23:59:59.9999 abgegeben werden.

# Der Gewinn:
1. Platz: Raspberry Pi Kit
2. Platz: USB Stick 32GB
3. Platz: USB Stick 8GB

und für Teilnehmer, die besser sind als der Zufall einen Sticker dieser Challenge.

Preise können nur an Studenten der HSMW vergeben werden.

Vielen Dank an die StuRa der HSMW für das Sponsoring der Preise.

# Regeln
0. Bestehende Funktionen dürfen nicht verändert werden.
1. Ausnahme von Regel 1 ist die Funktion berechneRoute() der Klasse Reisender.
2. Funktionen und Klassen dürfen nach Belieben hinzugefügt werden.
3. Die Abgabe erfolgt ausschließlich als Fork über github. Bitte erzeugt auch gleich eine jar-Datei.
4. Strengt euch selber an. Eine Kopie einer bestehenden Lösung als Abgabe führt zwangsläufig zur Disqualifikation.
5. Die Liste der Städte darf nicht verändert werden.

# Tipps
0. Nutzt die euch zur Verfügung gestellte Zeit. Eine bessere Lösung erhöht den Score wesentlich stärker, als verbleibende freie Zeit.
1. Die Testmaschine besitzt mehrere Prozessorkerne. Nutzt die Rechenleistung.
2. Optimiert wo es geht. Vielleicht genügt eine grobe Approximation von sehr komplexen und häufig aufgerufenen Berechnungen. Oft hilft es auch häufig berechnete Ergebnisse zu speichern.
3. Variiert den Startpunkt. Die Route muss nicht bei der ersten Stadt beginnen. Vielleicht ist eine andere Stadt als Startpunkt besser.
4. Nutzt bereits existierendes Wissen. Wenn ihr nach Verbesserungen für eure Berechnung sucht findet ihr in der Literatur sicherlich Hilfe oder Algorithmen.
5. Kommentiert schwer zu verstehende Source-Code-Passagen. Es hilft euch später.
6. Berechnet mit weniger Städten die optimale Lösung und schaut, wie weit eure Lösung von dieser entfernt ist.
7. Schaut euch die graphische Ausgabe eurer Route an. Wo stellt ihr Verbesserungsmöglichkeiten fest?
8. Experimentiert. Neue Ideen kommen schnell zusammen. Probiert sie aus, passt sie an, optimiert sie, aber ärgert euch nicht, falls ihr gescheiterte Experimente verwerft. Das gewonnene Wissen bringt euch immer weiter an Ziel.
