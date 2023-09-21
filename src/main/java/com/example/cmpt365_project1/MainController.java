package com.example.cmpt365_project1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MainController {
    @FXML
    private Label welcomeText;

    public static MainController object;

    public MainController() {

    }

    public static MainController getInstance() {
        if (object == null) {
            synchronized (MainController.class) {
                if (object == null) {
                    object = new MainController();
                }
            }
        }
        return object;
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }

    //function that opens a file explorer and prompts user to select a .wav file and processes it
    public void openFileClicked(ActionEvent e) throws IOException, UnsupportedAudioFileException {
        System.out.println("Choose a file..");
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(".wav files", "*.wav"));
        File file = fc.showOpenDialog(null);

        if (file != null) {
            System.out.println("File selected! --> " + file.getPath());

            ChartScreenController.getInstance().setFile(file);
            ChartScreenController.getInstance().run();

            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("ChartScreen.fxml")));
            Stage stage = (Stage)((Node)e.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setTitle("Waveform");
            stage.setScene(scene);
            stage.show();

        }

    }
}