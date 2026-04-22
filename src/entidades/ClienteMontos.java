package entidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClienteMontos {

    @FXML
    private TextField txtMontoCedula;

    @FXML
    private TextField txtMontoCargar;

    @FXML
    private Label lblMontoRespuesta;

    private final String IP_SERVIDOR = "172.31.116.72";
    private final int PUERTO = 5000;

    public String enviarUDP(String IP, int puerto, String mensaje) throws Exception {
        DatagramSocket socket = new DatagramSocket();
        InetAddress direccion = InetAddress.getByName(IP);

        byte[] buferS = mensaje.getBytes();
        DatagramPacket salida = new DatagramPacket(buferS, buferS.length, direccion, puerto);
        socket.send(salida);

        byte[] bufferE = new byte[1024];
        DatagramPacket entrada = new DatagramPacket(bufferE, bufferE.length);

        socket.setSoTimeout(3000);

        try {
            socket.receive(entrada);
            return new String(entrada.getData(), 0, entrada.getLength()).trim();
        } finally {
            socket.close();
        }
    }

    @FXML
    public void cargarMonto(ActionEvent event) {
        String cedula = txtMontoCedula.getText();
        String monto = txtMontoCargar.getText();
        
        if (cedula.isEmpty() || monto.isEmpty()) {
            lblMontoRespuesta.setText("Ingrese cédula y el monto a cargar.");
            return;
        }

        try {
            String trama = "CARGAR " + cedula + ";" + monto;
            String resultado = enviarUDP(IP_SERVIDOR, PUERTO, trama);
            lblMontoRespuesta.setText(resultado);
        } catch (Exception e) {
            lblMontoRespuesta.setText("Error al conectar con servidor");
            e.printStackTrace();
        }
    }

    @FXML
    public void pagar(ActionEvent event) {
        String cedula = txtMontoCedula.getText();
        
        if (cedula.isEmpty()) {
            lblMontoRespuesta.setText("Ingrese la cédula para pagar.");
            return;
        }

        try {
            String trama = "PAGAR " + cedula;
            String resultado = enviarUDP(IP_SERVIDOR, PUERTO, trama);
            lblMontoRespuesta.setText(resultado);
        } catch (Exception e) {
            lblMontoRespuesta.setText("Error al conectar con servidor");
            e.printStackTrace();
        }
    }

    @FXML
    public void consultarDeuda(ActionEvent event) {
        String cedula = txtMontoCedula.getText();
        
        if (cedula.isEmpty()) {
            lblMontoRespuesta.setText("Ingrese la cédula para consultar.");
            return;
        }

        try {
            String trama = "CONSULTAR " + cedula;
            String resultado = enviarUDP(IP_SERVIDOR, PUERTO, trama);
            lblMontoRespuesta.setText(resultado);
        } catch (Exception e) {
            lblMontoRespuesta.setText("Error al conectar con servidor");
            e.printStackTrace();
        }
    }

    @FXML
    public void volverABuscar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/buscar.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
