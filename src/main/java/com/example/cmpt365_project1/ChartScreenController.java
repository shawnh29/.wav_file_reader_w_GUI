package com.example.cmpt365_project1;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import javax.sound.sampled.*;
import java.io.*;
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

    public void run() throws IOException {

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

        byte[] dataW = new byte[4];
        dataInputStream.read(dataW);
        String dataW_header = new String(dataW);
        if (!dataW_header.equals("data")) {
            System.out.println("INVALID 'data' SUBCHUNK");
            return;
        }

        int data_len = Integer.reverseBytes(dataInputStream.readInt());
        int numSamples = fileSize / (numChannels * bitsPerSample / 8);

        short[] audioData = new short[data_len / 2];
        for (int i=0;i<audioData.length; i++) {
            audioData[i] = Short.reverseBytes(dataInputStream.readShort());
        }
//        for (int i=0; i<50; i++) {
//            System.out.println("Audio data " + i + ": " + audioData[i]);
//        }

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

        dataInputStream.close();
        fileInputStream.close();
///////////////////////////////////////////////////////////////////////////////

//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
//        AudioFormat audioFormat = audioInputStream.getFormat();

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        XYChart.Series series = new XYChart.Series();
//
//        for (int i=0; i<arr.length; i++) {
//            series.getData().add(new XYChart.Data(String.valueOf(i), arr[i]));
//        }
//        System.out.println("Hello");
//        lineChart.getData().addAll(series);
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
