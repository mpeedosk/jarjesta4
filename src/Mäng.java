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
import javafx.scene.input.MouseButton;
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

public class Mäng extends Application {

    //muutujad

    private final int RIDA = 5;
    private final int VEERG = 7;

    private Kontroll kontroll = new Kontroll(RIDA, VEERG);                // kontroll isendi loomine võidu kontrollimiseks
    private Minimax ai = new Minimax();
    private String seis = "";                                    // mängualgseis "" - võitjat pole, "X võitis" - esimese mängija võit, "O võitis" - teise mängija võit, "-" võit oli juba ära aga mängitakse edasi
    private String võitja = "";                                    // mängu võitnud mängija värv
    private Mängija kord = Mängija.mängija_1;                    // isend mängija vahetamiseks
    private Color m1_värv = Mängija.mängija_1.getVärv().get();    // mängija 1 värv
    private Color m2_värv = Mängija.mängija_2.getVärv().get();    // mängija 2_värv
    private SimpleObjectProperty<Color> ülemine = new SimpleObjectProperty<Color>(m1_värv); // ülemiste ringide värv
    boolean load_vajutatud = false;                                // keypress allhoidmise takistamine, kui ühe korra vajutad, paneb väärtuse true-ks
    boolean save_vajutatud = false;
    boolean abi_vajutatud = false;
    boolean uusmäng_vajutatud = false;

    GridPane grid = new GridPane();
    BorderPane juur = new BorderPane();

    public void start(Stage stage) throws Exception {

        //tiitel
        stage.setTitle("Järjesta neli");

        // juur

        // stseen
        Scene stseen = new Scene(juur, 800, 600); //!!

        // tausta pildi laadimine, keskele seadmine ning mõõtmed
        stseen.getRoot().setStyle("-fx-background-image: url('Taust.png');-fx-background-position: center center; -fx-background-repeat: no-repeat; -fx-background-size: 800px 600px;");

        // nupud
        Button uusmäng = new Button("Uus mäng");
        // et nuppude suurus oleks kõigil sama
        uusmäng.setMaxWidth(Double.MAX_VALUE);
        uusmäng.getStylesheets().add("stiil.css");        // kasutame nuppude muutmiseks css faili

        Button abi = new Button("Abi");
        abi.setMaxWidth(Double.MAX_VALUE);
        abi.getStylesheets().add("stiil.css");

        Button välju = new Button("Välju");
        välju.getStylesheets().add("stiil.css");
        välju.setMaxWidth(Double.MAX_VALUE);

        Button save = new Button("Salvesta");
        save.getStylesheets().add("stiil.css");
        save.setMaxWidth(Double.MAX_VALUE);

        Button load = new Button("Laadi");
        load.getStylesheets().add("stiil.css");
        load.setMaxWidth(Double.MAX_VALUE);

        // nuppude asukoht

        VBox vbox = new VBox();
        vbox.setSpacing(5);    // nuppude vahe
        vbox.setPadding(new Insets(0, 20, 10, 20));     // nuppude asukoha nihutamine paremale
        vbox.getChildren().addAll(uusmäng, abi, save, load, välju);
        juur.setLeft(vbox);
        vbox.translateYProperty().bind(stseen.heightProperty().subtract(stseen.getHeight() / 2 + 140)); // seome vBoxi stseeniga, et kui stseeni suurus muutub, siis muutub ka nuppude asukoht

        // ruudustik ringide jaoks
        grid.setTranslateX(-16);                        // positsiooni optimiseerimine
        grid.setTranslateY(48);
        grid.setAlignment(Pos.CENTER);                    // ruudustik on alati keskel

        grid.setHgap(2);                                // väikesed vahed ringide vahel
        grid.setVgap(2);

        // veerud
        grid.getColumnConstraints().addAll(
                new ColumnConstraints(80, 80, Double.MAX_VALUE),
                new ColumnConstraints(80, 80, Double.MAX_VALUE),
                new ColumnConstraints(80, 80, Double.MAX_VALUE),
                new ColumnConstraints(80, 80, Double.MAX_VALUE),
                new ColumnConstraints(80, 80, Double.MAX_VALUE),
                new ColumnConstraints(80, 80, Double.MAX_VALUE),
                new ColumnConstraints(80, 80, Double.MAX_VALUE));

        // read
        grid.getRowConstraints().addAll(
                new RowConstraints(80, 80, Double.MAX_VALUE),
                new RowConstraints(80, 80, Double.MAX_VALUE),
                new RowConstraints(80, 80, Double.MAX_VALUE),
                new RowConstraints(80, 80, Double.MAX_VALUE),
                new RowConstraints(80, 80, Double.MAX_VALUE));

        juur.setCenter(grid);
        kuvaRuudustik(grid);                            // tekitame ruudustiku

        // sündmuste lisamine

        // mängu salvestamine ja nupp

        // hiirega nupu peale vajutamine
        save.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0) {
                try {
                    salvesta();                            // salvestame mängu seisu faili
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // klaviatuuri nupu alla vajutamine
        save.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {

                // vaatab kas nupu peale on varem vajutatud ning kas nupuks on "enter"
                if (!save_vajutatud && ke.getCode().equals(KeyCode.ENTER)) {
                    save_vajutatud = true;
                    try {
                        salvesta();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }                                                    // salvestame mängu seisu faili
                }
            }
        });

        // klaviatuuri nupu lahti laskmine
        save.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    save_vajutatud = false; // teeb teatavaks, et nupu peale saab uuesti vajutada
                }

            }
        });


        // laadimis nupp
        // nupu peale vajutamine hiirega
        load.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {

                if (!load_vajutatud && ke.getCode().equals(KeyCode.ENTER)) {
                    load_vajutatud = true;
                    try {
                        kontroll.setTabel(loe());                // tabeli seadmine
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                    }

                    laadiRuudustik(grid, kontroll.getTabel());    // ruudustiku loomine
                    võitja = "";                                // võitja algväärtuse taastamine
                    ülemine.set(kord.getVärv().get());            // ülemiste ringide värvi muutmine
                }
            }
        });

        load.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    load_vajutatud = false;
                }

            }
        });

        load.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0) {
                try {

                    kontroll.setTabel(loe());                    // tabeli seadmine
                    laadiRuudustik(grid, kontroll.getTabel());    // ruudustiku loomine
                    võitja = "";                                // võitja algväärtuse taastamine
                    ülemine.set(kord.getVärv().get());            // ülemiste ringide värvi muutmine

                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }

            }
        });

        uusmäng.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {

                if (!uusmäng_vajutatud && ke.getCode().equals(KeyCode.ENTER)) {
                    uusmäng_vajutatud = true;
                    kuvaRuudustik(grid);                            // ruudustiku algväärtuse taastamine
                    kontroll.uusMäng();                                // tabeli algväärtuse taastamine
                    võitja = "";                                    // võitja algväärtuse taastamine
                }
            }
        });

        uusmäng.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    uusmäng_vajutatud = false;
                }

            }
        });

        // uue mängu loomine
        uusmäng.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0) {
                kuvaRuudustik(grid);                            // ruudustiku algväärtuse taastamine
                kontroll.uusMäng();                                // tabeli algväärtuse taastamine
                võitja = "";                                    // võitja algväärtuse taastamine
            }
        });

        // abiinfo kuvamine
        abi.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {

                if (!abi_vajutatud && ke.getCode().equals(KeyCode.ENTER)) {            // luuakse teine lava

                    Stage kiri = new Stage();
                    kiri.setResizable(false);
                    kiri.setTitle("Abi");
                    kiri.initModality(Modality.WINDOW_MODAL);
                    kiri.initOwner(juur.getScene().getWindow());

                    // küsimuse ja kahe nupu loomine

                    Label label = new Label("Järjesta neli! \n\n" + "Võidab see, kes saab neli enda\n" + "nuppu järjest, kas vertikaalselt,\n"
                            + "horisontaalselt või diagonaalselt.\n\n" + "Võimalikke järgmisi käike \nnäitab heledam ring.\n\n");
                    Button selge = new Button("Selge");
                    // nupu stiili muutmine

                    selge.setOnAction(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event) {
                            kiri.close();
                        }
                    });
                    selge.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        public void handle(KeyEvent ke) {

                            if (ke.getCode().equals(KeyCode.ENTER)) {
                                kiri.hide();
                            }
                        }
                    });


                    // küsimuse ja nuppude gruppi paigutamine
                    VBox vBox = new VBox();

                    vBox.setSpacing(5);                        // väiksed vahed teksti ja nuppude vahel
                    vBox.setPrefWidth(200);                    // eelistatud laius
                    vBox.setPrefHeight(220);                    // eelistatud kõrgus
                    vBox.setAlignment(Pos.CENTER);            // positsioon keskel
                    vBox.getChildren().addAll(label, selge);

                    //stseeni loomine ja näitamine
                    Scene stseen2 = new Scene(vBox);
                    kiri.setScene(stseen2);
                    kiri.show();


                }
            }
        });

        abi.setOnKeyReleased(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    abi_vajutatud = false;
                }

            }
        });

        abi.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent arg0) {
                // luuakse teine lava

                Stage kiri = new Stage();
                kiri.setResizable(false);
                kiri.setTitle("Abi");
                kiri.initModality(Modality.WINDOW_MODAL);
                kiri.initOwner(juur.getScene().getWindow());

                // küsimuse ja kahe nupu loomine

                Label label = new Label("Järjesta neli! \n\n" + "Võidab see, kes saab neli enda\n" + "nuppu järjest, kas vertikaalselt,\n"
                        + "horisontaalselt või diagonaalselt.\n\n" + "Võimalikke järgmisi käike \nnäitab heledam ring.\n\n");
                Button selge = new Button("Selge");
                // nupu stiili muutmine

                selge.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        kiri.close();
                    }
                });
                selge.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent ke) {

                        if (ke.getCode().equals(KeyCode.ENTER)) {
                            kiri.hide();
                        }
                    }
                });


                // küsimuse ja nuppude gruppi paigutamine
                VBox vBox = new VBox();

                vBox.setSpacing(5);                        // väiksed vahed teksti ja nuppude vahel
                vBox.setPrefWidth(200);                    // eelistatud laius
                vBox.setPrefHeight(220);                    // eelistatud kõrgus
                vBox.setAlignment(Pos.CENTER);            // positsioon keskel
                vBox.getChildren().addAll(label, selge);

                //stseeni loomine ja näitamine
                Scene stseen2 = new Scene(vBox);
                kiri.setScene(stseen2);
                kiri.show();
            }
        });

        // mängu sulgemine
        välju.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent ke) {

                if (ke.getCode().equals(KeyCode.ENTER)) {

                    // praktikumi näite põhjal
                    Stage kusimus = new Stage();

                    // tagumisele aknale vahetamise keelamine
                    kusimus.initModality(Modality.WINDOW_MODAL);
                    kusimus.initOwner(juur.getScene().getWindow());

                    // küsimuse ja kahe nupu loomine
                    Label label = new Label("Kas tõesti tahad kinni panna?");
                    Button okButton = new Button("Jah");
                    Button cancelButton = new Button("Ei");

                    // sündmuste lisamine
                    okButton.setOnAction(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event) {
                            kusimus.hide();
                            stage.close();
                        }
                    });

                    okButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        public void handle(KeyEvent ke) {

                            if (ke.getCode().equals(KeyCode.ENTER)) {
                                kusimus.hide();
                                stage.close();
                            }
                        }
                    });

                    cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                        public void handle(ActionEvent event) {
                            välju.setDisable(false);
                            kusimus.hide();
                        }
                    });

                    cancelButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
                        public void handle(KeyEvent ke) {

                            if (ke.getCode().equals(KeyCode.ENTER)) {
                                välju.setDisable(false);
                                kusimus.hide();
                            }
                        }
                    });

                    kusimus.setOnHiding(new EventHandler<WindowEvent>() {
                        public void handle(WindowEvent event) {
                            välju.setDisable(false);
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


        välju.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                // praktikumi näite põhjal
                Stage kusimus = new Stage();

                // tagumisele aknale vahetamise keelamine
                kusimus.initModality(Modality.WINDOW_MODAL);
                kusimus.initOwner(juur.getScene().getWindow());

                // küsimuse ja kahe nupu loomine
                Label label = new Label("Kas tõesti tahad kinni panna?");
                Button okButton = new Button("Jah");
                Button cancelButton = new Button("Ei");

                // sündmuste lisamine
                okButton.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        kusimus.hide();
                        stage.close();
                    }
                });
                okButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent ke) {

                        if (ke.getCode().equals(KeyCode.ENTER)) {
                            kusimus.hide();
                            stage.close();
                        }
                    }
                });

                cancelButton.setOnAction(new EventHandler<ActionEvent>() {
                    public void handle(ActionEvent event) {
                        välju.setDisable(false);
                        kusimus.hide();
                    }
                });


                cancelButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
                    public void handle(KeyEvent ke) {

                        if (ke.getCode().equals(KeyCode.ENTER)) {
                            välju.setDisable(false);
                            kusimus.hide();
                        }
                    }
                });

                kusimus.setOnHiding(new EventHandler<WindowEvent>() {
                    public void handle(WindowEvent event) {
                        välju.setDisable(false);
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

        //lava
        stage.setResizable(true);
        stage.setScene(stseen);
        stage.show();
    }

    private void kontrolli() {
        seis = kontroll.kontroll();

        //vaatame, kas keegi on võitnud
        if (seis.equals("X võitis") && !võitja.equals("-")) {
            võitja = "Punane";

            //otsime üles millised on võitja ringid ning lisame neile hajumisefekti
            for (Node sp : grid.getChildren()) {                        // grid koosneb StackPane-dest
                for (Node n : ((StackPane) sp).getChildren())            // igas StackPanes on 2 ringi
                    if (((Circle) n).getFill() == m1_värv) {        // vaatame, kas värv on sobiv
                        FadeTransition ft2 = new FadeTransition(Duration.millis(2000), ((Circle) n));
                        ft2.setFromValue(0.3);                // määratakse algväärtus (1.0 - täiesti selge)
                        ft2.setToValue(1.0);                    // määratakse lõppväärtus (0 - täiesti haihtunud)
                        ft2.setCycleCount(Timeline.INDEFINITE); // lõpmatu tsüklite arv
                        ft2.setAutoReverse(true);
                        ft2.play();
                    }

            }

        }
        // sama mis eelminegi
        else if (seis.equals("O võitis") && !võitja.equals("-")) {
            võitja = "Kollane";
            for (Node sp : grid.getChildren()) {
                for (Node n : ((StackPane) sp).getChildren())
                    if (((Circle) n).getFill() == m2_värv) {
                        FadeTransition ft2 = new FadeTransition(Duration.millis(2000), ((Circle) n));
                        ft2.setFromValue(0.3);
                        ft2.setToValue(1.0);
                        ft2.setCycleCount(Timeline.INDEFINITE);
                        ft2.setAutoReverse(true);
                        ft2.play();
                    }
            }
        }

        // kui tuleb viik
        else if (seis.equals("Viik") && !võitja.equals("-")) {
            võitja = "Viik";
        }

        if (võitja.equals("Kollane") || võitja.equals("Punane")) {

            // luuakse teine lava

            Stage kiri = new Stage();
            kiri.setTitle("Võitja!");
            kiri.initModality(Modality.WINDOW_MODAL);
            kiri.initOwner(juur.getScene().getWindow());

            // küsimuse ja kahe nupu loomine

            Label label = new Label(võitja + " võitis!");
            Button võit = new Button("Okei");
            // nupu stiili muutmine
            võit.setStyle("-fx-text-fill: green;");

            võit.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    kiri.close();
                    võitja = "-";
                }
            });

            kiri.setOnHiding(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent event) {
                    võitja = "-";
                }
            });

            // küsimuse ja nuppude gruppi paigutamine
            VBox vBox = new VBox();

            // tausta värv
            if (võitja.equals("Kollane"))
                vBox.setStyle("-fx-background-color: FFFF99;");
            else
                vBox.setStyle("-fx-background-color: FF6666;");
            vBox.setSpacing(5);                        // väiksed vahed teksti ja nuppude vahel
            vBox.setPrefWidth(170);                    // eelistatud laius
            vBox.setPrefHeight(70);                    // eelistatud kõrgus
            vBox.setAlignment(Pos.CENTER);            // positsioon keskel
            vBox.getChildren().addAll(label, võit);

            //stseeni loomine ja näitamine
            Scene stseen2 = new Scene(vBox);
            kiri.setScene(stseen2);
            kiri.show();
            return;
        } else if (võitja.equals("Viik")) {

            Stage kiri = new Stage();
            kiri.setTitle("Viik");
            kiri.initModality(Modality.WINDOW_MODAL);
            kiri.initOwner(juur.getScene().getWindow());

            Label label = new Label("Mäng lõppes viigiga!");
            Button võit = new Button("Okei");
            võit.setStyle("-fx-text-fill: green;");

            võit.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    kiri.close();
                    võitja = "-";
                }
            });

            kiri.setOnHiding(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent event) {
                    võitja = "-";
                }
            });

            VBox vBox = new VBox();
            vBox.setSpacing(5);
            vBox.setPrefWidth(170);
            vBox.setPrefHeight(70);
            vBox.setAlignment(Pos.CENTER);
            vBox.getChildren().addAll(label, võit);

            Scene stseen2 = new Scene(vBox);
            kiri.setScene(stseen2);
            kiri.show();
            return;
        }

        if(kord.getVärv().get() == m2_värv){
            int[] käik = ai.calculateMove(kontroll.getTabel());
            System.out.println("Käik: " + käik[0] + ", " + käik[1]);

            StackPane nodeByIndex = getNodeByIndex(käik[0], käik[1], grid);
            MouseEvent event = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
                    1, true, true, true, true, true,
                    true, true, true, true, true, null);

            for (Node node : nodeByIndex.getChildren()) {
                node.fireEvent(event);
                break;
            }

        }
/*        if (kontroll.getTabel()[r][v].equals("_")) {
            tr.setToY(0);
            tr.play();
            if (kord.getVärv().get() == m1_värv) {
                ring.fillProperty().bind(Mängija.mängija_1.getVärv());
                ülemine.set(m2_värv);
                kontroll.insertTabel(r, v, "x");
            }
        }*/

    }


    // meetod mängu seisu salvestamiseks
    public void salvesta() throws FileNotFoundException, IOException {
        // loome uue faili
        ObjectOutputStream välja = new ObjectOutputStream(new FileOutputStream("seisund.dat"));
        // kirjutame tabeli ning mängija korra faili
        välja.writeObject(kontroll.getTabel());
        välja.writeObject(kord);
        välja.close();

    }

    // meetod mängu seisu sisselugemiseks
    public String[][] loe() throws FileNotFoundException, IOException, ClassNotFoundException {
        // avame faili
        ObjectInputStream sisse = new ObjectInputStream(new FileInputStream("seisund.dat"));
        // loeme kaks objekti sisse
        String[][] sisend = (String[][]) sisse.readObject();
        kord = (Mängija) sisse.readObject();
        sisse.close();
        return sisend;
    }

    public static void main(String[] args) {
        launch(args);
    }

    // objekti saamine ruudustikust objekti asukoha põhjal
    public StackPane getNodeByIndex(int r, int v, GridPane grid) {
        Node result = null;
        for (Node node : grid.getChildren()) {
            if (GridPane.getRowIndex(node) == r && GridPane.getColumnIndex(node) == v) {
                result = node;
                break;
            }
        }
        return (StackPane) result;
    }

    // meetod mängu ruudustiku seadmiseks pärast laadimist
    private void laadiRuudustik(GridPane grid, String[][] tabel) {
        kuvaRuudustik(grid);                                                                        // alustamine algväärtustest
        for (int r = 0; r < RIDA; r++) {                                                                        // käime terve tabeli läbi
            for (int v = 0; v < VEERG; v++) {
                if (!tabel[r][v].equals("_")) {                                                        // kui tabelis on mingi koha peal x või o, siis lähme edasi
                    for (Node n : getNodeByIndex(r, v, grid).getChildren()) {                            // otsime StackPane asukoha pealt, mis võrdub tabeli rea ning veeru indeksiga
                        Circle R = (Circle) (n);                                                        // StackPane koosneb kahest ringist

                        if (R.getRadius() == 40.5) {                                                    // Ring raadiusega 40.5 on see mida me otsime
                            n.setTranslateY(0);                                                        // liigutab ringi algkohta tagasi

                            if (tabel[r][v].equals("x")) {                                            // seab ringile värvi vastavalt tabeli väärtusele
                                R.fillProperty().bind(new SimpleObjectProperty<Color>(m1_värv));
                            } else {
                                R.fillProperty().bind(new SimpleObjectProperty<Color>(m2_värv));
                            }

                        }
                    }

                }
            }

        }
    }

    // ruudustiku kuvamine
    private void kuvaRuudustik(GridPane grid) {
        grid.getChildren().clear();
        for (int r = 0; r < RIDA; r++) {                                    // r - ridade arv
            for (int v = 0; v < VEERG; v++) {                                // v - veergude arv

                // meile on oluline lisamise järjekord ning kumb eespool on
                StackPane stack = new StackPane();                // siia paneme eelvaateringi ning tavalise ringi

                // eelvaade
                Circle eelvaadeRing = new Circle(40.4);            // eelvaate ringil on raadius 0,1 võrra väiksem, et saaks hiljem teda eristada
                eelvaadeRing.setOpacity(.6);
                eelvaadeRing.setFill(Color.TRANSPARENT);        // tavaseisus on nähtamatu

                // kui eelvaate ringile minnakse hiirega üle, siis muudab ta värvi vastavalt mängija korrale
                eelvaadeRing.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent arg0) {
                        eelvaadeRing.setFill(Color.WHITE);
                        if (kord.getVärv().get() == m1_värv) {
                            eelvaadeRing.setFill(m1_värv);
                        } else {
                            eelvaadeRing.setFill(m2_värv);
                        }
                    }
                });

                // kui hiir lahkub eelvaate ringilt, siis on ring taas nähtamatu
                eelvaadeRing.setOnMouseExited(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent arg0) {
                        eelvaadeRing.setFill(Color.TRANSPARENT);
                    }
                });


                // ringid
                Circle ring = new Circle(40.5);
                ring.fillProperty().bind(ülemine);
                ring.setOpacity(1);
                ring.setTranslateY(-(82 * (r + 1.5)));        // liigutame ringid üles

                // varju efekt
                DropShadow vari = new DropShadow(30, Color.BLACK);
                ring.setEffect(vari);

                // liikumise efekt
                TranslateTransition tr = new TranslateTransition(Duration.millis(400), ring);

                // kui ülemise ringi peale minnakse hiirega, siis näitab temaga ühendatud eelvaate ringi, kuhu see ring vajutuse korral kukkuks
                ring.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent arg0) {
                        eelvaadeRing.setFill(Color.WHITE);
                        if (kord.getVärv().get() == m1_värv) {
                            eelvaadeRing.setFill(m1_värv);
                        } else {
                            eelvaadeRing.setFill(m2_värv);
                        }
                    }
                });

                // kui hiir ringilt lahkub on see taas nähtamatu
                ring.setOnMouseExited(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent arg0) {
                        eelvaadeRing.setFill(Color.TRANSPARENT);
                    }
                });

                // kui ringi peale vajutatakse, siis ring kukkub alla
                ring.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent arg0) {
                        // vaatame mis on selle ringi kordinaadid
                        int v = GridPane.getColumnIndex(stack);
                        int r = GridPane.getRowIndex(stack);

                        // vaatame kas tabelis on juba sellised kordinaadid olemas, et takistada mitmekordset vajutust
                        if (kontroll.getTabel()[r][v].equals("_")) {

                            // enne me liigutasime ringid üles, nüüd asetame nad tagasi algolekusse
                            tr.setToY(0);
                            tr.play();

                            // vaatame kelle kord on
                            if (kord.getVärv().get() == m1_värv) {
                                ring.fillProperty().bind(kord.getVärv()); // ringi värviks saab mängija värv
                                ülemine.set(m2_värv);                      // ülemiste ringide värv peab ka vahetuma
                                kord = Mängija.vahetaKord(kord);          // vahetame mängija korra
                                kontroll.insertTabel(r, v, "x");          // lisame tabelisse vastavasse kohta märgi
                                kontrolli();

                                // sama mis üleminegi, ainult teise mängija kohta
                            } else {
                                ring.fillProperty().bind(kord.getVärv());
                                ülemine.set(m1_värv);
                                kord = Mängija.vahetaKord(kord);
                                kontroll.insertTabel(r, v, "o");
                                kontrolli();

                            }
                        }
                    }
                });

                // sama mis tavalise ringi oma, kuid nüüd vaatab kas vajutatakse alumist, ruudustikus paiknevat ringi
                eelvaadeRing.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    public void handle(MouseEvent arg0) {
                        int v = GridPane.getColumnIndex(stack);
                        int r = GridPane.getRowIndex(stack);

                        if (kontroll.getTabel()[r][v].equals("_")) {
                            tr.setToY(0);
                            tr.play();
                            if (kord.getVärv().get() == m1_värv) {
                                ring.fillProperty().bind(Mängija.mängija_1.getVärv());
                                kord = Mängija.vahetaKord(kord);          // vahetame mängija korra
                                ülemine.set(m2_värv);
                                kontroll.insertTabel(r, v, "x");
                                kontrolli();
                            } else {
                                ring.fillProperty().bind(kord.getVärv());
                                ülemine.set(m1_värv);
                                kord = Mängija.vahetaKord(kord);
                                kontroll.insertTabel(r, v, "o");
                                kontrolli();

                            }
                        }
                    }
                });

                stack.getChildren().addAll(ring, eelvaadeRing);

                // lisame ringid ruudustikku veergu v ritta r
                grid.add(stack, v, r);
            }
        }
    }

}