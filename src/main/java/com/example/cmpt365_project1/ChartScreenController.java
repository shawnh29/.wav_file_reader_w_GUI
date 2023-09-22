package com.example.cmpt365_project1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ChartScreenController implements Initializable {
    @FXML
    private LineChart lineChart;
    private File file;

    public static ChartScreenController object;

    private int[] arr;

    public void setFile(File file) {
        this.file = file;
    }

    public ChartScreenController() {

    }

    public static ChartScreenController getInstance() {
        if (object == null) {
            synchronized (ChartScreenController.class) {
                if (object == null) {
                    object = new ChartScreenController();
                }
            }
        }
        return object;
    }

    public void run() throws UnsupportedAudioFileException, IOException {

        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
        AudioFormat audioFormat = audioInputStream.getFormat();

        float sampleRate = audioFormat.getSampleRate();
        System.out.println("Sample Rate: " + sampleRate);
        byte[] byteArray = audioInputStream.readAllBytes();

        arr = new int[byteArray.length]; //this is null for some reason, probs smth to do with synchronization and scene switching
        int value = 0;
        for (int i=0; i<byteArray.length; i++) {
            value = (value << 8) + (byteArray[i] & 0xFF);
            arr[i] = value;
            if (i == 0 || i == 1) {
                System.out.println("arr: " + arr[i]);
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        XYChart.Series series = new XYChart.Series();

        for (int i=0; i<arr.length; i++) {
            series.getData().add(new XYChart.Data(String.valueOf(i), arr[i]));
        }
        System.out.println("Hello");
        lineChart.getData().addAll(series);
    }
//    public void initialize() throws UnsupportedAudioFileException, IOException {
//
//        if (file != null) {
//            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
//            AudioFormat audioFormat = audioInputStream.getFormat();
//
//            float sampleRate = audioFormat.getSampleRate();
//            System.out.println("Sample Rate: " + sampleRate);
//            byte[] byteArray = audioInputStream.readAllBytes();
//
//            XYChart.Series series = new XYChart.Series();
//            series.getData().add(new XYChart.Data("1", 22));
//
//        }
//    }

}
