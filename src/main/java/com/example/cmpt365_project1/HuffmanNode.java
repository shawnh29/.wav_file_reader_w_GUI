package com.example.cmpt365_project1;

import java.util.*;

public class HuffmanNode implements Comparable<HuffmanNode>{
    private int frequency;
    private HuffmanNode left;
    private HuffmanNode right;
    private short num;

    public HuffmanNode(int frequency, short num) {
        this.frequency = frequency;
        this.num = num;
        this.left = null;
        this.right = null;
    }
    @Override
    public int compareTo(HuffmanNode node) {
        return this.frequency - node.frequency;
    }
    public static void generateCodes (HuffmanNode node, String code, HashMap<Short, String> codewords) {
        if (node == null) {
            return;
        }
        generateCodes(node.left, code + "0", codewords);
        // if it IS leaf node, add its code and value to the hashmap
        if (node.left == null && node.right == null) {
            codewords.put(node.num, code);
        }
        generateCodes(node.right, code + "1", codewords);
    }
    public static float runHuffman(List<Map.Entry<Short, Integer>> frequencyList) {
        PriorityQueue<HuffmanNode> queue = new PriorityQueue<>();

        for (Map.Entry<Short, Integer> entry : frequencyList) {
            queue.add(new HuffmanNode(entry.getValue(), entry.getKey()));
        }

        while (queue.size() > 1) {
            HuffmanNode leftNode = queue.poll();
            HuffmanNode rightNode = queue.poll();
            HuffmanNode parentNode = new HuffmanNode(leftNode.frequency + Objects.requireNonNull(rightNode).frequency, (short) -1);

            parentNode.left = leftNode;
            parentNode.right = rightNode;

            queue.add(parentNode);
        }
        HuffmanNode rootNode = queue.poll();

        HashMap<Short, String> codewords = new HashMap<>();
        generateCodes(rootNode, "", codewords);

        System.out.println(codewords);

        int sum = 0;
        for (Map.Entry<Short, String> entry : codewords.entrySet()) {
            sum += entry.getValue().length();               // maybe has to be a weighted average??
        }
        System.out.println("Sum: " + sum + " Len: " + codewords.size());
        System.out.println("Average codeword length: " + (float) sum / codewords.size());
        return (float) sum / codewords.size();
    }
}
