import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;



public class Parser {
		
	public static List<String> ulazniZnakovi = new ArrayList<>();
	public static String trenutniZnak = new String();
	public static String ispisnaLinija = new String();
	public static int glava = 0;
	public static boolean prihvacen;
		
	public static void main(String[] args) throws FileNotFoundException {
		
		/*4 nezavrsna znaka => 4 potprograma za ispis*/
		/*Scanner scan = new Scanner(new File("C:\\Users\\Ivan\\Desktop\\ulaz.txt"));*/
		/*Scanner scan = new Scanner(new File("C:\\Users\\Ivan\\Desktop\\ulaz_1.txt"));*/
		Scanner scan = new Scanner(System.in);
		String linija = new String();
		linija = scan.nextLine();
		scan.close();
		String[] pomZnakovi = linija.split("");
		for (String s: pomZnakovi) {
			ulazniZnakovi.add(s);
		}
	
		trenutniZnak = ulazniZnakovi.get(0);
		glava = 0;
		prihvacen = ispisS();
		System.out.println(ispisnaLinija);
		if (prihvacen && glava >= ulazniZnakovi.size()) {
			System.out.println("DA");
		}
		else {
			System.out.println("NE");
		}
	}

	private static boolean ispisS() {
		ispisnaLinija += "S";
		/*ako je kraj*/
		if (glava == (ulazniZnakovi.size())) { 
			return false;
		}
		trenutniZnak = ulazniZnakovi.get(glava); 
		glava++; 
		
		/*usporedi znak je li a ili b*, tj hoce li se gledati A pa B ili obrnuto*/
		if (trenutniZnak.equals("a")) {
			if (ispisA()) {
				return ispisB();
				
			}
		}
		
		else if (trenutniZnak.equals("b")) {
			if (ispisB()) {
				return ispisA();
			}
		}
		return false;
	}
	
	private static boolean ispisA() {
		ispisnaLinija += "A";
		/*ako je kraj*/
		if (glava == (ulazniZnakovi.size())) { 
			return false;
		}
		trenutniZnak = ulazniZnakovi.get(glava); 
		glava++;
		
		/*je li a ili b*/
		
		if (trenutniZnak.equals("a")) {
			return true;
		}
		/*ako je b, idi dalje za C*/
		else if (trenutniZnak.equals("b")) {
			return ispisC();
		}
		
		return false;
	}
	
	private static boolean ispisB() {
		ispisnaLinija += "B";
		if (glava == (ulazniZnakovi.size())) { 
			return true; 
		}

		trenutniZnak = ulazniZnakovi.get(glava);
		/*ako je c idi dalje u ispitivanje i u svakom koraku mozes stati i vratiti false*/
		if (trenutniZnak.equals("c")) {
			glava++;
			trenutniZnak = ulazniZnakovi.get(glava);
			if (trenutniZnak.equals("c") && glava < ulazniZnakovi.size()) {
				glava++;
				trenutniZnak = ulazniZnakovi.get(glava);
				/*equals ide u if jer prije toga zovem potp za S*/
				if (ispisS() && ulazniZnakovi.get(glava).equals("b") && glava < ulazniZnakovi.size()) {
					glava++;
					trenutniZnak = ulazniZnakovi.get(glava);
					if (trenutniZnak.equals("c") && glava < ulazniZnakovi.size()) {
						glava++;
						return true;
					} else {
						return false;
					}
				} else {
					return false;
				}
			} else {
				return false;
			}
			
		} 
		return true;
			
	}
	
	private static boolean ispisC() {
		ispisnaLinija += "C";
		if (ispisA()) {
				return ispisA();
			
		}
		return false;
	}
	
	

}
