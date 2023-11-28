package arduino_fx;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import com.fazecast.jSerialComm.SerialPort;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import jssc.SerialPortTimeoutException;
//import com.fazecast.jSerialComm.*;


/**
 *
 * @author mario de nichilo 02-06-2018
 */
public class FXMLDocumentController implements Initializable {

    //
    // Qui vengono dichiarati i controlli e le variabili del programma
    //
    @FXML
    private ComboBox<String> cmb;
    @FXML
    private Button btn, btn1, btnpin8, btnpin7, btnpin6, btnCiclo;
    @FXML
    private Pane p8, p7, p6, prg;
    @FXML
    private TextArea txt;
    @FXML
    private Label lbd, lbstato;
    @FXML
    private ComboBox<Integer> spHz;
    @FXML
    private ComboBox<Integer> spN;

    // Creo un'istanza della classe SerialPort
    //
    SerialPort serialPort;

    private String nomePorta;

    // Funzione richiamata ogni volta che si invia un comando
    // ad Arduino
    //
    private void inviaComando(String c) throws SerialPortException {
        if (serialPort.isOpened()) {
            serialPort.writeString(c);
            
        }
    }

    // Funzione che permette di scegliere quante volte e con quale intervallo
    // cambiare lo stato logico presente sul pin 6 di Arduino
    //
    @FXML
    void EseguiCiclo(ActionEvent event) throws SerialPortException {

        int durataSemiperiodo = spHz.getValue();
        int ciclo = spN.getValue();

        String msg = "@" + durataSemiperiodo + "c" + ciclo;
        inviaComando(msg);
        System.out.println(msg);
    }

    // Funzioni eseguite dai pulsanti collegati alle 
    // uscite 8-7-6 di Arduino
    //
    @FXML
    void onoffpin8(ActionEvent event) throws SerialPortException {

        inviaComando("led8");
    }

    @FXML
    void onoffpin7(ActionEvent event) throws SerialPortException {

        inviaComando("led7");
    }

    @FXML
    void onoffpin6(ActionEvent event) throws SerialPortException {
        inviaComando("led6");

    }

    //
    // Chiude la porta seriale e disabilità i controlli sulla finestra
    //
    @FXML
    void disconnetti(ActionEvent event) {

        try {

            if (serialPort.isOpened() == true) {
                serialPort.closePort();
                btn.disableProperty().set(false);
                btn1.disableProperty().set(true);
                btnpin6.disableProperty().set(true);
                btnpin7.disableProperty().set(true);
                btnpin8.disableProperty().set(true);
                spHz.disableProperty().set(true);
                spN.disableProperty().set(true);
                btnCiclo.disableProperty().set(true);
                prg.setStyle("-fx-background-color:gray");
                lbstato.setText("Arduino Disconnesso!");

            }

        } catch (SerialPortException ex) {
            System.out.println(ex);
        }

    }

    //
    // Procedura di connessione con la scheda Arduino
    //
    @FXML
    void connetti(ActionEvent event) throws InterruptedException, SerialPortTimeoutException {

        nomePorta = cmb.getValue();
        serialPort = new SerialPort(nomePorta);

        try {

            if (serialPort.isOpened() == false) {

                //Apre la porta seriale
                //
                serialPort.openPort();
                //
                //Abilita i controlli sulla finestra
                //
                btn.disableProperty().set(true);
                btn1.disableProperty().set(false);
                btnpin6.disableProperty().set(false);
                btnpin7.disableProperty().set(false);
                btnpin8.disableProperty().set(false);
                btnCiclo.disableProperty().set(false);
                spHz.disableProperty().set(false);
                spN.disableProperty().set(false);

                // Imposta alcune proprietà della porta seriale,come
                // ad esempio la velocità,in questo caso impostata a 9600 Baud
                //
                serialPort.setParams(SerialPort.BAUDRATE_9600,
                        SerialPort.DATABITS_8,
                        SerialPort.STOPBITS_1,
                        SerialPort.PARITY_NONE);

                serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN
                        | SerialPort.FLOWCONTROL_RTSCTS_OUT);

                //
                // Aggiunge un gestore d'eventi quando riceve qualcosa sulla
                // porta seriale
                //
                serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);

                //
                // Identifica la scheda Arduino attraverso una stringa di riconoscimento
                //
                try {
                    String idx = serialPort.readString();

                    if (serialPort.isOpened() && idx.contains("ARDU328PU")) {
                        prg.setStyle("-fx-background-color: green");
                        lbstato.setText("Arduino Connesso..." + "[" + serialPort.getPortName() + "]");
                        System.out.println("Dispositivo ricononosciuto---> OK");
                    }

                } catch (Exception e) {
                    System.out.println("Dispositivo non riconosciuto!");

                }

            }

        } catch (SerialPortException ex) {
            System.out.println("Errore nella scrittura sulla porta: " + ex);
        }

    }

    //
    // Alcune operazioni fatte all'apertura del programma
    //
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        //
        // Attraverso l'oggetto SerialPortList si ottiene un elenco
        // delle porte seriali sul pc memorizzate nel'array port
        //
        String[] port = SerialPortList.getPortNames();
        String porta = Arrays.toString(port);
        for (String Porta : port) {

            if (porta.contains("[")) {
                porta = porta.replace("[", "");
                porta = porta.replace("]", "");
            }
            cmb.getItems().add(Porta); // Si riempie il cmbox con le porte disponibili
        }

        //
        // Tutti i pulsanti e i comboBox,tranne il pulsante per la connessione, 
        // vengono disabilitati
        //
        btn.disableProperty().set(false);
        btn1.disableProperty().set(true);
        btnpin6.disableProperty().set(true);
        btnpin7.disableProperty().set(true);
        btnpin8.disableProperty().set(true);
        btnCiclo.disableProperty().set(true);
        spHz.disableProperty().set(true);
        spN.disableProperty().set(true);

        for (int i = 50; i < 2100; i = i + 50) {

            spHz.getItems().add(i);
        }

        for (int c = 1; c < 101; c++) {

            spN.getItems().add(c);
        }

        spHz.getSelectionModel().select(0);
        spN.getSelectionModel().select(1);

    }

    // Classe usata per ricevere dati attraverso
    // la porta seriale
    //
    class PortReader implements SerialPortEventListener {

        private String valore;

        public void setValore(String valore) {
            this.valore = valore;
        }

        public String getValore() {
            return valore;
        }

        //
        // Qui vengono letti e interpretati i comandi provenienti da Arduino
        //
        @Override
        public void serialEvent(SerialPortEvent event) {

            if (event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = serialPort.readString(event.getEventValue());
                    System.out.println(receivedData);
                    this.setValore(receivedData);
                    txt.setText(this.getValore() + "\n");

                    if (this.getValore().contains("led8 ON\n")) {
                        p8.setStyle("-fx-background-color:orange");
                    }
                    if (this.getValore().contains("led8 OFF\n")) {
                        p8.setStyle("-fx-background-color:gray");
                    }

                    if (this.getValore().contains("led7 ON\n")) {
                        p7.setStyle("-fx-background-color:orange");
                    }
                    if (this.getValore().contains("led7 OFF\n")) {
                        p7.setStyle("-fx-background-color:gray");
                    }

                    if (this.getValore().contains("led6 ON\n")) {
                        p6.setStyle("-fx-background-color:orange");
                    }
                    if (this.getValore().contains("led6 OFF\n")) {
                        p6.setStyle("-fx-background-color:gray");
                    }

                    Thread.sleep(300);

                } catch (SerialPortException ex) {
                    System.out.println("Errore nel ricevere dati da com-PORT: " + ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

}
