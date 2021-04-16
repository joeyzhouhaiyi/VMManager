package com.company;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /***
     *
     * @param args
     * args[0] - processes.txt
     * args[1] - memconfig.txt
     * args[2] - commands.txt
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        BufferedReader reader = null;

        //process processes.txt
        reader = new BufferedReader(new FileReader(args[0]));
        List<String> processList = new ArrayList<>();
        String line;
        int index = 0;
        while( (line = reader.readLine()) != null)
            processList.add(index++, line);


        //process memconfig.txt
        reader = new BufferedReader(new FileReader(args[1]));
        String memSize = reader.readLine();

        //process command.txt
        reader = new BufferedReader(new FileReader(args[2]));
        List<String> commadList = new ArrayList<>();
        line = null;
        index = 0;
        while( (line = reader.readLine()) != null)
            commadList.add(index++, line);



        // init threads in order
        //clock
        Thread clock = new Thread(MyClock.INSTANCE);
        //mmu
        VMManager MMU = new VMManager();
        MMU.setSize(Integer.parseInt(memSize.trim()));
        //scheduler
        Scheduler scheduler = new Scheduler();
        scheduler.setCommandList(commadList);
        int status = scheduler.setProcessQ(processList);
        if(status == -1)
            return;



        // start threads in order
        clock.start();
        MMU.start();
        scheduler.start();

        // stop all threads
        scheduler.quit();
        MMU.quit();
        MyClock.INSTANCE.quit();

        // wait for all to finish
        try {
            clock.join();
            MMU.join();
            scheduler.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
