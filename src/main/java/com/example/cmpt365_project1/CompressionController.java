package com.example.cmpt365_project1;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
public class CompressionController {
    @FXML
    private Label entropyLabel;
    @FXML
    private Label avgCodeLenLabel;
    @FXML
    private Label fileNameLabel;
    private double entropy = 0.0;
    public void openFile() throws IOException {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(".wav files", "*.wav"));
        File file = fc.showOpenDialog(null);
        entropy = 0.0;
        entropyLabel.setText("Entropy: ");
        avgCodeLenLabel.setText("Average Code Length: ");

        if (file != null) {
            fileNameLabel.setText("File: " + file.getName());
            fileNameLabel.setVisible(true);
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

            HashMap<Short, Integer> map = new HashMap<>();

            // we are combining both left and right channels so actual number of samples = 2 * numSamples
            short[] samples = new short[numSamples*2];
            for (int i=0;i<numSamples*2; i++) {
                samples[i] = Short.reverseBytes(dataInputStream.readShort());
                // putting the amplitude values into a hashmap, if a key value already exists, then just increment the frequency value
                if (!map.containsKey(samples[i])) {
                    map.put(samples[i], 1);
                } else {
                    map.replace(samples[i], map.get(samples[i]), map.get(samples[i])+1);
                }
            }

            List<Map.Entry<Short, Integer>> sortedMap = new ArrayList<>(map.entrySet());
            sortedMap.sort(Map.Entry.comparingByValue());

            // calculating entropy
            for (int i=0; i<sortedMap.size(); i++) {
                double p = 1.0 * sortedMap.get(i).getValue() / (numSamples*2);
                if (p > 0) {
                    double val = (p * (Math.log(p) / Math.log(2)));
                    entropy = entropy - val;
                }
            }
            DecimalFormat df = new DecimalFormat("#.######");
            df.setRoundingMode(RoundingMode.CEILING);

            entropy = Double.parseDouble(df.format(entropy));
            entropyLabel.setText(entropyLabel.getText() + entropy);
            System.out.println("Entropy: " + entropy);

            // running the Huffman Coding code
            float avgCodeLen = HuffmanNode.runHuffman(sortedMap, numSamples);
            avgCodeLen = Float.parseFloat(df.format(avgCodeLen));
            avgCodeLenLabel.setText(avgCodeLenLabel.getText() + avgCodeLen);
        }
        entropyLabel.setVisible(true);
        avgCodeLenLabel.setVisible(true);
    }
}
