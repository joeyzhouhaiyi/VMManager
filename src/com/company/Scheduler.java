package com.company;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Scheduler extends Thread {
    int coreNumber;
    List<Command> commandList = new ArrayList<>();
    Queue<UserProcess> processQ = new LinkedList<>();
    boolean quit = false;

    public int setProcessQ(List<String> processes)
    {
        if(processes.size()<3)
        {
            System.out.println("bad processes list format");
            return -1;
        }
        coreNumber = Integer.parseInt(processes.get(0));
        int pSize = Integer.parseInt(processes.get(1));
        processes.remove(0);
        processes.remove(0);
        for(String p: processes)
        {
            String[] times = p.split(" ");
            processQ.add(new UserProcess(Integer.parseInt(times[0]),Integer.parseInt(times[1])));
        }
        //double checking if parsing was successful
        if(processQ.size() != pSize)
        {
            System.out.println("error parsing process list");
            return -1;
        }

        return 0;
    }

    public void setCommandList(List<String> commandList)
    {
        for (String c : commandList)
        {
            String[] content = c.split(" ");
            if(content[0].equals("Store"))
            {
                this.commandList.add(new Command(null, Command.CommandType.Store, content[1]+" "+ content[2]));
            }else if(content[0].equals("Release"))
            {
                this.commandList.add(new Command(null, Command.CommandType.Release, content[1]));
            }else if(content[0].equals("Lookup"))
            {
                this.commandList.add(new Command(null, Command.CommandType.Lookup, content[1]));
            }else
            {
                System.out.println("found invalid commands: " + c);
            }
        }
    }


    public void quit(){quit = true;}
    @Override
    public void run() {
        while (!quit)
        {

        }
    }
}
