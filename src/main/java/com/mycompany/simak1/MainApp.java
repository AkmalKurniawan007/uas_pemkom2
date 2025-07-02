package com.mycompany.simak1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        
        // PERBAIKAN 1: Path ke file bahasa disesuaikan dengan struktur folder
        // Path ini mencari file `messages` di dalam paket `com.mycompany.simak1.i18n`
        ResourceBundle bundle = ResourceBundle.getBundle("com/mycompany/simak1/i18n/messages", new Locale("id"));

        // PERBAIKAN 2: Path ke file FXML disesuaikan dengan struktur folder
        // Path ini mencari `MainView.fxml` di dalam paket `com.mycompany.simak1.view`
        URL fxmlLocation = getClass().getResource("/com/mycompany/simak1/view/MainView.fxml");

        if (fxmlLocation == null) {
            System.err.println("FATAL ERROR: File FXML tidak ditemukan. Pastikan lokasinya sudah benar.");
            return;
        }
        
        // Muat FXML dan berikan bundle bahasa kepadanya
        FXMLLoader fxmlLoader = new FXMLLoader(fxmlLocation, bundle);
        
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        
        // Atur judul jendela menggunakan kunci dari file bahasa
        stage.setTitle(bundle.getString("app.title"));
        stage.setScene(scene);
        stage.show();
    }
}