import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MinDka {
	
	public static Set<String> skupStanja = new TreeSet<>();
	public static Set<String> skupSimbola = new TreeSet<>();
	public static Set<String> skupPrihvatljivihStanja = new TreeSet<>();
	public static String pocetnoStanje = new String();
	public static Map<String, String> prijelazi = new TreeMap<>();
	public static Set<String> dohvatljivaStanja = new TreeSet<>();
	public static Set<String> nedohvatljivaStanja = new TreeSet<>();
	public static Map<String, Boolean> tablica = new TreeMap<>();
	public static List<String> listaDohvatljivihStanja = new ArrayList<>();
	public static Map<String, String> noviPrijelazi = new TreeMap<>();
	
	public static void main(String[] args) throws FileNotFoundException {
		
		/*Scanner scan = new Scanner(new File("C:\\Users\\Ivan\\Desktop\\ulaz_1.txt"));*/
		Scanner scan = new Scanner(System.in);
		String linija = new String();
		String[] pomLinija;
		
		linija = scan.nextLine(); 
		pomLinija = linija.split("\\,");
		for (String s: pomLinija) {
			skupStanja.add(s);
		}
		
		linija = scan.nextLine();
		pomLinija = linija.split("\\,");
		for (String s: pomLinija) {
			skupSimbola.add(s);
		}
		
		linija = scan.nextLine();
		pomLinija = linija.split("\\,");
		for (String s: pomLinija) {
			skupPrihvatljivihStanja.add(s);
		}
		
		linija = scan.nextLine();
		pocetnoStanje = linija;
		
		while (scan.hasNextLine()) {
			linija = scan.nextLine();
			String[] mapa = linija.split("->");
			prijelazi.put(mapa[0], mapa[1]);
		}
		
		/*for (Map.Entry<String, String> entry: prijelazi.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}*/
		
		
		
		dohvatljivaStanja.add(pocetnoStanje);
		while(!dohvatljivaStanja.containsAll(racunajDohvatljivaStanja(dohvatljivaStanja))) {
			dohvatljivaStanja.addAll(racunajDohvatljivaStanja(dohvatljivaStanja));
		}
		/*System.out.println("Dohvatljiva stanja");
		for (String s: dohvatljivaStanja) {
			System.out.println(s);
		}*/
		
		nedohvatljivaStanja.addAll(skupStanja);
		nedohvatljivaStanja.removeAll(dohvatljivaStanja);
		
		/*System.out.println("Nedohvatljiva stanja");
		for (String s: nedohvatljivaStanja) {
			System.out.println(s);
		}*/
		
		/*izbacujem nedohvatljiva iz tablice prijelaza*/
		for(String nedohvatljivo: nedohvatljivaStanja) {
			for(String simbol: skupSimbola) {
				prijelazi.remove(nedohvatljivo + "," + simbol);
				skupStanja.remove(nedohvatljivo);
				skupPrihvatljivihStanja.remove(nedohvatljivo);
			}
		}
		
		/*prvo prolazim i trazim ona stanja koja nemaju istu prihvatljivost, prijelazi su nebitni*/
		oznaciStanjaRazlicitePrihvatljivosti(); 
		/*for (Map.Entry<String, Boolean> entry: tablica.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}*/
		/*izbacujem parove cijim se prijelazima ne poklapa prihvatljivost*/
		/*System.out.println("Nakon druge funkcije");*/
	
		izbaciStanjaSPrelaskomUOznacenaStanja();
		/*for(Map.Entry<String, Boolean> entry: tablica.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}*/
		
		/*istovjetna stanja oznacavam isto*/
		/*System.out.println("Nakon trece funkcije");*/
		
		srediIstovjetnaStanja();
		ispisiAutomat();
		for(Map.Entry<String, String> entry: prijelazi.entrySet()) {
			System.out.println(entry.getKey() + "->" + entry.getValue());
		}

	}
	
	public static Set<String> racunajDohvatljivaStanja(Set<String> trenutnoDohvatljiva) {
		
		Set<String> povratni = new TreeSet<>();
		povratni.addAll(trenutnoDohvatljiva);
		
		for (String stanje: trenutnoDohvatljiva) {
			for (String znak: skupSimbola) {
				String kljuc = stanje + "," + znak;
				if (prijelazi.containsKey(kljuc)) {
					povratni.add(prijelazi.get(kljuc));
				}
			}
		}
		
		return povratni;
		
	}
	
	public static void oznaciStanjaRazlicitePrihvatljivosti() {
		listaDohvatljivihStanja.addAll(dohvatljivaStanja);
		for (int i = 0; i < dohvatljivaStanja.size() - 1; i++) {
			for (int j = 1; j < dohvatljivaStanja.size(); j++) {
				if (i == j || j < i) continue;
				if ((skupPrihvatljivihStanja.contains(listaDohvatljivihStanja.get(i)) &&
					!skupPrihvatljivihStanja.contains(listaDohvatljivihStanja.get(j))) || 
						(!skupPrihvatljivihStanja.contains(listaDohvatljivihStanja.get(i)) &&
							skupPrihvatljivihStanja.contains(listaDohvatljivihStanja.get(j)))) {
					tablica.put(listaDohvatljivihStanja.get(i) + "," + 
							listaDohvatljivihStanja.get(j), false);
					/*System.out.println("Stavio sam false u tablicu");*/
				}
				
				else {
					tablica.put(listaDohvatljivihStanja.get(i) + "," + 
								listaDohvatljivihStanja.get(j), true);
					/*System.out.println("Stavio sam true u tablicu");*/
				}
					
			}
		}
	}
	
	
	public static void izbaciStanjaSPrelaskomUOznacenaStanja() {
	
	listaDohvatljivihStanja.addAll(dohvatljivaStanja);
	for (String simbol: skupSimbola) {	
		for(int i = 0; i < dohvatljivaStanja.size() - 1; i++) {
			for (int j = 1; j < dohvatljivaStanja.size(); j++) {
				if (i == j || j < i) continue;

				String stanje1 = listaDohvatljivihStanja.get(i);
				String stanje2 = listaDohvatljivihStanja.get(j);
				String prijelaz1 = prijelazi.get(stanje1 + "," + simbol); 
				String prijelaz2 = prijelazi.get(stanje2 + "," + simbol);
				
				if (stanje1.compareTo(stanje2) > 0) {
					String tmp = stanje1.substring(0, stanje1.length());
					stanje1 = stanje2.substring(0, stanje2.length());
					stanje2 = tmp.substring(0, tmp.length());			
				}
				
				if (prijelaz1.compareTo(prijelaz2) > 0) {
					String tmp = prijelaz1.substring(0, prijelaz1.length());
					prijelaz1 = prijelaz2.substring(0, prijelaz2.length());
					prijelaz2 = tmp.substring(0, tmp.length());			
				}
				
				/*System.out.println("Stanje1 je " + stanje1 + " stanje2 je " + stanje2);
				System.out.println("Prijelaz1 je " + prijelaz1 + " prijelaz2 je " + prijelaz2);*/
				if (tablica.get(stanje1 + "," + stanje2)) {
					if (prijelaz1.compareTo(prijelaz2) != 0) {
					
					if (!tablica.get(prijelaz1 + "," + prijelaz2)) {
						tablica.put(stanje1 + "," + stanje2, false);
							}
						
					
						}
					}
				}
			}
		}
	}
	
	public static void srediIstovjetnaStanja() {
		
		for(int i = 0; i < dohvatljivaStanja.size() - 1; i++) {
			for (int j = 1; j < dohvatljivaStanja.size(); j++) {
				if (i == j || j < i) continue;
				
				String stanje1 = listaDohvatljivihStanja.get(i);
				String stanje2 = listaDohvatljivihStanja.get(j);
				 
				if (tablica.get(stanje1 + "," + stanje2)) {
					/*System.out.println("Za " + stanje1 + " i " + stanje2 + " uvjet je istina");*/
					for (String simbol: skupSimbola) {
						String prijelaz1 = prijelazi.get(stanje1 + "," + simbol);
						String prijelaz2 = prijelazi.get(stanje2 + "," + simbol);
						if (prijelaz1 != null && prijelaz2 != null) {
						if (prijelaz1.compareTo(prijelaz2) > 0) {
							String tmp = prijelaz1.substring(0, prijelaz1.length());
							prijelaz1 = prijelaz2.substring(0, prijelaz2.length());
							prijelaz2 = tmp.substring(0, tmp.length());
							}
						}
						
						if (prijelaz1 == null && prijelaz2 != null) {
							prijelaz1 = prijelaz2.substring(0, prijelaz2.length());
						}
						
						if (prijelaz1 == null && prijelaz2 == null) {
							continue;
						}
			
						/*System.out.println("Zamjena prijelaza stanja " + stanje1 + " u " + prijelaz1);*/
						prijelazi.replace(stanje1 + "," + simbol, prijelaz1);
						prijelazi.remove(stanje2 + "," + simbol);
						for (Map.Entry<String, String> entry: prijelazi.entrySet()) {
							if (entry.getValue().compareTo(stanje2) == 0) {
								prijelazi.replace(entry.getKey(), stanje1);
							}
						}
					}
					
					skupStanja.remove(stanje2);
					skupPrihvatljivihStanja.remove(stanje2);
					if (pocetnoStanje.compareTo(stanje2) == 0) {
						pocetnoStanje = stanje1.substring(0, stanje1.length());
					}
	
					
				}
				
			}
		}
	}
	
	public static void ispisiAutomat() {
		String linijaStanja = new String();
		String linijaSimbola = new String();
		String linijaPrihvatljivihStanja = new String();
		for (String stanja: skupStanja) {
			linijaStanja += stanja + ",";
		}
		linijaStanja = linijaStanja.substring(0, linijaStanja.length() - 1);
		System.out.println(linijaStanja);
		
		for (String simbol: skupSimbola) {
			linijaSimbola += simbol + ",";
		}
		linijaSimbola = linijaSimbola.substring(0, linijaSimbola.length() - 1);
		System.out.println(linijaSimbola);
		
		for (String prihvatljivoStanje: skupPrihvatljivihStanja) {
			linijaPrihvatljivihStanja += prihvatljivoStanje + ",";
		}
		if (linijaPrihvatljivihStanja.length() > 0) {
		linijaPrihvatljivihStanja = linijaPrihvatljivihStanja.substring(0, linijaPrihvatljivihStanja.length() - 1);		
		System.out.println(linijaPrihvatljivihStanja);}
		else {
			System.out.println();
		}
		
		System.out.println(pocetnoStanje);
	}
}
