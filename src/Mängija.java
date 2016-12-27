import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

// klass Mängija kirjeldab mängijat
public enum Mängija {

	// mängija kelle värv on punane
	mängija_1(new SimpleObjectProperty<Color>(Color.RED)),
	
	// mängija kelle värv on kollane
	mängija_2(new SimpleObjectProperty<Color>(Color.YELLOW));
	
	
	private SimpleObjectProperty<Color> värv;

	private Mängija(SimpleObjectProperty<Color> värv) {
		this.värv = värv;
	}

	public SimpleObjectProperty<Color> getVärv() {
		return värv;
	}
	
	// tagastab järgmise käigul oleva mängija värvi
	
	// parameetriks on praeguse mängija värv
	// tagastatakse värv, mis pole praegune mängija
	
	
	public static Mängija vahetaKord(Mängija m) {
		if (m == mängija_1) 
			return mängija_2;
		else 
			return mängija_1;
		
	}
}