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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ClienteMontos {

    @FXML
    private TextField txtMontoCedula;

    @FXML
    private TextField txtMontoCargar;

    @FXML
    private Label lblMontoRespuesta;

    @FXML
    public void cargarMonto(ActionEvent event) {
        String cedula = txtMontoCedula.getText();
        String monto = txtMontoCargar.getText();

        if (cedula.isEmpty() || monto.isEmpty()) {
            lblMontoRespuesta.setText("Ingrese cédula y el monto a cargar.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "UPDATE clientes SET monto_tarjeta = monto_tarjeta + ? WHERE cedula = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setDouble(1, Double.parseDouble(monto));
            pstmt.setString(2, cedula);

            int filasActualizadas = pstmt.executeUpdate();
            if (filasActualizadas > 0) {
                lblMontoRespuesta.setText("Monto recargado exitosamente.");
            } else {
                lblMontoRespuesta.setText("Error: Cliente no encontrado.");
            }
        } catch (NumberFormatException e) {
            lblMontoRespuesta.setText("Error: Ingrese un monto numérico válido.");
        } catch (Exception e) {
            lblMontoRespuesta.setText("Error en la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void pagar(ActionEvent event) {
        String cedula = txtMontoCedula.getText();

        if (cedula.isEmpty()) {
            lblMontoRespuesta.setText("Ingrese la cédula para pagar el pasaje.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            // 1. Consultar el saldo actual y si tiene tarifa preferencial
            String sqlSelect = "SELECT preferencia, monto_tarjeta FROM clientes WHERE cedula = ?";
            PreparedStatement pstmtSelect = conn.prepareStatement(sqlSelect);
            pstmtSelect.setString(1, cedula);
            ResultSet rs = pstmtSelect.executeQuery();

            if (rs.next()) {
                String preferencia = rs.getString("preferencia");
                double saldoActual = rs.getDouble("monto_tarjeta");

                // 2. Determinar el costo del pasaje basado en la preferencia
                double tarifa = "SI".equalsIgnoreCase(preferencia) ? 0.17 : 0.35;

                // 3. Verificar si el usuario tiene saldo suficiente
                if (saldoActual >= tarifa) {
                    // 4. Descontar el valor del pasaje
                    String sqlUpdate = "UPDATE clientes SET monto_tarjeta = monto_tarjeta - ? WHERE cedula = ?";
                    PreparedStatement pstmtUpdate = conn.prepareStatement(sqlUpdate);
                    pstmtUpdate.setDouble(1, tarifa);
                    pstmtUpdate.setString(2, cedula);

                    int actualizadas = pstmtUpdate.executeUpdate();
                    if (actualizadas > 0) {
                        double nuevoSaldo = saldoActual - tarifa;
                        lblMontoRespuesta.setText(String.format("Pago exitoso: cobro de $%.2f. Saldo restante: $%.2f", tarifa, nuevoSaldo));
                    }
                } else {
                    lblMontoRespuesta.setText(String.format("Saldo insuficiente. Pasaje: $%.2f, Saldo actual: $%.2f", tarifa, saldoActual));
                }
            } else {
                lblMontoRespuesta.setText("Error: Cliente no encontrado.");
            }
        } catch (Exception e) {
            lblMontoRespuesta.setText("Error al conectar con la base de datos.");
            e.printStackTrace();
        }
    }

    @FXML
    public void consultarDeuda(ActionEvent event) {
        String cedula = txtMontoCedula.getText();

        if (cedula.isEmpty()) {
            lblMontoRespuesta.setText("Ingrese la cédula para consultar el saldo.");
            return;
        }

        try (Connection conn = ConexionDB.conectar()) {
            String sql = "SELECT monto_tarjeta FROM clientes WHERE cedula = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cedula);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double saldo = rs.getDouble("monto_tarjeta");
                lblMontoRespuesta.setText(String.format("El saldo disponible en su tarjeta es: $%.2f", saldo));
            } else {
                lblMontoRespuesta.setText("Error: Cliente no encontrado.");
            }
        } catch (Exception e) {
            lblMontoRespuesta.setText("Error al conectar con la base de datos.");
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