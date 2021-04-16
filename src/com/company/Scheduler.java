package com.company;

import java.util.*;

public class Scheduler extends Thread {
    int coreNumber;
    static int commandIndex = 0;
    static int runningProcessCount = 0;
    List<Command> commandList = new ArrayList<>();
    List<UserProcess> processQ = new ArrayList<>();
    List<UserProcess> runningProcesses = new ArrayList<>();
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

    public List<UserProcess> getProcessQ() {
        return processQ;
    }

    synchronized public List<UserProcess> getRunningProcesses() {
        return runningProcesses;
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


    void StartProcess(){runningProcessCount++;}
    void StopProcess()
    {
        if(runningProcessCount > 0)
            runningProcessCount --;
        else
            runningProcessCount = 0;
    }


    void checkProcessFinished()
    {
        for(UserProcess up : getRunningProcesses())
        {
            if(up.isFinished)
            {
                try {
                    up.join(); //joining running thread
                    System.out.println("Clock: " + MyClock.INSTANCE.getTime() +", "+ up.getProcessName() + ": Finished.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StopProcess();
                runningProcesses.remove(up);
                break;
            }

        }
    }


    //start process in start time order
    //keep track on each process's start and finish time
    //have process pick commands synchronously

    public void quit(){quit = true;}
    @Override
    public void run() {
        processQ.sort(Comparator.comparingInt( startTime -> startTime.startTime));
        while (processQ.size() != 0)
        {
            try {
                sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //if time has arrived and max number of process not reached
            if(MyClock.INSTANCE.getTime()/1000 >= processQ.get(0).startTime
                    && runningProcessCount < coreNumber)
            {
                UserProcess up = processQ.get(0);
                up.start();
                System.out.println("Clock: " + MyClock.INSTANCE.getTime() +", "+ up.getProcessName() + ": Started.");
                StartProcess(); // increment counter
                runningProcesses.add(up);
                processQ.remove(0);
            }
            checkProcessFinished();

        }
        while(!quit)
        {
            checkProcessFinished();
        }
    }
}
