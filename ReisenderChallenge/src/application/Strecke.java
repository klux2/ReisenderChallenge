package application;

import java.util.ArrayList;
import java.util.Collections;

public class Strecke implements Comparable<Strecke> {
    private ArrayList<Linie> linien;
    private double laenge;

    public Strecke(int anz) {
        linien = new ArrayList<>(anz);
        laenge = 0;
    }

    public Strecke(ArrayList<Linie> linien) {
        this.linien = new ArrayList<>(linien);
        laenge = 0;
    }

    ArrayList<Linie> getLinien() {
        return linien;
    }

    double getLaenge() {
        return laenge;
    }

    void berechneLaenge() {
        laenge = 0;
        if (linien != null) {
            for (Linie linie : linien) {
                laenge += linie.lange();
            }
        }
    }

    void clean() {
        ArrayList<Punkt> anfang = new ArrayList<>(linien.size()), ende = new ArrayList<>(linien.size());
        for (Linie linie : linien) {
            anfang.add(linie.getStart());
            ende.add(linie.getEnde());
        }
        ArrayList<Punkt> temp = new ArrayList<>(ende);
        ende.removeAll(anfang);
        anfang.removeAll(temp);
        Punkt start;
        if (anfang.size() == 0) {
            Linie max = Collections.max(linien);
            start = max.getEnde();
            linien.remove(max);
        } else {
            start = anfang.get(0);
        }
        ArrayList<Linie> l_neu = new ArrayList<>(linien.size());
        while (linien.size() > 0) {
            for (Linie linie : linien) {
                if (linie.getStart().equals(start)) {
                    l_neu.add(linie);
                    linien.remove(linie);
                    start = linie.getEnde();
                    break;
                }
            }
        }
        linien = l_neu;
    }

    @Override
    public int compareTo(Strecke o) {
        if (o != null) {
            berechneLaenge();
            o.berechneLaenge();
            return (int) (laenge - o.getLaenge());
        } else {
            throw new NullPointerException("Can't compare to 'null'");
        }
    }
}
