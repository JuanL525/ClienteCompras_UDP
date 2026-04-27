package entidades;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Cliente {

    @FXML
    private TextField txtCédula;

    @FXML
    private Label lblRespuesta;

    @FXML
    private TextField txtRegistroNombre;

    @FXML
    private TextField txtRegistroCedula;

    @FXML
    private TextField txtRegistroCorreo;

    @FXML
    private TextField txtRegistroTelefono;

    @FXML
    private CheckBox chkRegistroPreferencia;

    @FXML
    private Label lblRegistroRespuesta;


    @FXML
    public void buscar(ActionEvent event) {
        String cedula = txtCédula.getText();
        if (cedula == null || cedula.trim().isEmpty()) {
            lblRespuesta.setText("Ingrese una cédula");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT * FROM clientes WHERE cedula = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cedula);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String datos = "Nombre: " + rs.getString("nombre") +
                        "\nCorreo: " + rs.getString("correo") +
                        "\nMonto Tarjeta: $" + rs.getDouble("monto_tarjeta");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/resultado.fxml"));
                Parent root = loader.load();
                ClienteResultado controladorResultado = loader.getController();
                controladorResultado.setDatos(datos);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                lblRespuesta.setText("Error: Cliente no encontrado.");
            }
        } catch (Exception e) {
            lblRespuesta.setText("Error al conectar con la base de datos");
            e.printStackTrace();
        }
    }

    @FXML
    public void registrar(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/registrar.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            if (lblRespuesta != null) {
                lblRespuesta.setText("Error al cargar ventana de registro.");
            }
        }
    }

    @FXML
    public void crearUsuario(ActionEvent event) {
        String nombre = txtRegistroNombre.getText();
        String cedula = txtRegistroCedula.getText();
        String correo = txtRegistroCorreo.getText();
        String preferencia = chkRegistroPreferencia.isSelected() ? "SI" : "NO";
        String telefono = txtRegistroTelefono.getText();

        if (nombre == null || nombre.trim().isEmpty() ||
                cedula == null || cedula.trim().isEmpty() ||
                correo == null || correo.trim().isEmpty() ||
                telefono == null || telefono.trim().isEmpty()) {
            lblRegistroRespuesta.setText("Por favor llenar todos los campos.");
            return;
        }

        // Validacion de nombre solo letras
        if (!nombre.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")) {
            lblRegistroRespuesta.setText("El nombre solo debe contener letras.");
            return;
        }

        // Validacion de cedula solo numeros y maximo 10 digitos
        if (!cedula.matches("^[0-9]{1,10}$")) {
            lblRegistroRespuesta.setText("La cédula debe tener solo números (máximo 10).");
            return;
        }

        // Validacion de correo debe contener @ y dominio
        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            lblRegistroRespuesta.setText("Por favor ingrese un correo válido (ej: usuario@dominio.com).");
            return;
        }

        // Validacion de telefono solo numeros
        if (!telefono.matches("^[0-9]+$")) {
            lblRegistroRespuesta.setText("El teléfono debe tener solo números.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "INSERT INTO clientes (cedula, nombre, correo, telefono, preferencia) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cedula);
            pstmt.setString(2, nombre);
            pstmt.setString(3, correo);
            pstmt.setString(4, telefono);
            pstmt.setString(5, preferencia);

            int filasInsertadas = pstmt.executeUpdate();
            if (filasInsertadas > 0) {
                lblRegistroRespuesta.setText("Respuesta: Registro exitoso en Supabase.");
            }
        } catch (Exception e) {
            lblRegistroRespuesta.setText("Error: La cédula ya existe o falló la conexión.");
            e.printStackTrace();
        }
    }

    @FXML
    public void irAMontos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vista/montos.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            if (lblRespuesta != null) {
                lblRespuesta.setText("Error al cargar ventana de montos.");
            }
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