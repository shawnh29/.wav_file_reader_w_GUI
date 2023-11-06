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
import java.text.NumberFormat;
import java.util.*;
public class CompressionController {
    @FXML
    private Label entropyLabel;
    @FXML
    private Label avgCodeLenLabel;
    private double entropy;
    public void openFile() throws IOException {
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

            // do i need to combine both channels?? --> i only need 1 hashmap??
            HashMap<Short, Integer> map = new HashMap<>();
            HashMap<Short, Integer> mapSorted = new HashMap<>();
            HashMap<Short, Integer> map2 = new HashMap<>();

            short[] leftAudio = new short[numSamples];
            short[] rightAudio = new short[numSamples];
            for (int i=0;i<numSamples; i++) {
                leftAudio[i] = Short.reverseBytes(dataInputStream.readShort());
                rightAudio[i] = Short.reverseBytes(dataInputStream.readShort());

                if (!map.containsKey(leftAudio[i])) {
                    map.put(leftAudio[i], 1);
                } else {
                    map.replace(leftAudio[i], map.get(leftAudio[i]), map.get(leftAudio[i])+1);
                }
                if (!map2.containsKey(rightAudio[i])) {
                    map2.put(rightAudio[i], 1);
                } else {
                    map2.replace(rightAudio[i], map2.get(rightAudio[i]), map2.get(rightAudio[i])+1);
                }
            }
            List<Map.Entry<Short, Integer>> sortedMap = new ArrayList<>(map.entrySet());
            sortedMap.sort(Map.Entry.comparingByValue());
            sortedMap.remove(sortedMap.size()-1);

            List<Float> probabilityList = new ArrayList<>();
            for (Map.Entry<Short, Integer> entry : sortedMap) {
                probabilityList.add( (float) entry.getValue() / sortedMap.size());
            }
            System.out.println(sortedMap);
            System.out.println(probabilityList);

            for (int i=0; i<sortedMap.size(); i++) {
                double val = -1 * ( probabilityList.get(i) * (Math.log10(probabilityList.get(i)) / Math.log10(2)) );
                entropy += val;
            }
            DecimalFormat df = new DecimalFormat("#.####");
            df.setRoundingMode(RoundingMode.CEILING);
            entropy = Double.parseDouble(df.format(entropy));
            entropyLabel.setText(entropyLabel.getText() + entropy);
            System.out.println("Entropy: " + entropy);

            System.out.println("Length of hashmap: " + map.size());
            System.out.println("Length of hashmap2: " + map2.size());

        }
        entropyLabel.setVisible(true);
        avgCodeLenLabel.setVisible(true);
    }
}
