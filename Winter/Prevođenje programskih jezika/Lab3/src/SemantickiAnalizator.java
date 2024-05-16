import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class SemantickiAnalizator {
	
	public static Map<String, String> deklaracijaMain = new HashMap<>();
	public static Map<String, String> deklaracijaBlok = new HashMap<>();
	public static Boolean uBloku = false;
	public static Boolean odBloka = false;
	public static Boolean doBloka = false;
	public static List<String> ispisneLinije = new ArrayList<>();
	public static Boolean postojiGreska = false;
	public static Map<String, String> privremeniBlok = new HashMap<>();
	
	

	public static void main(String[] args) throws FileNotFoundException {
		
		//Scanner scan = new Scanner(new File("C:\\Users\\Ivan\\Desktop\\lab2.txt"));
		Scanner scan = new Scanner(System.in);
		String[] trenutnaLinija;
		String[] sljedecaLinija;
		int brojacPodudaranja = 0;
		trenutnaLinija = scan.nextLine().split(" ");
		while (scan.hasNextLine()) {
			
			sljedecaLinija = scan.nextLine().split(" ");
			
			

			if (nadiRazmake(trenutnaLinija) == nadiRazmake(sljedecaLinija)) {
				
				//ako se u glavnom programu pojavi deklaracija varijable
				if (nadiTip(trenutnaLinija).equals("IDN") && 
						nadiTip(sljedecaLinija).equals("OP_PRIDRUZI")) {
					//razmatranje deklaracija
				if (!deklaracijaMain.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) {

				deklaracijaMain.put(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2], 
						trenutnaLinija[nadiRazmake(trenutnaLinija) + 1]);

				}

				}
				
				//deklaracija varijable na pocetku za bloka iza kljucne rijeci za
				if (nadiTip(trenutnaLinija).equals("KR_ZA")) {
					
					uBloku = true;
					
					if (nadiTip(sljedecaLinija).equals("IDN")) {
					//ako nije deklariran u mainu deklariraj ga u bloku
					if (!deklaracijaBlok.containsKey(sljedecaLinija[nadiRazmake(sljedecaLinija) + 2])) {
						/*
					deklaracijaBlok.put(sljedecaLinija[nadiRazmake(sljedecaLinija) + 2], 
							sljedecaLinija[nadiRazmake(sljedecaLinija) + 1]);*/
						privremeniBlok.put(sljedecaLinija[nadiRazmake(sljedecaLinija) + 2], 
							sljedecaLinija[nadiRazmake(sljedecaLinija) + 1]);

						}
					}
				
				}
			} 
			if (!uBloku && trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("IDN")
					&& !sljedecaLinija[nadiRazmake(sljedecaLinija)].equals("OP_PRIDRUZI") &&
					!postojiGreska) {
				
				if (deklaracijaMain.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) {
				ispisneLinije.add(trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " "
							+ deklaracijaMain.get(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]) + " "
							+ trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
				} else {
					ispisneLinije.add("err " + trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " " +
							trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
					postojiGreska = true;
				}
				
			}
			
			//vec sam procitao kr_za i sad sam u kr_od
			if (uBloku && trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("KR_OD")) {
				odBloka = true;
			}
			
			//procitao sam kr_od i sad sam na kr_do
			if (uBloku && trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("KR_DO")) {
				doBloka = true;
			}
			if (uBloku && !odBloka && !doBloka && (trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("IDN") &&  
					!sljedecaLinija[nadiRazmake(sljedecaLinija)].equals("KR_OD") && 
					!postojiGreska)) {
				if (!sljedecaLinija[nadiRazmake(sljedecaLinija)].equals("OP_PRIDRUZI")) {
					if (deklaracijaBlok.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) {
						ispisneLinije.add(trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " "
						+ deklaracijaBlok.get(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]) + " "
						+ trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
						
						
					} else if (deklaracijaMain.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) {
						ispisneLinije.add(trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " "
								+ deklaracijaMain.get(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]) + " "
								+ trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
						
					} else if (!(deklaracijaBlok.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) && 
							!(deklaracijaMain.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]))){
						postojiGreska = true;
						ispisneLinije.add("err " + trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " " +
								trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
						}
					} 
				}
			
			if (odBloka) {
				if (trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("IDN")) {

					if (!deklaracijaBlok.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) {
						ispisneLinije.add("err " + trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " " +
								trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
						postojiGreska = true;
					}
					
					odBloka = false;
					
				}  else if (trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("BROJ")) {
					odBloka = false;
				} 
				
			} 
			
			if (doBloka) {
				if (trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("IDN")) {
					if (!deklaracijaMain.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) {
					ispisneLinije.add("err " + trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " " +
							trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
					postojiGreska = true;
					doBloka = false;
					} else if (deklaracijaMain.containsKey(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2])) {
						ispisneLinije.add(trenutnaLinija[nadiRazmake(trenutnaLinija) + 1] + " "
					+ deklaracijaMain.get(trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]) + " "
					+ trenutnaLinija[nadiRazmake(trenutnaLinija) + 2]);
						doBloka = false;
					}
					
				}
					
				 else if (trenutnaLinija[nadiRazmake(trenutnaLinija)].equals("BROJ")) {
					doBloka = false;
				} 
				
			}
			

			if (nadiTip(trenutnaLinija).equals("KR_AZ")) {
				deklaracijaBlok.clear();
				uBloku = false;
				}
			
			trenutnaLinija = sljedecaLinija;
			
			}
		
		for (String linija: ispisneLinije) {
			System.out.println(linija);
		}
		
	
		scan.close();

	}
	
	public static int nadiRazmake(String[] testniString) {
		int brojac = 0;
		for (int i = 0; i < testniString.length; i++) {
			if (testniString[i].equals("")) {
				brojac++;
			}
		
		}
		return brojac;
	}
	
	public static String nadiTip(String[] testniString) {
		String tip = new String();
		for (int i = 0; i < testniString.length; i++) {

			if (!testniString[i].equals("")) {
				tip = testniString[i];
				break;
			}
		}
		return tip;
	}

}
