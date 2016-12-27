import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

// klass M�ngija kirjeldab m�ngijat
public enum M�ngija {

	// m�ngija kelle v�rv on punane
	m�ngija_1(new SimpleObjectProperty<Color>(Color.RED)),
	
	// m�ngija kelle v�rv on kollane
	m�ngija_2(new SimpleObjectProperty<Color>(Color.YELLOW));
	
	
	private SimpleObjectProperty<Color> v�rv;

	private M�ngija(SimpleObjectProperty<Color> v�rv) {
		this.v�rv = v�rv;
	}

	public SimpleObjectProperty<Color> getV�rv() {
		return v�rv;
	}
	
	// tagastab j�rgmise k�igul oleva m�ngija v�rvi
	
	// parameetriks on praeguse m�ngija v�rv
	// tagastatakse v�rv, mis pole praegune m�ngija
	
	
	public static M�ngija vahetaKord(M�ngija m) {
		if (m == m�ngija_1) 
			return m�ngija_2;
		else 
			return m�ngija_1;
		
	}
}