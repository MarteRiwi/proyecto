package marte.src.com.sigp;

import javafx.application.Application;
import javafx.stage.Stage;

public class MarteApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // El login decide a qué panel ir según el rol del usuario
        new LoginView(primaryStage).mostrar();
    }

    public static void main(String[] args) {
        // launch() inicializa el toolkit de JavaFX y llama a start()
        launch(args);
    }
}


