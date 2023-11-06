package com.example.cmpt365_project1;

public class Node implements Comparable<Node>{
    private int frequency;
    private Node left;
    private Node right;

    public int getFrequency() {
        return frequency;
    }

    public Node(Node left, Node right) {
        this.frequency = left.getFrequency() + right.getFrequency();
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(Node node) {
        return Integer.compare(frequency, node.getFrequency());
    }
}
