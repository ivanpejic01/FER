import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class stranicenje {
	

	public static void main(String[] args) {
		
		int n = 3;
		int m = 1;
		Integer nula = 0;
		Integer jedan = 1;
		
		List<Short> tablica = new ArrayList<>();
		List<Short> disk = new ArrayList<>();
		Map<Integer, List<Short>> okviri = new HashMap<>();
		
		for (int i = 0; i < n * 16; i++) {
			tablica.add(Integer.valueOf(0).shortValue());
		}
		
		for (int i = 0; i < n * 16 * 32; i++) {
			disk.add(nula.shortValue());
		}
		
		for (int i = 0; i < m; i++) {
			List<Short> sadrzajOkvira = new ArrayList<>();
			char pomNula = 0;
			for (int j = 0; j < 32; j++) {
				sadrzajOkvira.add(nula.shortValue());
			}
			okviri.put(i, sadrzajOkvira);
		}
		
		for (Map.Entry<Integer, List<Short>> entry: okviri.entrySet()) {
			//System.out.println("kljuc " + entry.getKey() + " vrijednost " + entry.getValue());
		}
		
		
		int t = 0;
		while (true) {
			int brProcesa = t % 3;
			System.out.println("br procesa = " + brProcesa);
			System.out.println("t = " + t);
					
			//int logickaAdresa = (int)((int)(Math.floor(Math.random()*1024)) & 1022);
			int logickaAdresa = 510;
			logickaAdresa = logickaAdresa & 1022;
			int brojStranice = (logickaAdresa & 960) >> 6;
			System.out.println("logicka adresa je " + logickaAdresa + ", a heksadekadski " + Integer.toHexString(logickaAdresa));
			//System.out.println("broj stranice je " + brojStranice);
			int visiBitoviFizAdr = tablica.get(brProcesa * 16 + brojStranice) & (1023 << 6);
			//System.out.println("visi bitovi adrese " + visiBitoviFizAdr);
			int niziBitoviFizAdr = logickaAdresa & 63;
			//System.out.println("nizi bitovi adrese " + niziBitoviFizAdr);
			int fizickaAdresa = niziBitoviFizAdr | visiBitoviFizAdr;
			
			//System.out.println("Vrijednost tablice prije provjere " + tablica.get(0 * 16 + brojStranice));
			//System.out.println("Vrijednost druge tablice prije provjere " + tablica.get(1 * 16 + brojStranice));
			if ((tablica.get(brProcesa * 16 + brojStranice) & 32) == 0) {
				System.out.println("Promasaj!");
				Boolean biloSlobodnih = false;
				//idi po svim okvirima i gledaj ima li slobodnih
				for (Map.Entry<Integer, List<Short>> entry: okviri.entrySet()) {
					List<Short> pomLista = entry.getValue();
					if (Collections.frequency(pomLista, Integer.valueOf(0).shortValue()) == pomLista.size()) {
						List<Short> stranicaSDiska = disk.subList(brProcesa * 512 + 32 * brojStranice, 
								brProcesa * 512 + 32 * brojStranice + 32);
						//System.out.println("stranica s diska " +  stranicaSDiska);
						okviri.put(entry.getKey(), stranicaSDiska);
						System.out.println("	Dodijeljen okvir " + entry.getKey());
						Integer podZaTablicu = t | (1 << 5) | ((entry.getKey()) << 6);
						tablica.set(brProcesa * 16 + brojStranice, podZaTablicu.shortValue());
						System.out.println("Zapis tablice: " + tablica.get(brProcesa * 16 + brojStranice) + " = " 
						+ Integer.toHexString(tablica.get(brProcesa * 16 + brojStranice)));
						Short sadrzajUOkviru = entry.getValue().get(niziBitoviFizAdr / 2);
						//System.out.println("Stara vrijednost: " + sadrzajUOkviru);
						sadrzajUOkviru++;
						//System.out.println("Nova vrijednost " +  sadrzajUOkviru);
						//ovo azuriranje vrijednosti provjeri
						List<Short> pomListaZaAzuriranje = entry.getValue();
						pomListaZaAzuriranje.set(niziBitoviFizAdr / 2, sadrzajUOkviru);
						okviri.put(entry.getKey(), pomListaZaAzuriranje);
						List<Short> listaZaStavljanjeStraniceNaDisk = entry.getValue();
						int indeks = brProcesa * 512 + 32 * brojStranice;
						//stavi stranicu na disk
						for (Short element: listaZaStavljanjeStraniceNaDisk) {
							disk.set(indeks, element);
							indeks++;
							
						}
						stranicaSDiska = disk.subList(brProcesa * 512 + 32 * brojStranice, brProcesa * 512 + 32 * brojStranice + 32);
						//System.out.println("Stranica s diska nakon azuriranja " + stranicaSDiska);
						//System.out.println("Sadrzaj okvira: " + entry.getValue());
						System.out.println("Fizicka adresa = " + Integer.toHexString(fizickaAdresa));
						System.out.println("Sadrzaj adrese = " + (sadrzajUOkviru - 1));
						biloSlobodnih = true;
						break;
					 
					}
				}
				int minLru = 0;
				int procesMinLru = 0;
				int brStrMinLru = 0;
				int indOkvStraniceZaVan = 0;
				int brojac = 0;
				Boolean prvi = true;
				//ako nije bilo slobodnih okvira...
				if (!biloSlobodnih) {
					System.out.println("nije bilo slobodnih");
					//idem po svim okvirima i pomocu njihovog sadrzaja na disku nalazim stranicu i izbacujem ju natrag na disk
					for (Map.Entry<Integer, List<Short>> entry: okviri.entrySet()) {
						List<Short> vrijednostOkvira = entry.getValue();
						//sad na disk
						for (int i = 0; i < disk.size(); i = i + 32) {
							List<Short> stranicaSDiska = disk.subList(i, i + 32);
							//System.out.println("stranica s diska " + stranicaSDiska);
							//ako su vrijednosti stranice s okvira i s diska jednake, idem dalje
							if (vrijednostOkvira.equals(stranicaSDiska)) {
								List<Short> tablicaPrevodenjaZaProces = tablica.subList((i / 512) * 16, (i / 512) * 16 + 16);
								//System.out.println("tablica prevodjenja za proces " + i / 512 + " " + tablicaPrevodenjaZaProces);
								//sad idem 1 po 1 zapis u tablici prevodjenja
								brojac = 0;
								for (Short redak: tablicaPrevodenjaZaProces) {
									brojac++;
									//ako je stranica prisutna gledaj joj LRU
									if ((redak & 32) != 0) {
										if (prvi) {
										minLru = redak & 31;
										procesMinLru = i / 512;
										brStrMinLru = brojac;
										indOkvStraniceZaVan = entry.getKey();
										//System.out.println("LRU joj je " + (redak & 31));
										prvi = false;
										}
										else {
											if ((redak & 31) < minLru) {
												minLru = redak & 31;
												procesMinLru = i / 512;
												brStrMinLru = brojac;
												indOkvStraniceZaVan = entry.getKey();
											}
										}
									}
										
									}
								}
							}
						}
					
					//System.out.println("Nasao sam stranicu s minimalnim LRU-om");

					//System.out.println("Proces u kojem je minimalni lru = " + procesMinLru);
					//System.out.println("Broj stranice u kojoj je minlru = " + (brStrMinLru - 1));
					//System.out.println("Indeks okvira koji praznim = " + indOkvStraniceZaVan);
					
					//nakon sto sam nasao stranicu s najmanjim LRU-om, izbacujem ju van, a ova ide umjesto nje
					//System.out.println("Broj stranice koju izbacujem = " + (brStrMinLru - 1));
					System.out.println("	Izbacujem stranicu " + Integer.toHexString(((brStrMinLru - 1) << 6)) + 
							" iz procesa " + procesMinLru);
					System.out.println("	LRU izbacene stranice = " + Integer.toHexString(minLru));
					System.out.println("	Dodijeljen okvir " + Integer.toHexString(indOkvStraniceZaVan));
					System.out.println("Fizicka adresa: " + Integer.toHexString(fizickaAdresa));
					int sadrzajAdrese = okviri.get(indOkvStraniceZaVan).get(niziBitoviFizAdr / 2);
					//System.out.println("Sadrzaj adrese: " + sadrzajAdrese);
					int staraVrijednostTablica = tablica.get(procesMinLru * 16 + (brStrMinLru - 1));
					//System.out.println("Stara vrijednost tablica " + staraVrijednostTablica);
					staraVrijednostTablica = staraVrijednostTablica & (-33);
					tablica.set(procesMinLru * 16 + (brStrMinLru - 1),
							Integer.valueOf(staraVrijednostTablica).shortValue());
					//System.out.println("nova vrijednost tablica " + tablica.get(procesMinLru * 16 + (brStrMinLru - 1)));
					//System.out.println("Nova vrijednost tablica: " + staraVrijednostTablica);
					List<Short> listaZaStavljanjeStraniceNaDisk = okviri.get(indOkvStraniceZaVan);
					int indeks = procesMinLru * 512 + 32 * (brStrMinLru - 1);
					//stavi STARU stranicu oz okvira na disk
					for (Short element: listaZaStavljanjeStraniceNaDisk) {
						disk.set(indeks, element);
						indeks++;
						
					}

					
					/*System.out.println("Stranica za ubaciti " 
							+ disk.subList(brProcesa * 512 + 32 * brojStranice, 
									brProcesa * 512 + 32 * brojStranice + 32));*/
					okviri.put(indOkvStraniceZaVan, disk.subList(brProcesa * 512 + 32 * brojStranice, 
							brProcesa * 512 + 32 * brojStranice + 32));
					//azuriraj novu vrijednost u okviru
					Short sadrzajUOkviru = okviri.get(indOkvStraniceZaVan).get(niziBitoviFizAdr / 2);
					System.out.println("Sadrzaj adrese: " + sadrzajUOkviru);
					sadrzajUOkviru++;
					//System.out.println("Nova vrijednost " +  sadrzajUOkviru);
					//ubacujem NOVU stranicu na disk, a vec je stavljena i u okvir
					listaZaStavljanjeStraniceNaDisk = okviri.get(indOkvStraniceZaVan);
					listaZaStavljanjeStraniceNaDisk.set(niziBitoviFizAdr / 2, sadrzajUOkviru);
					indeks = brProcesa * 512 + brojStranice;
					for (Short element: listaZaStavljanjeStraniceNaDisk) {
						disk.set(indeks, element);
						indeks++;
						
					}
					//System.out.println("Stranica diska nakon azuriranja " + listaZaStavljanjeStraniceNaDisk);
					//System.out.println("Stranica u okviru nakon azuriranja " + okviri.get(indOkvStraniceZaVan));
					Integer podZaTablicu = t | (1 << 5) | (indOkvStraniceZaVan << 6);
					//System.out.println("Podatak za tablicu: " + (podZaTablicu));
					tablica.set(brProcesa * 16 + brojStranice, podZaTablicu.shortValue());
					System.out.println("Zapis tablice: " + tablica.get(brProcesa * 16 + brojStranice) + " = "
							+ Integer.toHexString(tablica.get(brProcesa * 16 + brojStranice)));
					
				}
				System.out.println("--------------------------------------------");
				
			}

			else {
				System.out.println("Pogodak!");
				//ako sam pogodio, moram azurirati LRU u tablici prevodenja i izmijenjenu ju staviti na disk
				Short podIzTablice = tablica.get(brProcesa * 16 + brojStranice);
				System.out.println("Stari podatak u tablici = " + podIzTablice);
				podIzTablice = Integer.valueOf(podIzTablice & (-32)).shortValue();
				podIzTablice = Integer.valueOf(podIzTablice | t).shortValue();
				System.out.println("Novi podatak u tablici = " + podIzTablice);
				tablica.set(brProcesa * 16 + brojStranice, podIzTablice);
				System.out.println("--------------------------------------------");
			}
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			t++;
			if (t == 32) {
				for (int i = 0; i < tablica.size(); i = i + 2) {
					tablica.set(i, Integer.valueOf(tablica.get(i) & (-32)).shortValue());
				}
				
				tablica.set(brProcesa  * 16 + brojStranice, 
						Integer.valueOf(tablica.get(brProcesa * 16 + brojStranice) & (-31)).shortValue());
			}
		}
		
		
		
	}

}
