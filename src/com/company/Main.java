package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        VMManager m = new VMManager(2);
        m.Store("v1", 3);
        m.Store("v2", 4);
        m.Store("v1", 5);
    }
}
