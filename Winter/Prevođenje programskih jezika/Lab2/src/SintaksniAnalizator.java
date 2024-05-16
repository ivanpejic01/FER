import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class SintaksniAnalizator {
	
	public static List<String[]> ulazniZnakovi = new ArrayList<>();
	public static Stack<String> stog = new Stack<>();
	public static int kazaljkaNaUlazneZnakove = 0;
	public static String vrhStoga = new String();
	public static String[] ulazniZnakPolje;
	public static int razmaci = 0;
	public static Stack<String> stabloStog = new Stack<>();
	public static List<String> ispisStabla = new ArrayList<>();
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Scanner scan = new Scanner(System.in);
		
		/*spremanje ulaznih znakova koji su ujedno i zavrsni u jednu listu*/
		while(scan.hasNextLine()) {
			String[] linija = scan.nextLine().split(" ");
			ulazniZnakovi.add(linija);
			
		}

		stog.push("dnoStoga");
		stog.push("<program>");
		stabloStog.push("<program>" + " " + String.valueOf(razmaci));
		
		boolean nemaGreske = true;

		while(nemaGreske) {
			vrhStoga = stog.pop();
			stog.push(vrhStoga);
			if (kazaljkaNaUlazneZnakove < ulazniZnakovi.size()) {
				ulazniZnakPolje = ulazniZnakovi.get(kazaljkaNaUlazneZnakove);
				} else if (kazaljkaNaUlazneZnakove == ulazniZnakovi.size()){
					ulazniZnakPolje[0] = "prazan";
				}
			/*akcija #1*/
			if (vrhStoga.equals("<program>") && (ulazniZnakPolje[0].equals("IDN") || ulazniZnakPolje[0].equals("KR_ZA"))) {
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<lista_naredbi>");
				
				Zamijeni(pomocnaLista);
			} 
			
			/*akcija #2*/
			else if (vrhStoga.equals("<lista_naredbi>") && (ulazniZnakPolje[0].equals("IDN") || ulazniZnakPolje[0].equals("KR_ZA"))){
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<naredba>");
				pomocnaLista.add("<lista_naredbi>");
				
				Zamijeni(pomocnaLista);
			}
			
			/*akcija #3*/
			else if ((vrhStoga.equals("<lista_naredbi>") && (ulazniZnakPolje[0].equals("KR_AZ") || ulazniZnakPolje[0].equals("prazan"))) ||
					(vrhStoga.equals("<E_lista>") && (ulazniZnakPolje[0].equals("IDN") || ulazniZnakPolje[0].equals("D_ZAGRADA") || ulazniZnakPolje[0].equals("KR_ZA") || 
							ulazniZnakPolje[0].equals("KR_DO") || ulazniZnakPolje[0].equals("KR_AZ") || ulazniZnakPolje[0].equals("prazan"))) || 
					(vrhStoga.equals("<T_lista>") && (ulazniZnakPolje[0].equals("IDN") || ulazniZnakPolje[0].equals("OP_PLUS") || ulazniZnakPolje[0].equals("OP_MINUS") || 
							ulazniZnakPolje[0].equals("D_ZAGRADA") || ulazniZnakPolje[0].equals("KR_ZA") || ulazniZnakPolje[0].equals("KR_DO") || ulazniZnakPolje[0].equals("KR_AZ") || ulazniZnakPolje[0].equals("prazan")))) {
				stog.push("$");
				Izvuci();
				
		
			}
			
			/*akcija #4*/
			else if (vrhStoga.equals("<naredba>") && ulazniZnakPolje[0].equals("IDN")) {
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<naredba_pridruzivanja>");
				
				Zamijeni(pomocnaLista);
			}
			
			/*akcija #5*/
			else if (vrhStoga.equals("<naredba>") && ulazniZnakPolje[0].equals("KR_ZA")) {
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<za_petlja>");
				
				Zamijeni(pomocnaLista);
			}
			
			/*akcija #6*/
			else if(vrhStoga.equals("<naredba_pridruzivanja>") && ulazniZnakPolje[0].equals("IDN")) {
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("OP_PRIDRUZI");
				pomocnaLista.add("<E>");
				
				Zamijeni(pomocnaLista);
				Pomakni();

				ispisStabla.add(ispisRazmaka(String.valueOf(razmaci)) + ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
			}
			
			/*akcija #7*/
			else if (vrhStoga.equals("<za_petlja>") && ulazniZnakPolje[0].equals("KR_ZA")) {
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("IDN");
				pomocnaLista.add("KR_OD");
				pomocnaLista.add("<E>");
				pomocnaLista.add("KR_DO");
				pomocnaLista.add("<E>");
				pomocnaLista.add("<lista_naredbi>");
				pomocnaLista.add("KR_AZ");
				Zamijeni(pomocnaLista);
				Pomakni();
				
				ispisStabla.add(ispisRazmaka(String.valueOf(razmaci)) + ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
			}
			
			/*akcija #8*/
			else if (vrhStoga.equals("<E>") && (
					ulazniZnakPolje[0].equals("IDN") || 
					ulazniZnakPolje[0].equals("BROJ") ||
					ulazniZnakPolje[0].equals("OP_PLUS") ||
					ulazniZnakPolje[0].equals("OP_MINUS") ||
					ulazniZnakPolje[0].equals("L_ZAGRADA"))) {
				
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<T>");
				pomocnaLista.add("<E_lista>");
				Zamijeni(pomocnaLista);
				
			}
			
			/*akcija #9*/
			else if(vrhStoga.equals("<E_lista>") && (ulazniZnakPolje[0].equals("OP_PLUS") ||
					ulazniZnakPolje[0].equals("OP_MINUS"))) {
				
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<E>");
				Zamijeni(pomocnaLista);
				Pomakni();
				
				ispisStabla.add(ispisRazmaka(String.valueOf(razmaci)) + ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
			}
			
			/*akcija #10*/
			else if (vrhStoga.equals("<T>") && (ulazniZnakPolje[0].equals("IDN") || 
					ulazniZnakPolje[0].equals("BROJ") || 
					ulazniZnakPolje[0].equals("OP_PLUS") || 
					ulazniZnakPolje[0].equals("OP_MINUS") || 
					ulazniZnakPolje[0].equals("L_ZAGRADA"))) {
				
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<P>");
				pomocnaLista.add("<T_lista>");
				Zamijeni(pomocnaLista);

				
				
			}
			
			/*akcija #11*/
			else if (vrhStoga.equals("<T_lista>") && (ulazniZnakPolje[0].equals("OP_PUTA") || 
					ulazniZnakPolje[0].equals("OP_DIJELI"))) {
				
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<T>");
				Zamijeni(pomocnaLista);
				Pomakni();
				
				ispisStabla.add(ispisRazmaka(String.valueOf(razmaci)) + ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
			}
			
			/*akcija #12*/
			else if (vrhStoga.equals("<P>") && (ulazniZnakPolje[0].equals("OP_PLUS") || 
					ulazniZnakPolje[0].equals("OP_MINUS"))) {
				
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<P>");
				Zamijeni(pomocnaLista);
				Pomakni();
				
				ispisStabla.add(ispisRazmaka(String.valueOf(razmaci)) + ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
			}
			
			/*akcija #13*/
			else if (vrhStoga.equals("<P>") && ulazniZnakPolje[0].equals("L_ZAGRADA")) {
				
				List<String> pomocnaLista = new ArrayList<>();
				pomocnaLista.add("<E>");
				pomocnaLista.add("D_ZAGRADA");
				Zamijeni(pomocnaLista);
				Pomakni();
				
				ispisStabla.add(ispisRazmaka(String.valueOf(razmaci)) + ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
			}
			
			/*akcija #14*/
			else if ((vrhStoga.equals("<P>") && (ulazniZnakPolje[0].equals("IDN") || ulazniZnakPolje[0].equals("BROJ")))) {
				
				Izvuci();
				Pomakni();
				
				ispisStabla.add(ispisRazmaka(String.valueOf(razmaci + 1)) + 
						ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
				

			}
			
			else if (vrhStoga.equals(ulazniZnakPolje[0])) {
				Izvuci();
				Pomakni();
				
			}
			
			else if (vrhStoga.equals("dnoStoga") && ulazniZnakPolje[0].equals("prazan")) {
				for (String linija: ispisStabla) {
					System.out.println(linija);
				}
				nemaGreske = false;
			}
			else {
				nemaGreske = false;
				if (kazaljkaNaUlazneZnakove >= ulazniZnakovi.size()) {
					System.out.println("err kraj");
				
				} else {
					System.out.println("err " + ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
				}
				
				
			}

		}
		
		
		scan.close();
	
	}
	
	public static void Zamijeni(List<String> znakoviNaStog) {
		stog.pop();
		String elementStablo = stabloStog.pop();
		String[] parsiranElementStablo = elementStablo.split(" ");
		ispisStabla.add(ispisRazmaka(parsiranElementStablo[1]) + parsiranElementStablo[0]);
		razmaci = Integer.valueOf(parsiranElementStablo[1]) + 1;
		Stack<String> pomocniStog = new Stack<>();
		for(String znakNaStog: znakoviNaStog) {
			pomocniStog.push(znakNaStog);
		}
		
		while(pomocniStog.size() > 0) {
			String pomocniElement = pomocniStog.pop();
			stog.push(pomocniElement);
			stabloStog.push(pomocniElement + " " + String.valueOf(razmaci));
		}
		
	}
	
	public static void Pomakni() {
		kazaljkaNaUlazneZnakove++;
	}
	
	public static void Izvuci() {
		String elementStablo = stabloStog.pop();
		String[] parsiranElementStablo = elementStablo.split(" ");
		
		if (vrhStoga.equals(ulazniZnakPolje[0])) {
			ispisStabla.add(ispisRazmaka(parsiranElementStablo[1]) + 
					ulazniZnakPolje[0] + " " + ulazniZnakPolje[1] + " " + ulazniZnakPolje[2]);
			stog.pop();
		} else {
			ispisStabla.add(ispisRazmaka(parsiranElementStablo[1]) + parsiranElementStablo[0]);

		if (!stog.isEmpty()) {
			String vrhStoga = stog.pop();
		if (vrhStoga.equals("$")) {
			ispisStabla.add(ispisRazmaka(String.valueOf(Integer.valueOf(parsiranElementStablo[1]) + 1)) + "$");
			stog.pop();
				} 
			}
		}
	}

	public static String ispisRazmaka(String n) {
		int brojRazmaka = Integer.parseInt(n);
		String razmaciZaIspis = new String();
		for (int i = 0; i < brojRazmaka; i++) {
			razmaciZaIspis += " ";
		}
		return razmaciZaIspis;
	}
	

}
