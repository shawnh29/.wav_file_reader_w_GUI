package com.example.cmpt365_project1;

import java.util.*;

public class HuffmanNode implements Comparable<HuffmanNode> {
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

    // so that the nodes can be compared by the PriorityQueue
    @Override
    public int compareTo(HuffmanNode node) {
        return this.frequency - node.frequency;
    }

    public static float runHuffman(List<Map.Entry<Short, Integer>> freqList, int numSamples) {
        // this hashmap will be used later when we need the frequencies
        HashMap<Short, Integer> map = new HashMap<>();

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();

        // inserting all the leaf nodes into priority queue
        for (Map.Entry<Short, Integer> entry : freqList) {
            HuffmanNode newNode = new HuffmanNode(entry.getValue(), entry.getKey());
            pq.add(newNode);
            map.put(entry.getKey(), entry.getValue());
        }

        // we loop until we only have the root node left
        while (pq.size() > 1) {
            HuffmanNode leftNode = pq.poll();
            HuffmanNode rightNode = pq.poll();
            // create a parent node with the frequency = left + right
            HuffmanNode parentNode = new HuffmanNode(leftNode.frequency + rightNode.frequency, (short) -1);

            parentNode.left = leftNode;
            parentNode.right = rightNode;

            // add the parent node back into the priority queue
            pq.add(parentNode);
        }
        // now pq just has the root node
        HuffmanNode root = pq.poll();
        HashMap<Short, String> codewords = new HashMap<>();
        generateCodes(root, "", codewords);

//        System.out.println(codewords);

        // the average codeword length is: (frequency/total number of samples) * codeword length
        float avg = 0;
        for (Map.Entry<Short, String> entry : codewords.entrySet()) {
            avg += ((float) map.get(entry.getKey()) / (numSamples * 2)) * entry.getValue().length();
        }
        System.out.println("Average codeword length is: " + avg);
        return avg;
    }
    public static void generateCodes(HuffmanNode node, String code, HashMap<Short, String> codewordList) {
        // base case
        if (node == null) {
            return;
        }
        // if the current node is a leaf node, add the key and codeword into the hashmap
        if (node.left == null && node.right == null) {
            codewordList.put(node.num, code);
        }
        // recurse on the node's left child
        generateCodes(node.left, code + "0", codewordList);
        // recurse on the node's right child
        generateCodes(node.right, code + "1", codewordList);
    }
}
