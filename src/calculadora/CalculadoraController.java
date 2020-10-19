package calculadora;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class CalculadoraController implements Initializable {

    String value1;
    String value2;
    String operator;

    @FXML
    private Label resultado;

    @FXML
    private BorderPane borderPane;

    InetAddress ip;
    // establish the connection with server port 5056 5057
    Socket s1;
    Socket s2;
    // obtaining input and out streams
    DataInputStream dis1;
    DataOutputStream dos1;
    DataInputStream dis2;
    DataOutputStream dos2;
    ProtocoloResponse protocoloResponse = new ProtocoloResponse();
    boolean result = false;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            ip = InetAddress.getByName("localhost");
            s1 = new Socket(ip, 5056);
            s2 = new Socket(ip, 5057);

            dis1 = new DataInputStream(s1.getInputStream());
            dos1 = new DataOutputStream(s1.getOutputStream());

            dis2 = new DataInputStream(s2.getInputStream());
            dos2 = new DataOutputStream(s2.getOutputStream());

        } catch (Exception e) {
            System.out.println("error connection " + e.getMessage());
            showAlertInformation(
                    "Atenção",
                    "Não foi possivel efetuar comunicação com os servidores");
        }


        setTimeout(() -> {
            Stage stage = (Stage) borderPane.getScene().getWindow();
            stage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, this::closeWindowEvent);
        }, 1000);


    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }



    public void addNumber(MouseEvent event) {
        String id = ((Control)event.getSource()).getId();
        switch (id) {
            case  "um" :
                addNumberLabel("1");
                break;

            case  "dois" :
                addNumberLabel("2");
                break;

            case  "tres" :
                addNumberLabel("3");
                break;

            case  "quatro" :
                addNumberLabel("4");
                break;
                
            case  "cinco" :
                addNumberLabel("5");
                break;
                
            case  "seis" :
                addNumberLabel("6");
                break;
                
            case  "sete" :
                addNumberLabel("7");
                break;
                
            case  "oito" :
                addNumberLabel("8");
                break;
                
            case  "nove" :
                addNumberLabel("9");
                break;

            case  "zero" :
                addNumberLabel("0");
                break;
        }
        
    }

    private void addNumberLabel(String number) {
        String value = resultado.getText();
        if (value.equals("0")) {
            resultado.setText(number);
        } else {
            if(!Character.isDigit(value.charAt(0))) {
                resultado.setText(number);
            } else {
                if (result) {
                    result = false;
                    resultado.setText(number);
                } else {
                    resultado.setText(value + number);
                }

            }
        }
    }


    public void addOperator(MouseEvent event) {
        String id = ((Control)event.getSource()).getId();
        switch (id) {
            case "adicao" :
                operatorHandle("+");
                break;

            case "subtracao" :
                operatorHandle("-");
                break;

            case "multiplicacao" :
                operatorHandle("*");
                break;

            case "divisao" :
                operatorHandle("/");
                break;

            case "porcentagem" :
                operatorHandle("%");
                break;

            case "potenciacao" :
                operatorHandle("^");
                break;

            case "raiz" :
                operatorHandle("√");
                break;
        }
    }

    private void operatorHandle(String operatorValue) {
        String valueDisplay = resultado.getText();
        if (valueDisplay.length() > 0 ) {
            if (operator == null) {
                if (value2 == null) {
                    value1 = valueDisplay;
                    operator = operatorValue;
                    resultado.setText("0");
                    //previewValue.setValue(value1 + " " + operator + " ");
                }
            } else {
                operator = operatorValue;
            }
        }
    }


    public void deleteItemDisplay() {
        String value = resultado.getText();
        if (value.length() > 0 && !value.equals("0")) {
            String newValue = value.substring (0, value.length() - 1);
            if (newValue.length() == 0) {
                resultado.setText("0");
            } else {
                resultado.setText(newValue);
            }
        }
    }

    public void clean() {
        value1 = null;
        value2 = null;
        operator = null;
        resultado.setText("0");
    }

    public boolean isArithmetic() {
        return this.operator.equals("^") || this.operator.equals("%") ? false : true;
    }


    public void equal() {
        if (operator != null) {
            value2 = resultado.getText();
            ProtocoloRequest protocolo = new ProtocoloRequest(value1, value2, operator);
            String res;

            try {
                if (isArithmetic()) {
                    dos1.writeUTF(protocolo.getStringRequest());
                    res = dis1.readUTF();
                } else {
                    dos2.writeUTF(protocolo.getStringRequest());
                    res = dis2.readUTF();
                }

                this.clean();

                protocoloResponse.converterStringValores(res);
                resultado.setText(protocoloResponse.getMensagem());
                result = true;

            } catch (Exception e) {
                System.out.println(e.getMessage());
                this.clean();
            }

        }
    }



    private void closeWindowEvent(WindowEvent event) {
        System.out.println("Saindo ...");
        try {
            if(dis1 != null && dis2 != null) {
                dos1.writeUTF("Exit");
                dos2.writeUTF("Exit");
                dis1.close();
                dis2.close();
                dos1.close();
                dos2.close();
                s1.close();
                s2.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showAlertInformation(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("");
        alert.setContentText(msg);

        alert.showAndWait();
    }
}
