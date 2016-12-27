import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

public class M�ng extends Application{

	//muutujad

	private Kontroll kontroll = new Kontroll(5,7); 				// kontroll isendi loomine v�idu kontrollimiseks
	private String seis = "";									// m�ngualgseis "" - v�itjat pole, "X v�itis" - esimese m�ngija v�it, "O v�itis" - teise m�ngija v�it, "-" v�it oli juba �ra aga m�ngitakse edasi
	private String v�itja = "";									// m�ngu v�itnud m�ngija v�rv
	private M�ngija kord = M�ngija.m�ngija_1;					// isend m�ngija vahetamiseks
	private Color m1_v�rv = M�ngija.m�ngija_1.getV�rv().get();	// m�ngija 1 v�rv
	private Color m2_v�rv = M�ngija.m�ngija_2.getV�rv().get();	// m�ngija 2_v�rv
	private SimpleObjectProperty<Color> �lemine = new SimpleObjectProperty<Color>(m1_v�rv); // �lemiste ringide v�rv
	boolean load_vajutatud = false;								// keypress allhoidmise takistamine, kui �he korra vajutad, paneb v��rtuse true-ks
	boolean save_vajutatud = false;
	boolean abi_vajutatud  = false;
	boolean uusm�ng_vajutatud = false;

	public void start(Stage stage) throws Exception {

		//tiitel
		stage.setTitle("J�rjesta neli"); 					

		// juur
		BorderPane juur = new BorderPane();

		// stseen
		Scene stseen = new Scene(juur, 800, 600); //!!

		// tausta pildi laadimine, keskele seadmine ning m��tmed
		stseen.getRoot().setStyle("-fx-background-image: url('Taust.png');-fx-background-position: center center; -fx-background-repeat: no-repeat; -fx-background-size: 800px 600px;");

		// nupud
		Button uusm�ng = new Button("Uus m�ng");
		// et nuppude suurus oleks k�igil sama
		uusm�ng.setMaxWidth(Double.MAX_VALUE);
		uusm�ng.getStylesheets().add("stiil.css");		// kasutame nuppude muutmiseks css faili

		Button abi = new Button("Abi");
		abi.setMaxWidth(Double.MAX_VALUE);
		abi.getStylesheets().add("stiil.css");

		Button v�lju = new Button("V�lju");
		v�lju.getStylesheets().add("stiil.css");
		v�lju.setMaxWidth(Double.MAX_VALUE);

		Button save = new Button("Salvesta");
		save.getStylesheets().add("stiil.css");
		save.setMaxWidth(Double.MAX_VALUE);

		Button load = new Button("Laadi");
		load.getStylesheets().add("stiil.css");
		load.setMaxWidth(Double.MAX_VALUE);

		// nuppude asukoht

		VBox vbox = new VBox();
		vbox.setSpacing(5);	// nuppude vahe
		vbox.setPadding(new Insets(0, 20, 10, 20));	 // nuppude asukoha nihutamine paremale
		vbox.getChildren().addAll(uusm�ng, abi,save, load,v�lju);
		juur.setLeft(vbox);
		vbox.translateYProperty().bind(stseen.heightProperty().subtract(stseen.getHeight()/2+140)); // seome vBoxi stseeniga, et kui stseeni suurus muutub, siis muutub ka nuppude asukoht

		// ruudustik ringide jaoks
		GridPane grid = new GridPane();
		grid.setTranslateX(-16);						// positsiooni optimiseerimine
		grid.setTranslateY(48);
		grid.setAlignment(Pos.CENTER);					// ruudustik on alati keskel

		grid.setHgap(2);								// v�ikesed vahed ringide vahel
		grid.setVgap(2);

		// veerud 
		grid.getColumnConstraints().addAll(
				new ColumnConstraints(80,80,Double.MAX_VALUE), 
				new ColumnConstraints(80,80,Double.MAX_VALUE), 	
				new ColumnConstraints(80,80,Double.MAX_VALUE), 
				new ColumnConstraints(80,80,Double.MAX_VALUE), 
				new ColumnConstraints(80,80,Double.MAX_VALUE), 
				new ColumnConstraints(80,80,Double.MAX_VALUE), 
				new ColumnConstraints(80,80,Double.MAX_VALUE));

		// read
		grid.getRowConstraints().addAll(
				new RowConstraints(80,80,Double.MAX_VALUE), 
				new RowConstraints(80,80,Double.MAX_VALUE), 
				new RowConstraints(80,80,Double.MAX_VALUE), 
				new RowConstraints(80,80,Double.MAX_VALUE), 
				new RowConstraints(80,80,Double.MAX_VALUE));   

		juur.setCenter(grid);
		kuvaRuudustik(grid);							// tekitame ruudustiku

		// s�ndmuste lisamine

		// m�ngu salvestamine ja nupp

		// hiirega nupu peale vajutamine
		save.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent arg0) {
				try {
					salvesta();							// salvestame m�ngu seisu faili
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		// klaviatuuri nupu alla vajutamine
		save.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {

				// vaatab kas nupu peale on varem vajutatud ning kas nupuks on "enter"
				if (!save_vajutatud && ke.getCode().equals(KeyCode.ENTER))
				{
					save_vajutatud = true;
					try {
						salvesta();
					} catch (IOException e) {
						e.printStackTrace();
					}													// salvestame m�ngu seisu faili	
				}		    
			}
		});

		// klaviatuuri nupu lahti laskmine
		save.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if(ke.getCode().equals(KeyCode.ENTER)){
					save_vajutatud = false; // teeb teatavaks, et nupu peale saab uuesti vajutada
				}

			}
		});


		// laadimis nupp
		// nupu peale vajutamine hiirega
		load.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {

				if (!load_vajutatud && ke.getCode().equals(KeyCode.ENTER))
				{
					load_vajutatud = true;
					try {
						kontroll.setTabel(loe());				// tabeli seadmine
					} catch (ClassNotFoundException | IOException e) {
						e.printStackTrace();
					}			

					laadiRuudustik(grid, kontroll.getTabel());	// ruudustiku loomine
					v�itja = "";								// v�itja algv��rtuse taastamine	
					�lemine.set(kord.getV�rv().get());			// �lemiste ringide v�rvi muutmine
				}		    
			}
		});

		load.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if(ke.getCode().equals(KeyCode.ENTER)){
					load_vajutatud = false;
				}

			}
		});

		load.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent arg0) {
				try {

					kontroll.setTabel(loe());					// tabeli seadmine
					laadiRuudustik(grid, kontroll.getTabel());	// ruudustiku loomine
					v�itja = "";								// v�itja algv��rtuse taastamine	
					�lemine.set(kord.getV�rv().get());			// �lemiste ringide v�rvi muutmine

				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}

			}
		});

		uusm�ng.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {

				if (!uusm�ng_vajutatud && ke.getCode().equals(KeyCode.ENTER))
				{
					uusm�ng_vajutatud = true;	
					kuvaRuudustik(grid);							// ruudustiku algv��rtuse taastamine
					kontroll.uusM�ng();								// tabeli algv��rtuse taastamine
					v�itja = "";									// v�itja algv��rtuse taastamine
				}		    
			}
		});

		uusm�ng.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if(ke.getCode().equals(KeyCode.ENTER)){
					uusm�ng_vajutatud = false;
				}

			}
		});

		// uue m�ngu loomine
		uusm�ng.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent arg0) {
				kuvaRuudustik(grid);							// ruudustiku algv��rtuse taastamine
				kontroll.uusM�ng();								// tabeli algv��rtuse taastamine
				v�itja = "";									// v�itja algv��rtuse taastamine
			}
		});

		// abiinfo kuvamine
		abi.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {

				if (!abi_vajutatud && ke.getCode().equals(KeyCode.ENTER))
				{			// luuakse teine lava

					Stage kiri = new Stage();
					kiri.setResizable(false);
					kiri.setTitle("Abi");
					kiri.initModality(Modality.WINDOW_MODAL);
					kiri.initOwner(juur.getScene().getWindow());

					// k�simuse ja kahe nupu loomine

					Label label = new Label("J�rjesta neli! \n\n" + "V�idab see, kes saab neli enda\n" + "nuppu j�rjest, kas vertikaalselt,\n" 
							+ "horisontaalselt v�i diagonaalselt.\n\n"+"V�imalikke j�rgmisi k�ike \nn�itab heledam ring.\n\n");
					Button selge = new Button("Selge");
					// nupu stiili muutmine

					selge.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							kiri.close();
						}
					});
					selge.setOnKeyPressed(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent ke) {

							if (ke.getCode().equals(KeyCode.ENTER))
							{
								kiri.hide(); 
							}
						}
					});
					

					// k�simuse ja nuppude gruppi paigutamine
					VBox vBox = new VBox();

					vBox.setSpacing(5);						// v�iksed vahed teksti ja nuppude vahel
					vBox.setPrefWidth(200);					// eelistatud laius
					vBox.setPrefHeight(220);					// eelistatud k�rgus
					vBox.setAlignment(Pos.CENTER);			// positsioon keskel
					vBox.getChildren().addAll(label, selge);

					//stseeni loomine ja n�itamine
					Scene stseen2 = new Scene(vBox);
					kiri.setScene(stseen2);
					kiri.show();


				}		    
			}
		});

		abi.setOnKeyReleased(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {
				if(ke.getCode().equals(KeyCode.ENTER)){
					abi_vajutatud = false;
				}

			}
		});

		abi.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent arg0) {
				// luuakse teine lava

				Stage kiri = new Stage();
				kiri.setResizable(false);
				kiri.setTitle("Abi");
				kiri.initModality(Modality.WINDOW_MODAL);
				kiri.initOwner(juur.getScene().getWindow());

				// k�simuse ja kahe nupu loomine

				Label label = new Label("J�rjesta neli! \n\n" + "V�idab see, kes saab neli enda\n" + "nuppu j�rjest, kas vertikaalselt,\n" 
						+ "horisontaalselt v�i diagonaalselt.\n\n"+"V�imalikke j�rgmisi k�ike \nn�itab heledam ring.\n\n");
				Button selge = new Button("Selge");
				// nupu stiili muutmine

				selge.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						kiri.close();
					}
				});
				selge.setOnKeyPressed(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {

						if (ke.getCode().equals(KeyCode.ENTER))
						{
							kiri.hide(); 
						}
					}
				});



				// k�simuse ja nuppude gruppi paigutamine
				VBox vBox = new VBox();

				vBox.setSpacing(5);						// v�iksed vahed teksti ja nuppude vahel
				vBox.setPrefWidth(200);					// eelistatud laius
				vBox.setPrefHeight(220);					// eelistatud k�rgus
				vBox.setAlignment(Pos.CENTER);			// positsioon keskel
				vBox.getChildren().addAll(label, selge);

				//stseeni loomine ja n�itamine
				Scene stseen2 = new Scene(vBox);
				kiri.setScene(stseen2);
				kiri.show();
			}
		});

		// m�ngu sulgemine 
		v�lju.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent ke) {

				if (ke.getCode().equals(KeyCode.ENTER))
				{

					// praktikumi n�ite p�hjal
					Stage kusimus = new Stage();

					// tagumisele aknale vahetamise keelamine
					kusimus.initModality(Modality.WINDOW_MODAL);
					kusimus.initOwner(juur.getScene().getWindow());

					// k�simuse ja kahe nupu loomine
					Label label = new Label("Kas t�esti tahad kinni panna?");
					Button okButton = new Button("Jah");
					Button cancelButton = new Button("Ei");

					// s�ndmuste lisamine
					okButton.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							kusimus.hide(); 
							stage.close();
						}
					});

					okButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent ke) {

							if (ke.getCode().equals(KeyCode.ENTER))
							{
								kusimus.hide(); 
								stage.close();	
							}
						}
					});

					cancelButton.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							v�lju.setDisable(false);
							kusimus.hide();
						}
					});

					cancelButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
						public void handle(KeyEvent ke) {

							if (ke.getCode().equals(KeyCode.ENTER))
							{
								v�lju.setDisable(false);
								kusimus.hide();
							}
						}
					});

					kusimus.setOnHiding(new EventHandler<WindowEvent>() {
						public void handle(WindowEvent event) {
							v�lju.setDisable(false);
						}
					});

					// nuppude paiutus
					FlowPane pane = new FlowPane(10, 10);
					pane.setAlignment(Pos.CENTER);
					pane.getChildren().addAll(okButton, cancelButton);

					VBox vBox = new VBox(10);
					vBox.setAlignment(Pos.CENTER);
					vBox.getChildren().addAll(label, pane);

					//stseeni loomine ja kuvamine
					Scene stseen2 = new Scene(vBox);
					kusimus.setScene(stseen2);
					kusimus.show();	
				}		    
			}
		});


		v�lju.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent event) {
				// praktikumi n�ite p�hjal
				Stage kusimus = new Stage();

				// tagumisele aknale vahetamise keelamine
				kusimus.initModality(Modality.WINDOW_MODAL);
				kusimus.initOwner(juur.getScene().getWindow());

				// k�simuse ja kahe nupu loomine
				Label label = new Label("Kas t�esti tahad kinni panna?");
				Button okButton = new Button("Jah");
				Button cancelButton = new Button("Ei");

				// s�ndmuste lisamine
				okButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						kusimus.hide(); 
						stage.close();
					}
				});
				okButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {

						if (ke.getCode().equals(KeyCode.ENTER))
						{
							kusimus.hide(); 
							stage.close();	
						}
					}
				});

				cancelButton.setOnAction(new EventHandler<ActionEvent>() {
					public void handle(ActionEvent event) {
						v�lju.setDisable(false);
						kusimus.hide();
					}
				});


				cancelButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
					public void handle(KeyEvent ke) {

						if (ke.getCode().equals(KeyCode.ENTER))
						{
							v�lju.setDisable(false);
							kusimus.hide();
						}
					}
				});

				kusimus.setOnHiding(new EventHandler<WindowEvent>() {
					public void handle(WindowEvent event) {
						v�lju.setDisable(false);
					}
				});

				// nuppude paiutus
				FlowPane pane = new FlowPane(10, 10);
				pane.setAlignment(Pos.CENTER);
				pane.getChildren().addAll(okButton, cancelButton);

				VBox vBox = new VBox(10);
				vBox.setAlignment(Pos.CENTER);
				vBox.getChildren().addAll(label, pane);

				//stseeni loomine ja kuvamine
				Scene stseen2 = new Scene(vBox);
				kusimus.setScene(stseen2);
				kusimus.show();
			}


		});		


		grid.setOnMouseClicked(new EventHandler<MouseEvent>(){
			public void handle(MouseEvent arg0) {
				//seisu kontrollimine
				seis = kontroll.kontroll();

				//vaatame, kas keegi on v�itnud
				if(seis.equals("X v�itis")&&!v�itja.equals("-")){		
					v�itja = "Punane";

					//otsime �les millised on v�itja ringid ning lisame neile hajumisefekti
					for(Node sp: grid.getChildren()){						// grid koosneb StackPane-dest
						for(Node n: ((StackPane)sp).getChildren())			// igas StackPanes on 2 ringi
							if(((Circle) n).getFill() == m1_v�rv){		// vaatame, kas v�rv on sobiv
								FadeTransition ft2 = new FadeTransition(Duration.millis(2000),((Circle) n));
								ft2.setFromValue(0.3);  				// m��ratakse algv��rtus (1.0 - t�iesti selge)
								ft2.setToValue(1.0); 					// m��ratakse l�ppv��rtus (0 - t�iesti haihtunud)
								ft2.setCycleCount(Timeline.INDEFINITE); // l�pmatu ts�klite arv
								ft2.setAutoReverse(true);	
								ft2.play();
							}

					}

				}

				// sama mis eelminegi
				else if (seis.equals("O v�itis")&&!v�itja.equals("-")){
					v�itja = "Kollane";
					for(Node sp: grid.getChildren()){
						for(Node n: ((StackPane)sp).getChildren())
							if(((Circle) n).getFill() == m2_v�rv){
								FadeTransition ft2 = new FadeTransition(Duration.millis(2000),((Circle) n));
								ft2.setFromValue(0.3); 	
								ft2.setToValue(1.0);
								ft2.setCycleCount(Timeline.INDEFINITE);  
								ft2.setAutoReverse(true);
								ft2.play();		
							}				
					}
				}

				// kui tuleb viik
				else if(seis.equals("Viik")&&!v�itja.equals("-")){
					v�itja = "Viik";
				}

				if(v�itja.equals("Kollane")||v�itja.equals("Punane")){

					// luuakse teine lava

					Stage kiri = new Stage();
					kiri.setTitle("V�itja!");
					kiri.initModality(Modality.WINDOW_MODAL);
					kiri.initOwner(juur.getScene().getWindow());

					// k�simuse ja kahe nupu loomine

					Label label = new Label(v�itja + " v�itis!");
					Button v�it = new Button("Okei");
					// nupu stiili muutmine
					v�it.setStyle("-fx-text-fill: green;");

					v�it.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							kiri.close();
							v�itja = "-";
						}
					});

					kiri.setOnHiding(new EventHandler<WindowEvent>() {
						public void handle(WindowEvent event) {
							v�itja = "-";
						}
					});

					// k�simuse ja nuppude gruppi paigutamine
					VBox vBox = new VBox();

					// tausta v�rv
					if(v�itja.equals("Kollane"))
						vBox.setStyle("-fx-background-color: FFFF99;");
					else
						vBox.setStyle("-fx-background-color: FF6666;");
					vBox.setSpacing(5);						// v�iksed vahed teksti ja nuppude vahel
					vBox.setPrefWidth(170);					// eelistatud laius
					vBox.setPrefHeight(70);					// eelistatud k�rgus
					vBox.setAlignment(Pos.CENTER);			// positsioon keskel
					vBox.getChildren().addAll(label, v�it);

					//stseeni loomine ja n�itamine
					Scene stseen2 = new Scene(vBox);
					kiri.setScene(stseen2);
					kiri.show();

				}
				else if(v�itja.equals("Viik")){

					Stage kiri = new Stage();
					kiri.setTitle("Viik");
					kiri.initModality(Modality.WINDOW_MODAL);
					kiri.initOwner(juur.getScene().getWindow());

					Label label = new Label("M�ng l�ppes viigiga!");
					Button v�it = new Button("Okei");
					v�it.setStyle("-fx-text-fill: green;");

					v�it.setOnAction(new EventHandler<ActionEvent>() {
						public void handle(ActionEvent event) {
							kiri.close();
							v�itja = "-";
						}
					});

					kiri.setOnHiding(new EventHandler<WindowEvent>() {
						public void handle(WindowEvent event) {
							v�itja = "-";
						}
					});

					VBox vBox = new VBox();
					vBox.setSpacing(5);
					vBox.setPrefWidth(170);
					vBox.setPrefHeight(70);
					vBox.setAlignment(Pos.CENTER);
					vBox.getChildren().addAll(label, v�it);

					Scene stseen2 = new Scene(vBox);
					kiri.setScene(stseen2);
					kiri.show();
				}

			}
		});

		//lava
		stage.setResizable(true);
		stage.setScene(stseen);
		stage.show();
	}

	// meetod m�ngu seisu salvestamiseks
	public void salvesta() throws FileNotFoundException, IOException{
		// loome uue faili
		ObjectOutputStream v�lja = new ObjectOutputStream(new FileOutputStream("seisund.dat"));
		// kirjutame tabeli ning m�ngija korra faili
		v�lja.writeObject(kontroll.getTabel());
		v�lja.writeObject(kord);
		v�lja.close();

	}

	// meetod m�ngu seisu sisselugemiseks
	public String[][] loe() throws FileNotFoundException, IOException, ClassNotFoundException{
		// avame faili
		ObjectInputStream sisse = new ObjectInputStream(new FileInputStream("seisund.dat")); 
		// loeme kaks objekti sisse
		String[][] sisend = (String[][])sisse.readObject();
		kord = (M�ngija) sisse.readObject();
		sisse.close();
		return sisend;
	}

	public static void main(String[] args) {
		launch(args);
	}

	// objekti saamine ruudustikust objekti asukoha p�hjal
	public StackPane getNodeByIndex(int r, int v,GridPane grid) {
		Node result = null;
		for(Node node : grid.getChildren()) { 
			if(GridPane.getRowIndex(node) == r && GridPane.getColumnIndex(node) == v) {
				result = node;
				break;
			}
		}
		return (StackPane)result;
	}

	// meetod m�ngu ruudustiku seadmiseks p�rast laadimist
	private void laadiRuudustik(GridPane grid, String[][] tabel){
		kuvaRuudustik(grid); 																		// alustamine algv��rtustest
		for(int r=0; r<5; r++){																		// k�ime terve tabeli l�bi
			for(int v=0; v<7; v++){		
				if(!tabel[r][v].equals("_")){														// kui tabelis on mingi koha peal x v�i o, siis l�hme edasi
					for(Node n: getNodeByIndex(r,v,grid).getChildren()){							// otsime StackPane asukoha pealt, mis v�rdub tabeli rea ning veeru indeksiga
						Circle R = (Circle)(n);														// StackPane koosneb kahest ringist

						if(R.getRadius()==40.5){													// Ring raadiusega 40.5 on see mida me otsime
							n.setTranslateY(0);														// liigutab ringi algkohta tagasi

							if(tabel[r][v].equals("x")){											// seab ringile v�rvi vastavalt tabeli v��rtusele
								R.fillProperty().bind(new SimpleObjectProperty<Color>(m1_v�rv));
							}else{
								R.fillProperty().bind(new SimpleObjectProperty<Color>(m2_v�rv));
							}

						}
					}

				}
			}

		}
	}

	// ruudustiku kuvamine
	private void kuvaRuudustik(GridPane grid){
		grid.getChildren().clear();
		for(int r=0;r<5; r++){									// r - ridade arv
			for(int v=0; v<7; v++){								// v - veergude arv

				// meile on oluline lisamise j�rjekord ning kumb eespool on
				StackPane stack = new StackPane();				// siia paneme eelvaateringi ning tavalise ringi 

				// eelvaade
				Circle eelvaadeRing = new Circle(40.4);			// eelvaate ringil on raadius 0,1 v�rra v�iksem, et saaks hiljem teda eristada
				eelvaadeRing.setOpacity(.6);		
				eelvaadeRing.setFill(Color.TRANSPARENT); 		// tavaseisus on n�htamatu

				// kui eelvaate ringile minnakse hiirega �le, siis muudab ta v�rvi vastavalt m�ngija korrale
				eelvaadeRing.setOnMouseEntered(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent arg0) {
						eelvaadeRing.setFill(Color.WHITE);
						if(kord.getV�rv().get()==m1_v�rv){
							eelvaadeRing.setFill(m1_v�rv);
						}else{
							eelvaadeRing.setFill(m2_v�rv);
						}
					}
				});

				// kui hiir lahkub eelvaate ringilt, siis on ring taas n�htamatu
				eelvaadeRing.setOnMouseExited(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent arg0) {
						eelvaadeRing.setFill(Color.TRANSPARENT);
					}
				});


				// ringid
				Circle ring = new Circle(40.5);
				ring.fillProperty().bind(�lemine);
				ring.setOpacity(1);
				ring.setTranslateY(-(82*(r+1.5)));		// liigutame ringid �les

				// varju efekt
				DropShadow vari = new DropShadow(30, Color.BLACK);
				ring.setEffect(vari);
				
				// liikumise efekt
				TranslateTransition tr = new TranslateTransition(Duration.millis(400), ring);

				// kui �lemise ringi peale minnakse hiirega, siis n�itab temaga �hendatud eelvaate ringi, kuhu see ring vajutuse korral kukkuks
				ring.setOnMouseEntered(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent arg0) {
						eelvaadeRing.setFill(Color.WHITE);
						if(kord.getV�rv().get()==m1_v�rv){
							eelvaadeRing.setFill(m1_v�rv);
						}else{
							eelvaadeRing.setFill(m2_v�rv);
						}
					}
				});

				// kui hiir ringilt lahkub on see taas n�htamatu
				ring.setOnMouseExited(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent arg0) {
						eelvaadeRing.setFill(Color.TRANSPARENT);
					}
				});

				// kui ringi peale vajutatakse, siis ring kukkub alla
				ring.setOnMouseClicked(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent arg0) {
						// vaatame mis on selle ringi kordinaadid
						int v = GridPane.getColumnIndex(stack);
						int r = GridPane.getRowIndex(stack);

						// vaatame kas tabelis on juba sellised kordinaadid olemas, et takistada mitmekordset vajutust
						if(kontroll.getTabel()[r][v].equals("_")){

							// enne me liigutasime ringid �les, n��d asetame nad tagasi algolekusse
							tr.setToY(0);
							tr.play();

							// vaatame kelle kord on
							if(kord.getV�rv().get()==m1_v�rv){
								ring.fillProperty().bind(kord.getV�rv()); // ringi v�rviks saab m�ngija v�rv 
								�lemine.set(m2_v�rv);					  // �lemiste ringide v�rv peab ka vahetuma
								kord = M�ngija.vahetaKord(kord);		  // vahetame m�ngija korra
								kontroll.insertTabel(r, v, "x");		  // lisame tabelisse vastavasse kohta m�rgi

								// sama mis �leminegi, ainult teise m�ngija kohta
							}else{
								ring.fillProperty().bind(kord.getV�rv());
								�lemine.set(m1_v�rv);
								kord = M�ngija.vahetaKord(kord);
								kontroll.insertTabel(r, v, "o");

							}
						}
					}
				});

				// sama mis tavalise ringi oma, kuid n��d vaatab kas vajutatakse alumist, ruudustikus paiknevat ringi
				eelvaadeRing.setOnMouseClicked(new EventHandler<MouseEvent>(){
					public void handle(MouseEvent arg0) {
						int v = GridPane.getColumnIndex(stack);
						int r = GridPane.getRowIndex(stack);

						if(kontroll.getTabel()[r][v].equals("_")){
							tr.setToY(0);
							tr.play();
							if(kord.getV�rv().get() == m1_v�rv){
								ring.fillProperty().bind(M�ngija.m�ngija_1.getV�rv());
								�lemine.set(m2_v�rv);
								kord = M�ngija.vahetaKord(kord);
								kontroll.insertTabel(r, v, "x");
							}else{
								ring.fillProperty().bind(M�ngija.m�ngija_2.getV�rv());
								�lemine.set(m1_v�rv);
								kord = M�ngija.vahetaKord(kord);
								kontroll.insertTabel(r, v, "o");

							}
						}
					}
				});
				
				stack.getChildren().addAll(ring,eelvaadeRing);

				// lisame ringid ruudustikku veergu v ritta r
				grid.add(stack, v, r); 
			}
		}
	}

}