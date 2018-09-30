package application;

import javafx.scene.canvas.GraphicsContext;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static application.Main.procCount;

public class Reisender {
	private LinkedList<Linie> route;
	private LinkedList<Punkt> stadte;

	public Reisender(LinkedList<Punkt> stadte)
	{
		this.stadte = stadte;
		route = new LinkedList<Linie>();
	}

	public LinkedList<Linie> berechneRoute(long startzeit) {
		// beste Lösung: https://xkcd.com/399/

		int anz = stadte.size();
		if (anz < 2) {
			return null;
		}
		LinkedList<Linie> l;
		final Strecke[] min = new Strecke[1];

		// Eroeffnungs-Heuristik
		// All Nearest-Neighbor
		Strecke[] s = new Strecke[anz];
		for (int i = 0; i < s.length; i++) {
			s[i] = new Strecke(anz + 2);
		}
		for (int i = 0; i < anz; i++) {
			Punkt start = stadte.get(i);
			Punkt ende;
			double dist;
			LinkedList<Punkt> uebrig = new LinkedList<>(stadte);
			for (int j = 0; j < anz - 1; j++) {
				ende = uebrig.get(0);
				if (ende == start) {
					ende = uebrig.get(1);
				}
				dist = new Linie(start, ende).lange();
				uebrig.remove(start);
				for (Punkt stadt : uebrig) {
					double d_neu = new Linie(start, stadt).lange();
					if (d_neu < dist) {
						ende = stadt;
						dist = d_neu;
					}
				}
				s[i].getLinien().add(new Linie(start, ende));
				start = ende;
			}
			s[i].getLinien().add(new Linie(start, stadte.get(i)));
			s[i].clean();
			s[i].berechneLaenge();
		}
		min[0] = Collections.min(Arrays.asList(s));

		// Verbesserungsheuristik
		// 3-opt-Heuristik
		int max = stadte.size() < 50 ? 30 : 1;
		int[] delims = new int[procCount];
		for (int t = 0; t < procCount; t++) {
			delims[t] = min[0].getLinien().size() / procCount * t;
		}
		for (int n = 0; n < max; n++) {
			ExecutorService es = Executors.newCachedThreadPool();
			for (int t = 0; t < procCount; t++) {
				final int finalT = t;
				Thread thread = new Thread(() -> {
					int upperBound;
					if (finalT + 1 < procCount) {
						upperBound = delims[finalT + 1];
					} else {
						upperBound = min[0].getLinien().size();
					}
					for (int i = delims[finalT]; i < upperBound; i++) {
						if (Thread.interrupted()) {
//							System.out.println(String.format("Thread %d interrupted at %d of %d", finalT, i, upperBound));
							break;
						}
						for (int j = i; j < min[0].getLinien().size(); j++) {
							for (int k = j; k < min[0].getLinien().size(); k++) {
								if ((i < j - 1) && (i < k - 1) && (j < k - 1)) {
									Strecke[] s_neu = new Strecke[7];
									Punkt ai, aj, ak, ei, ej, ek;
									ai = min[0].getLinien().get(i).getStart();
									ei = min[0].getLinien().get(i).getEnde();
									aj = min[0].getLinien().get(j).getStart();
									ej = min[0].getLinien().get(j).getEnde();
									ak = min[0].getLinien().get(k).getStart();
									ek = min[0].getLinien().get(k).getEnde();
									for (int m = 0; m < 7; m++) {
										s_neu[m] = new Strecke(min[0].getLinien());
										s_neu[m].getLinien().add(new Linie(
												s_neu[m].getLinien().get(s_neu[m].getLinien().size() - 1).getEnde(),
												s_neu[m].getLinien().get(0).getStart()));
									}

									s_neu[0].getLinien().set(i, new Linie(ai, ak));
									s_neu[0].getLinien().set(j, new Linie(ej, ei));
									s_neu[0].getLinien().set(k, new Linie(aj, ek));
									umdrehen(s_neu[0], j, k);

									s_neu[1].getLinien().set(i, new Linie(ai, ej));
									s_neu[1].getLinien().set(j, new Linie(ak, aj));
									s_neu[1].getLinien().set(k, new Linie(ei, ek));
									umdrehen(s_neu[1], i, j);

									s_neu[2].getLinien().set(i, new Linie(ai, ej));
									s_neu[2].getLinien().set(j, new Linie(ak, ei));
									s_neu[2].getLinien().set(k, new Linie(aj, ek));

									s_neu[3].getLinien().set(i, new Linie(ai, aj));
									s_neu[3].getLinien().set(j, new Linie(ei, ak));
									s_neu[3].getLinien().set(k, new Linie(ej, ek));
									umdrehen(s_neu[3], i, j);
									umdrehen(s_neu[3], j, k);

									s_neu[4].getLinien().set(i, new Linie(ai, aj));
									s_neu[4].getLinien().set(j, new Linie(ei, ej));
									s_neu[4].getLinien().set(k, new Linie(ak, ek));
									umdrehen(s_neu[4], i, j);

									s_neu[5].getLinien().set(i, new Linie(ai, ak));
									s_neu[5].getLinien().set(j, new Linie(ej, aj));
									s_neu[5].getLinien().set(k, new Linie(ei, ek));
									umdrehen(s_neu[5], i, j);
									umdrehen(s_neu[5], j, k);

									s_neu[6].getLinien().set(i, new Linie(ai, ei));
									s_neu[6].getLinien().set(j, new Linie(aj, ak));
									s_neu[6].getLinien().set(k, new Linie(ej, ek));
									umdrehen(s_neu[6], j, k);

									for (int m = 0; m < 7; m++) {
										s_neu[m].clean();
										s_neu[m].berechneLaenge();
										if (s_neu[m].getLaenge() < min[0].getLaenge()) {
											min[0] = s_neu[m];
										}
									}
								}
							}
						}
//						if (i + 1 == upperBound) {
//							System.out.println(String.format("Thread %d finished", finalT));
//						}
					}
				});
				es.execute(thread);
//				System.out.println(String.format("Thread %d started", t));
			}
			es.shutdown();
			try {
				if (stadte.size() < 50) {
					es.awaitTermination(verbleibendeZeit(startzeit) - 500, TimeUnit.MILLISECONDS);
					es.shutdownNow();
				} else {
					es.awaitTermination(verbleibendeZeit(startzeit) - 20000, TimeUnit.MILLISECONDS);
					es.shutdownNow();
					es.awaitTermination(15000, TimeUnit.MILLISECONDS);
				}
//					System.out.println("ExecutorService terminated: " + es.isTerminated());
			} catch (InterruptedException e) {
				System.exit(-1);
			}
		}
		l = new LinkedList<>(min[0].getLinien());

		return l;
	}

	private void umdrehen(Strecke strecke, int von, int bis) {
		if (von < bis) {
			for (int i = von + 1; i < bis; i++) {
				strecke.getLinien().set(i, strecke.getLinien().get(i).umdrehen());
			}
		} else {
			for (int i = bis + 1; i < von; i++) {
				strecke.getLinien().set(i, strecke.getLinien().get(i).umdrehen());
			}
		}
	}

	// nur zur Hilfe :)
	private long verbleibendeZeit(long startZeit)
	{
		double zeitMultiplier = 6/1000.0;
		long vorhandeneZeit = (long)(zeitMultiplier*stadte.size()*stadte.size()*1000)/1;
		long verbrauchteZeit = System.currentTimeMillis() - startZeit;
		return vorhandeneZeit - verbrauchteZeit;
	}

	public double score(int seitenLange)
	{
		int stadteAnzahl = stadte.size();
		long startZeit = System.currentTimeMillis();
		route = berechneRoute(startZeit);
		long endeZeit = System.currentTimeMillis();
		double berechnungszeitInSek = (endeZeit - startZeit)/1000.0;
		if (berechnungszeitInSek == 0)
			berechnungszeitInSek += 0.001;

		double zeitMultiplier = 6/1000.0;
		double vorhandeneZeitInSekunden = zeitMultiplier*stadteAnzahl*stadteAnzahl;
		double verbleibendeSekunden = vorhandeneZeitInSekunden - berechnungszeitInSek;

		String fehler = "";
		if (stadteAnzahl != stadte.size())
			fehler += "Städteanzahl hat sich verändert! ";
		if (!alleStadteBereist())
			fehler += "Nicht alle Städte bereist! ";
		if (!routeIstZusammenhangend())
			fehler += "Route ist nicht zusammenhängend! ";
		if (verbleibendeSekunden < 0)
			fehler += "Zeitlimit überschritten! ";;
			if (fehler != "")
			{
				System.out.println(fehler);
				printInfo(verbleibendeSekunden, 0);
				return 0;
			}

			double avgDistance = 0.521405433*seitenLange;
			double score = (avgDistance * (stadteAnzahl-1) + verbleibendeSekunden) /langeDerRoute();
			printInfo(verbleibendeSekunden, score);
			return score;
	}

	private void printInfo(double verbleibendeSekunden, double score)
	{
		System.out.println("Städteanzahl: " + (stadte.size()));
		System.out.println("Routenlänge: " + langeDerRoute());
		System.out.println("verbleibende Senkunden: " + verbleibendeSekunden);
		System.out.println("Score: " + score);
		System.out.println();
	}

	public double langeDerRoute()
	{
		return langeDerRoute(route);
	}

	public double langeDerRoute(LinkedList<Linie> r)
	{
		if (r == null)
			return 0;
		double lange = 0;
		for (Linie linie: r)
			lange += linie.lange();
		return lange;
	}

	public boolean alleStadteBereist()
	{
		if (route == null)
			return false;
		for (Punkt stadt: stadte)
			if (!stadtInRouteEnthalten(stadt))
				return false;
		return true;
	}

	private boolean stadtInRouteEnthalten(Punkt stadt)
	{
		if (route == null)
			return false;
		for (Linie linie: route)
		{
			if (stadt.equals(linie.getStart()))
				return true;
			if (stadt.equals(linie.getEnde()))
				return true;
		}
		return false;
	}

	private boolean routeIstZusammenhangend()
	{
		return routeIstZusammenhangend(route);
	}

	private boolean routeIstZusammenhangend(LinkedList<Linie> r)
	{
		if (r == null)
			return false;
		if (r.size() < 2)
			return true;
		for (int i=0; i < r.size()-1; i++)
			if (!r.get(i).getEnde().equals(r.get(i+1).getStart())) // Wenn Ende von Strecke i != Start von Strecke i+1
				return false;
		return true;
	}

	public void zeichneAlles(GraphicsContext graphischeElemente)
	{
		zeichneStadte(graphischeElemente);
		zeichneRoute(graphischeElemente);
	}

	public void zeichneStadte(GraphicsContext gc)
	{
		if (stadte == null)
			return;
		for (Punkt stadt: stadte)
			stadt.zeichne(gc);
	}

	public void zeichneRoute(GraphicsContext gc)
	{
		if (route == null)
			return;
		for (Linie strecke: route)
			strecke.zeichne(gc);
	}
}
