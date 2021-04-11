package com.company;

public class Main {

    public static void main(String[] args) {
	// write your code here
        VMManager m = new VMManager(2);
        m.Store("v1", 3, "process 1");
        m.Store("v2", 4, "process 2");
        m.Store("v1", 5, "process 3");
        m.Store("v3", 8, "process 3");
        m.Store("v4", 58, "process 3");
        m.Release("v4", "process 6");
        m.Release("v4", "process 6");
        m.Release("v1", "procee 9");
    }
}
