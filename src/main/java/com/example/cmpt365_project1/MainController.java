package com.example.cmpt365_project1;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    @FXML
    private Label welcomeText;
    @FXML
    private NumberAxis xAxis = new NumberAxis();
    @FXML
    private NumberAxis yAxis = new NumberAxis();
    @FXML
    private LineChart<Number, Number> leftLineChart = new LineChart<>(xAxis, yAxis);
    @FXML
    private LineChart<Number, Number> rightLineChart = new LineChart<>(xAxis, yAxis);

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

            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);

            byte[] riff = new byte[4];
            dataInputStream.read(riff);
            String riffHeader = new String(riff);
            if (!riffHeader.equals("RIFF")) {
                System.out.println("NOT A VALID WAV FILE (RIFF)");
                return;
            }

            int fileSize = Integer.reverseBytes(dataInputStream.readInt());

            byte[] wave = new byte[4];
            dataInputStream.read(wave);
            String waveHeader = new String(wave);
            if (!waveHeader.equals("WAVE")) {
                System.out.println("NOT A VALID WAV FILE (WAVE)");
                return;
            }

            byte[] fmt = new byte[4];
            dataInputStream.read(fmt);
            String fmtHeader = new String(fmt);
            if (!fmtHeader.equals("fmt ")) {
                System.out.println("INVALID 'fmt' SUBCHUNK");
                return;
            }

            int formatSize = Integer.reverseBytes(dataInputStream.readInt());
            short audioFormat = Short.reverseBytes(dataInputStream.readShort());
            short numChannels = Short.reverseBytes(dataInputStream.readShort());
            int sampleRate = Integer.reverseBytes(dataInputStream.readInt());
            int byteRate = Integer.reverseBytes(dataInputStream.readInt());
            short blockAlign = Short.reverseBytes(dataInputStream.readShort());
            short bitsPerSample = Short.reverseBytes(dataInputStream.readShort());

            int data_len = 0;
            boolean notFound = true;

            while (notFound) {
                byte[] chunk = new byte[4];
                dataInputStream.read(chunk);
                String chunkID = new String(chunk);
                if (!chunkID.equals("data")) {
                    int chunkLen = Integer.reverseBytes(dataInputStream.readInt());
                    byte[] junk = new byte[chunkLen];
                    dataInputStream.read(junk, 0, chunkLen);
                } else {
                    notFound = false;
                    data_len = Integer.reverseBytes(dataInputStream.readInt());
                }
            }

            int numSamples = data_len / (numChannels * bitsPerSample / 8);

            short[] leftAudio = new short[numSamples];
            short[] rightAudio = new short[numSamples];
            for (int i=0;i<numSamples; i++) {
                leftAudio[i] = Short.reverseBytes(dataInputStream.readShort());
                rightAudio[i] = Short.reverseBytes(dataInputStream.readShort());
            }

            System.out.println("File Size: " + fileSize + " Bytes");
            System.out.println("Format Size: " + formatSize);
            System.out.println("Audio Format: " + audioFormat);
            System.out.println("Channels: " + numChannels);
            System.out.println("Sample Rate: " + sampleRate + "Hz");
            System.out.println("Bits per Sample: " + bitsPerSample);
            System.out.println("Byte Rate: " + byteRate);
            System.out.println("Block Align: " + blockAlign);
            System.out.println("Total number of samples: " + numSamples);
            System.out.println("Length of data: " + data_len);

            XYChart.Series<Number, Number> leftSeries = new XYChart.Series<>();
            XYChart.Series<Number, Number> rightSeries = new XYChart.Series<>();

            rightLineChart.setLegendVisible(false);
            rightLineChart.setCreateSymbols(false);
            leftLineChart.setLegendVisible(false);
            leftLineChart.setCreateSymbols(false);

            List<XYChart.Data<Number, Number>> leftData = new ArrayList<>();
            List<XYChart.Data<Number, Number>> rightData = new ArrayList<>();

            int i;
            String index;
            for (i=0; i<numSamples; i++) {
                index = String.valueOf(i);
                leftData.add(i, new XYChart.Data(index, leftAudio[i]));
                rightData.add(i, new XYChart.Data(index, rightAudio[i]));
            }
            leftSeries.getData().addAll(leftData);
            rightSeries.getData().addAll(rightData);

            leftLineChart.getData().clear();
            rightLineChart.getData().clear();
            leftLineChart.getData().add(leftSeries);
            rightLineChart.getData().add(rightSeries);
//            leftSeries.getNode().setStyle("-fx-stroke-width: 1px;");
//            rightSeries.getNode().setStyle("-fx-stroke-width: 1px;");

            dataInputStream.close();
            fileInputStream.close();

        }
    }
}