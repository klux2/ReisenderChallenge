package application;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedList;
import java.util.Random;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;


public class Main extends Application {
	private int breite = 700;
	private int hohe = breite;
	private boolean zeigeTeilScore = false;
	private static int startSeed = 90;
	
	@Override
	public void start(Stage primaryStage) {		
		try {
			primaryStage.setTitle("Gesamtscore ReisenderChallenge");
			AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
			Scene scene = new Scene(root,breite,hohe);
			Canvas zeichenbrett = new Canvas(breite,hohe);
			GraphicsContext graphischeElemente = zeichenbrett.getGraphicsContext2D();
			root.getChildren().add(zeichenbrett);			
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();

			// alles Nötige generieren
			System.out.print("Erstelle Seeds ... ");
			long[] seeds = seedGenerator(startSeed, 10);
			/* tests mit:
			 * 16 Städte (6 tests)
			 * 40 Städte (3 test)
			 * 209 Städte (1 test)
			 */
			System.out.println("fertig.");
			System.out.print("Erstelle Städte ... ");
			LinkedList<Punkt>[] stadte = generiereStadte(seeds, 10);
			Reisender[] reisende = generieReisende(stadte);
			reisende[reisende.length-1].zeichneStadte(graphischeElemente);
			System.out.println("fertig.");

			// berechne score und somit die routen
			System.out.println("Berechne Score ... ");
			double score = berechneScore(reisende);
			score = round(score, 4);
			
			// zeichne letzten Reisenden
			reisende[reisende.length-1].zeichneRoute(graphischeElemente);

			// Score anzeigen
			Label scoreText = new Label("Score: " + score); 
			scoreText.setMinSize(breite, 100);
			scoreText.setTextFill(Color.BLACK);
			scoreText.setFont(new Font(50));
			root.getChildren().add(scoreText);
			scoreText.setText("Gesamtscore: " + score);
			
			System.out.println("Gesamtscore: " + score);
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void erstelleFenster(Reisender reisender, Double score, int testNummer)
	{
		if (!zeigeTeilScore)
			return;
        try {
        	AnchorPane root = (AnchorPane)FXMLLoader.load(getClass().getResource("MainWindow.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Ergebnis von Test: " + testNummer);
            Scene scene = new Scene(root, breite, hohe);
            stage.setScene(scene);
            
			Canvas zeichenbrett = new Canvas(breite,hohe);
			GraphicsContext graphischeElemente = zeichenbrett.getGraphicsContext2D();
			root.getChildren().add(zeichenbrett);
			reisender.zeichneAlles(graphischeElemente);
            
			score = round(score, 4);
			Label scoreText = new Label("Score: " + score); 
			scoreText.setMinSize(breite, 100);
			scoreText.setTextFill(Color.BLACK);
			scoreText.setFont(new Font(50));
			root.getChildren().add(scoreText);
			scoreText.setText("Score: " + score);
			
			stage.setResizable(false);
            stage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public static void main(String[] args)
	{		
		if (args.length > 0)
			startSeed = Integer.parseInt(args[0]);
		launch(args);
	}
	
	private double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal bd = new BigDecimal(Double.toString(value));
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    return bd.doubleValue();
	}
	
	private double berechneScore(Reisender[] reisende)
	{
		double score = 0;
		for (int i=0; i<reisende.length; i++)
		{
			Reisender r = reisende[i];
			double teilscore = r.score(breite);
			score += teilscore;
			if (i == 0 || i == 6 || i==9)
				erstelleFenster(r, teilscore, i);
		}
		return score;
	}
	
	private Reisender[] generieReisende(LinkedList<Punkt>[] stadte)
	{
		Reisender[] reisende = new Reisender[stadte.length];
		for (int i=0; i<reisende.length; i++)
			reisende[i] = new Reisender(stadte[i]);
		return reisende;
	}
	
	private LinkedList<Punkt>[] generiereStadte(long[] seeds, int testAnzahl)
	{
		if (testAnzahl%10 != 0) // Anzahl muss ein Vielfaches von 10 sein
			return null;
		LinkedList<Punkt>[] stadte = new LinkedList[testAnzahl];
		for (int i=0; i<testAnzahl*0.6; i++)
			stadte[i] = generierePunkte(seeds[i], 16);
		for (int i=testAnzahl*6/10; i<testAnzahl*0.9; i++)
			stadte[i] = generierePunkte(seeds[i], 40);
		for (int i=testAnzahl*9/10; i<testAnzahl; i++)
			stadte[i] = generierePunkte(seeds[i], 209);
		return stadte;
	}
	
	private long[] seedGenerator(int startSeed, int menge)
	{
		if (menge <= 0)
			return new long[0];
		Random seedGenerator = new Random(startSeed);
		long [] seeds = new long[menge];
		for (int i=0; i<seeds.length; i++)
			seeds[i] = seedGenerator.nextLong();
		return seeds;
	}
	
	private LinkedList<Punkt> generierePunkte(long seed, int anzahl)
	{
		int randabstand = 5;
		LinkedList<Punkt> punkte = new LinkedList<Punkt>();
		Random random = new Random(seed);
		for (int i=0; i<anzahl; i++)
			punkte.add(new Punkt(randabstand + random.nextInt(breite-2*randabstand), randabstand+random.nextInt(hohe-2*randabstand)));
		return punkte;
	}
}
