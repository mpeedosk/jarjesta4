
public class Kontroll {
	private int r;
	private int v;
	private String[][] tabel;

	public Kontroll(int r, int v) {
		super();
		this.r = r;
		this.v = v;
		this.tabel = new String[r][v];
		uusMäng();
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
	static int järjest  = 4;
	
	void uusMäng(){
		for(int i = 0; i<r; i++) 									// käime mõlemad listid läbi
			for(int j = 0; j<v; j++)
				tabel[i][j] = "_";
	}
	
	String kontroll(){ 							// meetod kõikide võimaluste kontrolliks
		String pv = paremVasak(); 
		if(!pv.equals(""))								// kui kusagil on võit tuvastatud, pole edasi mõtet vaadata
			return pv;
		String üa = ülesAlla();
		if(!üa.equals(""))
			return üa;
		String dvp = diagonaalVP();
		if(!dvp.equals(""))
			return dvp;
		String dpv = diagonaalPV();
		if(!dpv.equals(""))
			return dpv;		
		String viik = viik();
		if(!viik.equals(""))
			return viik;
		return "";										// kui ükski juhtum pole täidetud tagastab tühisõne
		
		
	}
	private String paremVasak(){							// vaatame palju horisontaalselt järjest on
														// esimene tsükkel muudab rida, teine veergu
		for(int i = 0; i<r; i++){												// vaatame rida
			xcount = ocount = 0; // uue rea korral nullime loendurid
			for(int j =0; j<v-1; j++){ 											// vaatame rea elementi
				if(tabel[i][j].equals("x")&&tabel[i][j+1].equals("x")) 
					xcount++;
				else if (tabel[i][j].equals("o")&&tabel[i][j+1].equals("o"))
					ocount++;
				else
					xcount=ocount= 0;
				
																				// igal sammul vaatame, kas vajaminev arv on täis
				if(xcount>=järjest-1) 
					return "X võitis";
				else if (ocount>=järjest-1)
					return "O võitis";
				}
			}
		return "";
	}
	private String ülesAlla(){
																		// esimene tsükkel muudab veergu, teine tsükkel rida
		for(int i = 0; i<v; i++){ 												// vaatame rida
			xcount = ocount = 0; 
			for(int j =0; j<r-1; j++){ 											// vaatame rea elementi
				if(tabel[j][i].equals("x")&&tabel[j+1][i].equals("x")) 
					xcount++;
				else if (tabel[j][i].equals("o")&&tabel[j+1][i].equals("o"))
					ocount++;
				else
					xcount = ocount = 0;
				
				if(xcount>=järjest-1)
					return "X võitis";
				else if (ocount>=järjest-1)
					return "O võitis";
			}

		}
		return "";
	}
	private String diagonaalVP(){									//kontrollib ülevalt vasakult alla paremale poole
																	// vaatab elemente peadiagonaalil ja allpool seda
																			// esimene tsükkel on seotud ridadega, teine tsükkelö seotud veergudega
		for(int i = 0; i <Math.min(r,v)-1; i++){ 
			xcount = ocount = 0;											 // iga kord kui loendamist alustame uuelt realt, nullime eelmised tulemused
			for(int j=0; j<Math.min(r,v)-i-1;j++ ){							 //j<Math.min(r,v)-i-1 sest iga tsükkel tuleb vähem elemente läbi vaadata ning see oleneb muutujast i
				if(tabel[i+j][j].equals("x")&&tabel[i+1+j][j+1].equals("x")) //
					xcount++;
				else if (tabel[i+j][j].equals("o")&&tabel[i+1+j][j+1].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				
				if(xcount>=järjest-1)
					return "X võitis";
				else if (ocount>=järjest-1)
					return "O võitis";
			}

		}
																				// vaatab elemente ülevalpool peadiagonaali
		for(int i = 1; i < Math.min(r, v)-1; i++){
			xcount = ocount = 0;
			for(int j=0; j<Math.min(r, v)-1;j++ ){
				if(j+i+1<Math.max(r, v)){
				if(tabel[j][j+i].equals("x")&&tabel[j+1][j+1+i].equals("x")) 	// sama mis eelmine, ainult et tabeli ja veeru muutujad ([i+j][j]) on ära vahetatud 
					xcount++;
				else if (tabel[j][j+i].equals("o")&&tabel[j+1][j+1+i].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				
				if(xcount>=järjest-1)
					return "X võitis";
				else if (ocount>=järjest-1)
					return "O võitis";
				}
			}

		}
		return "";
	}
	private String diagonaalPV(){ 										//kontrollib ülevalt vasakult alla paremale poole
																		// vaatab elemente peadiagonaalil ja allpool seda															// esimest tsükklit on vaja ridade jaoks, teist veergude jaoks
		for(int i = 0; i <Math.min(r,v)-1; i++){ 
			xcount = ocount = 0; 												// iga kord kui loendamist alustame uuelt realt, nullime eelmised tulemused
			for(int j=v-1,k=i; j>0+i&&k<r-1;j--,k++){ 							//j<Math.min(r,v)-i-1 sest iga tsükkel tuleb vähem elemente läbi vaadata ning see oleneb muutujast i
				if(tabel[k][j].equals("x")&&tabel[k+1][j-1].equals("x")) //
					xcount++;
				else if (tabel[k][j].equals("o")&&tabel[k+1][j-1].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				if(xcount>=järjest-1)
					return "X võitis";
				else if (ocount>=järjest-1)
					return "O võitis";
			}

		}
		for(int i = 0; i < v-1; i++){ 											// i muutub veergude järgi
			xcount = ocount = 0; 												// iga kord kui loendamist alustame uuelt realt, nullime eelmised tulemused
			for(int j=v-1,k=0; j>0+i&&k<r-1;j--,k++){						    // k on ridade indeks
				if(tabel[k][j-i].equals("x")&&tabel[k+1][j-i-1].equals("x")) 
					xcount++;
				else if (tabel[k][j-i].equals("o")&&tabel[k+1][j-i-1].equals("o"))
					ocount++;
				else 
					xcount = ocount=0;
				if(xcount>=järjest-1)
					return "X võitis";
				else if (ocount>=järjest-1)
					return "O võitis";
			}

		}
		return "";

	}	
	private String viik(){			// viigi kontrollimine
		int tühicount = 0;			// kui palju kohti veel tühi on
		for(String[] rida: tabel){ 	
			for(String elem: rida){
				if(elem.equals("_"))
					tühicount++; 	// kui leiame tühja koha siis suurename loendurit
			}
		}
		if(tühicount==0)
			return "Viik"; 			// kui ühtegi tühja kohta pole tagastame viig
		else
			return "";
	}
}
