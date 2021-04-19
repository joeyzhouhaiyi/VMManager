package com.company;

import java.util.*;

/***
 * this class schedules processes in a FIFO order
 */
public class Scheduler extends Thread {
    int coreNumber;
    static int runningProcessCount = 0;
    List<UserProcess> processQ = new ArrayList<>(); // full list of processes parsed from processes.txt
    List<UserProcess> runningProcesses = new ArrayList<>(); // processes that are running at the moment
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

    synchronized public List<UserProcess> getRunningProcesses() {
        return runningProcesses;
    }


    void StartProcess(){runningProcessCount++;}
    void StopProcess()
    {
        if(runningProcessCount > 0)
            runningProcessCount --;
        else
            runningProcessCount = 0;
    }


    // when process's isFinished flag is set to true,
    // this function prints the finished text and calls join() function of the thread
    // then remove that thread from the running processes list
    void checkProcessFinished()
    {
        for(UserProcess up : getRunningProcesses())
        {
            if(up.isFinished)
            {
                try {
                    up.join(); //joining running thread
                    String s = "Clock: " + MyClock.INSTANCE.getTime() +", "+ up.getProcessName() + ": Finished.";
                    System.out.println(s);
                    OutputLogger.getInstance().writeLine(s);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                StopProcess();
                runningProcesses.remove(up);
                break;
            }

        }
    }


    //start process in FIFO order
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
                String s = "Clock: " + MyClock.INSTANCE.getTime() +", "+ up.getProcessName() + ": Started.";
                System.out.println(s);
                OutputLogger.getInstance().writeLine(s);
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
