package com.company;

import java.util.Random;

public class UserProcess extends Thread{
    static int processNumber = 1;
    String name;
    int startTime;
    int totalDuration;
    public boolean isFinished = false;

    public UserProcess(int startTime, int totalDuration)
    {
        this.startTime = startTime;
        this.totalDuration = totalDuration;
        this.name = "Process " + processNumber++;
    }
    public String getProcessName(){return name;}

    // while the process is running, it will constantly pick a command from the commands list
    @Override
    public void run() {
        int actualStartTime = MyClock.INSTANCE.getTime() / 1000;
        while((MyClock.INSTANCE.getTime() / 1000 - actualStartTime) < totalDuration) // while running time is smaller than total duration
        {

            try {
                //pick next command
                Commands.PickNext(this);
                Thread.sleep(new Random().nextInt(1000) + 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        isFinished = true;
    }
}
