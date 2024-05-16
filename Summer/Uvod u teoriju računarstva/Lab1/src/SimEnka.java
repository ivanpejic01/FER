import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Map;

public class SimEnka {
	
	public static String[] ulazniStringovi;
	public static List<String> stanja = new ArrayList<>();
	public static List<String> ulazniSimboli = new ArrayList<>();
	public static List<String> prihvatljivaStanja = new ArrayList<>();
	public static String pocetnoStanje = new String();
	public static Map<String, Map<String, List<String>>> prijelazi = new HashMap<>();
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner scan = new Scanner(System.in);
		String linija = new String();
		

		linija = scan.nextLine(); 
		ulazniStringovi = linija.split("\\|"); 

		linija = scan.nextLine(); 
		String[] pomLinija = linija.split("\\,"); 
		for (String s: pomLinija) { 
			stanja.add(s);
		}
		

		linija = scan.nextLine(); 
		pomLinija = linija.split("\\,"); 
		for (String s: pomLinija) { 
			ulazniSimboli.add(s);
		}

		linija = scan.nextLine(); 
		pomLinija = linija.split("\\,"); 
		for (String s: pomLinija) { 
			prihvatljivaStanja.add(s);
		}
		
		pocetnoStanje = scan.nextLine(); 
		
		while(scan.hasNextLine()) {
			linija = scan.nextLine();
			List<String> listaStanja; 
			String lijevoDesno[] = linija.split("->"); 
			String lijevo[] = lijevoDesno[0].split(","); 
			String trenutnoStanje = lijevo[0]; 
			String ulazniZnak = lijevo[1]; 
			String desno[] = lijevoDesno[1].split(","); 

			Map<String, List<String>> mapaSkupStanja = new HashMap<>(); 
			
			if (prijelazi.containsKey(trenutnoStanje)) { 
				mapaSkupStanja = prijelazi.get(trenutnoStanje);
			}
			
			
			if (mapaSkupStanja.get(ulazniZnak) == null) { 
				listaStanja = new ArrayList<>(); 
			}
			
			else {
				listaStanja = mapaSkupStanja.get(ulazniZnak); 
															  
			}
			
			for (String s : desno) { 
				listaStanja.add(s);
			}
			mapaSkupStanja.put(ulazniZnak, listaStanja); 
			prijelazi.put(trenutnoStanje, mapaSkupStanja); 
		}
		
	
		scan.close();

		

		for (String skupPrijelaza: ulazniStringovi) {
			
			String ispisnaLinija = new String();
			List<String> trenutnaStanja = new ArrayList<>();
			trenutnaStanja.add(pocetnoStanje);
			List<String> obicniPrijelazi = new ArrayList<>();
			List<String> prijelaznaStanja = new ArrayList<>();
						
			while(!trenutnaStanja.containsAll(racunajEpsilonPrijelaze(trenutnaStanja))) {
				trenutnaStanja.addAll(racunajEpsilonPrijelaze(trenutnaStanja));
			}
			Collections.sort(trenutnaStanja);
			 trenutnaStanja = trenutnaStanja.stream().distinct().collect(Collectors.toList());
				
			for (String s: trenutnaStanja) {
				ispisnaLinija += s + ",";
			}
			ispisnaLinija = ispisnaLinija.substring(0, ispisnaLinija.length() - 1);
			ispisnaLinija += "|";
			
			
			for (String znak: skupPrijelaza.split(",")) {
				
				for (String stanje: trenutnaStanja) {	
					obicniPrijelazi = racunajObicnePrijelaze(stanje, znak);
					prijelaznaStanja.addAll(obicniPrijelazi);
					
					while(!obicniPrijelazi.containsAll(racunajEpsilonPrijelaze(obicniPrijelazi))) {
						obicniPrijelazi.addAll(racunajEpsilonPrijelaze(obicniPrijelazi));
					}
					
					prijelaznaStanja.addAll(obicniPrijelazi);
					Collections.sort(prijelaznaStanja);
					prijelaznaStanja = prijelaznaStanja.stream().distinct().collect(Collectors.toList());
				}
		
				if(prijelaznaStanja.isEmpty()) {
					ispisnaLinija += '#';
				}
				
				if (!prijelaznaStanja.isEmpty()) {
				for (String s: prijelaznaStanja) {
					ispisnaLinija += s + ',';
				}
				ispisnaLinija = ispisnaLinija.substring(0, ispisnaLinija.length() - 1);
				}

				ispisnaLinija += '|';
				trenutnaStanja.clear();
				trenutnaStanja.addAll(prijelaznaStanja);
				prijelaznaStanja.clear();
				obicniPrijelazi.clear();

			}
			ispisnaLinija = ispisnaLinija.substring(0, ispisnaLinija.length() - 1);
			System.out.println(ispisnaLinija);
		}
		
		
	}
	

	public static List<String> racunajEpsilonPrijelaze(List<String> stanja) {
		Map<String, List<String>> pomocnaMapa = new HashMap<>();
		List<String> pomocnaLista = new ArrayList<>();
		List<String> listaUlaznihStanja = new ArrayList<>();
		listaUlaznihStanja.addAll(stanja);
			
		pomocnaLista.clear();
		for (String stanje: listaUlaznihStanja) {			
		if (prijelazi.containsKey(stanje)) {
			pomocnaMapa.putAll(prijelazi.get(stanje));
			if (pomocnaMapa.containsKey("$")) {
				for (String s: pomocnaMapa.get("$")) {
					if (!s.equals("#")) {
					pomocnaLista.add(s);
					}
				}
			}
			
		}

		}
			listaUlaznihStanja.addAll(pomocnaLista);
			listaUlaznihStanja = listaUlaznihStanja.stream().distinct().collect(Collectors.toList());
		
	
		return listaUlaznihStanja;
}
	
	public static List<String> racunajObicnePrijelaze(String stanje, String znak) {
		Map<String, List<String>> pomocnaMapa = new HashMap<>();
		List<String> pomocnaLista = new ArrayList<>();
		if (prijelazi.containsKey(stanje)) {
			pomocnaMapa.putAll(prijelazi.get(stanje));
			if (pomocnaMapa.containsKey(znak)) {
				for (String s: pomocnaMapa.get(znak)) {
					if (!s.equals("#")) {
						if (s.split(",") != null) {
							for(String s1: s.split(",")) {
								pomocnaLista.add(s1);
							}
						}
						else {
							pomocnaLista.add(s);
						}
					}
				}
			}
		}
		return pomocnaLista;
	}

}
