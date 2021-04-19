package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    /***
     * Programming assignment #3 By
     * Haiyi Zhou 27008457
     * Yushan Yang 40099151
     * Zhuoyan Li 40061140
     *
     * @param args
     * args[0] - processes.txt
     * args[1] - memconfig.txt
     * args[2] - commands.txt
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        BufferedReader reader = null;
        OutputLogger.getInstance();
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

        if( reader != null)
            // close reader
            reader.close();

        // init threads in order
        //clock
        Thread clock = new Thread(MyClock.INSTANCE);
        //mmu
        VMManager MMU = new VMManager();
        MMU.setSize(Integer.parseInt(memSize.trim()));
        //commands
        Commands commands = new Commands();
        commands.setCommands(commadList);
        //scheduler
        Scheduler scheduler = new Scheduler();
        int status = scheduler.setProcessQ(processList);
        if(status == -1)
            return;



        // start threads in order
        clock.start();
        MMU.start();
        scheduler.start();

        // hold main until there's is no more process to schedule nor to run
        while(scheduler.processQ.size()!=0 || scheduler.getRunningProcesses().size() != 0){
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        scheduler.quit();
        MMU.quit();
        MyClock.INSTANCE.quit();
        System.out.println("stop all threads");
        // close writer
        OutputLogger.getInstance().close();

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
