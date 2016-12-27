
public class Kontroll {
	private int r;
	private int v;
	private String[][] tabel;

	public Kontroll(int r, int v) {
		super();
		this.r = r;
		this.v = v;
		this.tabel = new String[r][v];
		uusM�ng();
	}
	public String[][] getTabel() {
		return tabel;
	}

	public void setTabel(String[][] tabel) {
		this.tabel = tabel;
	}
	public void insertTabel(int r, int v, String s) {
		tabel[r][v] = s;
	}
	int xcount = 0;
	int ocount = 0;
	static int j�rjest  = 4;
	
	void uusM�ng(){
		for(int i = 0; i<r; i++) 									// k�ime m�lemad listid l�bi
			for(int j = 0; j<v; j++)
				tabel[i][j] = "_";
	}
	
	String kontroll(){ 							// meetod k�ikide v�imaluste kontrolliks
		String pv = paremVasak(); 
		if(!pv.equals(""))								// kui kusagil on v�it tuvastatud, pole edasi m�tet vaadata
			return pv;
		String �a = �lesAlla();
		if(!�a.equals(""))
			return �a;
		String dvp = diagonaalVP();
		if(!dvp.equals(""))
			return dvp;
		String dpv = diagonaalPV();
		if(!dpv.equals(""))
			return dpv;		
		String viik = viik();
		if(!viik.equals(""))
			return viik;
		return "";										// kui �kski juhtum pole t�idetud tagastab t�his�ne
		
		
	}
	private String paremVasak(){							// vaatame palju horisontaalselt j�rjest on
														// esimene ts�kkel muudab rida, teine veergu
		for(int i = 0; i<r; i++){												// vaatame rida
			xcount = ocount = 0; // uue rea korral nullime loendurid
			for(int j =0; j<v-1; j++){ 											// vaatame rea elementi
				if(tabel[i][j].equals("x")&&tabel[i][j+1].equals("x")) 
					xcount++;
				else if (tabel[i][j].equals("o")&&tabel[i][j+1].equals("o"))
					ocount++;
				else
					xcount=ocount= 0;
				
																				// igal sammul vaatame, kas vajaminev arv on t�is
				if(xcount>=j�rjest-1) 
					return "X v�itis";
				else if (ocount>=j�rjest-1)
					return "O v�itis";
				}
			}
		return "";
	}
	private String �lesAlla(){
																		// esimene ts�kkel muudab veergu, teine ts�kkel rida
		for(int i = 0; i<v; i++){ 												// vaatame rida
			xcount = ocount = 0; 
			for(int j =0; j<r-1; j++){ 											// vaatame rea elementi
				if(tabel[j][i].equals("x")&&tabel[j+1][i].equals("x")) 
					xcount++;
				else if (tabel[j][i].equals("o")&&tabel[j+1][i].equals("o"))
					ocount++;
				else
					xcount = ocount = 0;
				
				if(xcount>=j�rjest-1)
					return "X v�itis";
				else if (ocount>=j�rjest-1)
					return "O v�itis";
			}

		}
		return "";
	}
	private String diagonaalVP(){									//kontrollib �levalt vasakult alla paremale poole
																	// vaatab elemente peadiagonaalil ja allpool seda
																			// esimene ts�kkel on seotud ridadega, teine ts�kkel� seotud veergudega
		for(int i = 0; i <Math.min(r,v)-1; i++){ 
			xcount = ocount = 0;											 // iga kord kui loendamist alustame uuelt realt, nullime eelmised tulemused
			for(int j=0; j<Math.min(r,v)-i-1;j++ ){							 //j<Math.min(r,v)-i-1 sest iga ts�kkel tuleb v�hem elemente l�bi vaadata ning see oleneb muutujast i
				if(tabel[i+j][j].equals("x")&&tabel[i+1+j][j+1].equals("x")) //
					xcount++;
				else if (tabel[i+j][j].equals("o")&&tabel[i+1+j][j+1].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				
				if(xcount>=j�rjest-1)
					return "X v�itis";
				else if (ocount>=j�rjest-1)
					return "O v�itis";
			}

		}
																				// vaatab elemente �levalpool peadiagonaali
		for(int i = 1; i < Math.min(r, v)-1; i++){
			xcount = ocount = 0;
			for(int j=0; j<Math.min(r, v)-1;j++ ){
				if(j+i+1<Math.max(r, v)){
				if(tabel[j][j+i].equals("x")&&tabel[j+1][j+1+i].equals("x")) 	// sama mis eelmine, ainult et tabeli ja veeru muutujad ([i+j][j]) on �ra vahetatud 
					xcount++;
				else if (tabel[j][j+i].equals("o")&&tabel[j+1][j+1+i].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				
				if(xcount>=j�rjest-1)
					return "X v�itis";
				else if (ocount>=j�rjest-1)
					return "O v�itis";
				}
			}

		}
		return "";
	}
	private String diagonaalPV(){ 										//kontrollib �levalt vasakult alla paremale poole
																		// vaatab elemente peadiagonaalil ja allpool seda															// esimest ts�kklit on vaja ridade jaoks, teist veergude jaoks
		for(int i = 0; i <Math.min(r,v)-1; i++){ 
			xcount = ocount = 0; 												// iga kord kui loendamist alustame uuelt realt, nullime eelmised tulemused
			for(int j=v-1,k=i; j>0+i&&k<r-1;j--,k++){ 							//j<Math.min(r,v)-i-1 sest iga ts�kkel tuleb v�hem elemente l�bi vaadata ning see oleneb muutujast i
				if(tabel[k][j].equals("x")&&tabel[k+1][j-1].equals("x")) //
					xcount++;
				else if (tabel[k][j].equals("o")&&tabel[k+1][j-1].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				if(xcount>=j�rjest-1)
					return "X v�itis";
				else if (ocount>=j�rjest-1)
					return "O v�itis";
			}

		}
		for(int i = 0; i < v-1; i++){ 											// i muutub veergude j�rgi
			xcount = ocount = 0; 												// iga kord kui loendamist alustame uuelt realt, nullime eelmised tulemused
			for(int j=v-1,k=0; j>0+i&&k<r-1;j--,k++){						    // k on ridade indeks
				if(tabel[k][j-i].equals("x")&&tabel[k+1][j-i-1].equals("x")) 
					xcount++;
				else if (tabel[k][j-i].equals("o")&&tabel[k+1][j-i-1].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				if(xcount>=j�rjest-1)
					return "X v�itis";
				else if (ocount>=j�rjest-1)
					return "O v�itis";
			}

		}
		return "";

	}	
	private String viik(){			// viigi kontrollimine
		int t�hicount = 0;			// kui palju kohti veel t�hi on
		for(String[] rida: tabel){ 	
			for(String elem: rida){
				if(elem.equals("_"))
					t�hicount++; 	// kui leiame t�hja koha siis suurename loendurit
			}
		}
		if(t�hicount==0)
			return "Viik"; 			// kui �htegi t�hja kohta pole tagastame viig
		else
			return "";
	}
}
