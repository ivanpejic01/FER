import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class SimPa {

	public static String[] ulazniNizovi;
	public static Set<String> skupStanja = new HashSet<>();
	public static Set<String> skupUlaznihZnakova = new HashSet<>();
	public static Set<String> skupZnakovaStoga = new HashSet<>();
	public static Set<String> skupPrihvatljivihStanja = new HashSet<>();
	public static String pocetnoStanje = new String();
	public static String pocetniZnakStoga = new String();
	public static Map<String, String> funkcijePrijelaza = new HashMap<>();
	public static Stack<String> stog = new Stack<>();
	public static String trenutnoStanje = new String();
	public static String ispisnaLinija = new String();
	public static String vrhStoga = new String();
	
	
	public static void main(String[] args) throws FileNotFoundException {
		
				//Scanner scan = new Scanner(new File("C:\\Users\\Ivan\\Desktop\\ulaz1.txt"));
				Scanner scan = new Scanner(new File("C:\\Users\\Ivan\\Desktop\\ulaz.txt"));
				//Scanner scan = new Scanner(System.in);
				String linija = new String();
				
				
				linija = scan.nextLine();
				ulazniNizovi = linija.split("\\|");

				linija = scan.nextLine();
				for (String s: linija.split("\\,")) {
					skupStanja.add(s);
				}
				
				linija = scan.nextLine();
				for (String s: linija.split("\\,")) {
					skupUlaznihZnakova.add(s);
				}
				
				linija = scan.nextLine();
				for (String s: linija.split("\\,")) {
					skupZnakovaStoga.add(s);
				}
				
				linija = scan.nextLine();
				for (String s: linija.split("\\,")) {
					skupPrihvatljivihStanja.add(s);
				}
				
				pocetnoStanje = scan.nextLine();
				pocetniZnakStoga = scan.nextLine();
				
				while(scan.hasNextLine()) {
					linija = scan.nextLine();
					
					String kljucVrijednost[] = linija.split("->");
					String key = kljucVrijednost[0];
					String value = kljucVrijednost[1];
					
					funkcijePrijelaza.put(key, value);
					
				}
				scan.close();
				radAutomata();
				

	}
	
	
	public static void radAutomata() {
		
		String[] ulazniNiz;
		boolean postojiEpsilon;
		for (String ulaznaLinija: ulazniNizovi) {
			ispisnaLinija = "";
			stog.clear();
			ulazniNiz = ulaznaLinija.split(",");
			trenutnoStanje = pocetnoStanje;
			stog.push(pocetniZnakStoga);
			ispisnaLinija += trenutnoStanje + "#" + pocetniZnakStoga + "|";

			for (String ulazniZnak: ulazniNiz) {
				
				postojiEpsilon = true;
				/*prvo provjeri epsilon prijelaz*/

				while (postojiEpsilon) {
				vrhStoga = stog.pop();
				if (funkcijePrijelaza.containsKey(trenutnoStanje + ",$," + vrhStoga)) {
					stog.push(vrhStoga);
					postojiEpsilonPrijelaz();
					}
				else {
					postojiEpsilon = false;
					stog.push(vrhStoga);
				}
				}

				/*situacija u kojoj postoji prijelaz za trenutno stanje, ulazni znak i znak stoga*/
				if (!stog.isEmpty()) {
				String pomString = stog.pop();
				if (funkcijePrijelaza.containsKey(trenutnoStanje + "," + ulazniZnak + "," + pomString)) {
					stog.push(pomString);
					postojiPrijelazZaZnak(ulazniZnak);
					}
				
				else {
					/*situacija u kojoj ne postoji prijelaz za trenutno stanje, ulazni znak i znak stoga*/
						ispisnaLinija += "fail|0";
						break;
					}
				}

			}
			
			System.out.println(ispisnaLinija);
		}
	}
	
	public static void postojiEpsilonPrijelaz() {
		
		String pomKljuc = trenutnoStanje;
		String znakStoga = stog.pop();
		stog.push(znakStoga);
		String pomVrijednost[] = funkcijePrijelaza.get(pomKljuc + ",$," + znakStoga).split(",");

		trenutnoStanje = pomVrijednost[0];
		String pomStog = pomVrijednost[1];
		
		if (pomStog.length() == 2) {
			stog.push(Character.toString(pomStog.charAt(1)));
			stog.push(Character.toString(pomStog.charAt(0)));
			stog.pop();
			ispisnaLinija += trenutnoStanje + "#";
			
			for (String s: stog) {
				ispisnaLinija += s;
			
			}		
			ispisnaLinija += "|";
			
		}
		
		if (pomStog.length() == 1 && !pomStog.equals("$")) {
			ispisnaLinija += trenutnoStanje + "#";
			for(String s: stog) {
				ispisnaLinija += s;
			}
			ispisnaLinija += "|";
		}
		
		if (pomStog.equals("$")) {
			stog.pop();
			ispisnaLinija += trenutnoStanje + "#";
			if (!stog.isEmpty()) {
			
			for(String s: stog) {
				ispisnaLinija += s;
				}
			
			}
			
			else {
				ispisnaLinija += "$";
			}
			ispisnaLinija += "|";
		}
	}
	
	public static void postojiPrijelazZaZnak(String znak) {
		
		String pomKljuc = trenutnoStanje;
		String znakStoga = stog.pop();
		String pomVrijednost[] = funkcijePrijelaza.get(pomKljuc + "," + znak + "," + znakStoga).split(",");

		trenutnoStanje = pomVrijednost[0];
		String pomStog = pomVrijednost[1];
		
		if (pomStog.length() == 2) {
			stog.push(Character.toString(pomStog.charAt(1)));
			stog.push(Character.toString(pomStog.charAt(0)));
			ispisnaLinija += trenutnoStanje + "#";
			
			for (String s: stog) {
				ispisnaLinija += s;
			
			}		
			ispisnaLinija += "|";
			
		}
		
		if (pomStog.length() == 1 && !pomStog.equals("$")) {
			ispisnaLinija += trenutnoStanje + "#";
			for(String s: stog) {
				ispisnaLinija += s;
			}
			ispisnaLinija += "|";
		}
		
		if (pomStog.equals("$")) {
			stog.pop();
			ispisnaLinija += trenutnoStanje + "#";
			if (!stog.isEmpty()) {
			
			for(String s: stog) {
				ispisnaLinija += s;
				}
			
			}
			
			else {
				ispisnaLinija += "$";
			}
			ispisnaLinija += "|";
		}
		
		
		
	}

}
