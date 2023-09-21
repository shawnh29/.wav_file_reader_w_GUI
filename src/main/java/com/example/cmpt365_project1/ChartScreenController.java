package com.example.cmpt365_project1;

import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;

public class ChartScreenController {
    @FXML
    private LineChart<?, ?> LineChart;
    private File file;

    public static ChartScreenController object;

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

        XYChart.Series series = new XYChart.Series();
        series.getData().add(new XYChart.Data("1", 22));

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
