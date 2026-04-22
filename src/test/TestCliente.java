package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class TestCliente extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        URL fxmlLocation = getClass().getResource("/vista/buscar.fxml");

        if (fxmlLocation == null) {
            System.out.println("No se encontró el archivo FXML.");
            System.exit(1);
        }

        Parent root = FXMLLoader.load(fxmlLocation);
        Scene scene = new Scene(root);
        primaryStage.setTitle("CLIENTES - Cliente");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}