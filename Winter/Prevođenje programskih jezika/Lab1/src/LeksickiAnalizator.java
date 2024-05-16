
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LeksickiAnalizator {
	
	public static List<String> operatori = new ArrayList<>();
	public static List<String> kljucneRijeci = new ArrayList<>();
	
	public static void main(String[] args) throws FileNotFoundException{
		
		//Scanner scan = new Scanner(System.in);
		Scanner scan = new Scanner(new File("C:\\Users\\Ivan\\Desktop\\lab2.txt"));
		operatori.add("+");
		operatori.add("-");
		operatori.add("*");
		operatori.add("/");
		operatori.add("=");
		operatori.add("(");
		operatori.add(")");
		kljucneRijeci.add("za");
		kljucneRijeci.add("od");
		kljucneRijeci.add("do");
		kljucneRijeci.add("az");
		
		int brojacLinija = 0;
		
		
		while (scan.hasNextLine()) {
			
			++brojacLinija;
			String[] linija = scan.nextLine().split(""); /*linija je ustvari polje znakova i 
			sad cu citati znak po znak i grupirati*/

			if(linija.length > 0 && linija[0] != "") {
			String rijec = new String();
			String konstanta = new String();
			for (int i = 0; i < linija.length; i++) {
				if (!linija[i].equals(" ") || !linija[i].equals("\t")) {
					
					/*komentari*/
					if (linija[i].equals("/")) {
						i++;
						if ((i < linija.length) && linija[i].equals("/")) {
							i = linija.length;
							break;
						} else {
							i--;
						}
					}
					
					/*identifikatori*/
					if (Character.isLetter(linija[i].charAt(0))) {
						rijec += linija[i];
						Boolean trazimRijec = true;
						if (i == linija.length - 1) {
							trazimRijec = false;
						}
						
						int j = i + 1;
						while(trazimRijec && j < linija.length) {
							if (linija[j].equals(" ") || operatori.contains(linija[j])) {
								trazimRijec = false;
							} else {
								rijec += linija[j];
								j++;
								trazimRijec = true;
							}
							
						}
						i = j - 1;
						if (kljucneRijeci.contains(rijec)) {
							System.out.println("KR_" + rijec.toUpperCase() + " " + brojacLinija + " " + rijec);
						} else {
						System.out.println("IDN " + brojacLinija + " " + rijec);
						}
						rijec = "";
					}
					
					/*operatori*/ 
					else if (operatori.contains(linija[i])) {
						if (linija[i].equals("=")) {
							System.out.println("OP_PRIDRUZI " + brojacLinija + " " + linija[i]);
						}
						
						else if (linija[i].equals("+")) {
							System.out.println("OP_PLUS " + brojacLinija + " " + linija[i]);
						}
						
						else if (linija[i].equals("-")) {
							System.out.println("OP_MINUS " + brojacLinija + " " + linija[i]);
						}
						
						else if (linija[i].equals("*")) {
							System.out.println("OP_PUTA " + brojacLinija + " " + linija[i]);
						}
						
						else if (linija[i].equals("/")) {
							System.out.println("OP_DIJELI " + brojacLinija + " " + linija[i]);
						}
						
						else if (linija[i].equals("(")) {
							System.out.println("L_ZAGRADA " + brojacLinija + " " + linija[i]);
						}
						
						else if (linija[i].equals(")")) {
							System.out.println("D_ZAGRADA " + brojacLinija + " " + linija[i]);
						}
						
						
						
						
					}
					
					/*konstante*/
					else if (Character.isDigit(linija[i].charAt(0))) {
						konstanta += linija[i];
						Boolean trazimKonstantu = true;
						
						if (i == linija.length - 1) {
							trazimKonstantu = false;
						}
						
						int j = i + 1;
						
						while(trazimKonstantu && j < linija.length) {
							if (linija[j].equals(" ") || linija[j].equals("\t") || 
									operatori.contains(linija[j]) ||
									Character.isLetter(linija[j].charAt(0))) {
								trazimKonstantu = false;
							} else {
								konstanta += linija[j];
								j++;
								trazimKonstantu = true;
							}
							
						}
						i = j - 1;
						System.out.println("BROJ " + brojacLinija + " " + konstanta);
						konstanta = "";
						
					}
					
					
				}

				
			}
		}
			
			
		}
		
		scan.close();

	}

}
