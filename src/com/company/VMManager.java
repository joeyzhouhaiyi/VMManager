package com.company;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// need to run on its own thread
public class VMManager extends Thread {
    List<Page> mainMemory = new ArrayList<>();
    final String VMPATH = "vm.txt";
    int mainMemorySize;
    boolean quit = false;

    
    public VMManager ()    {

    }
    public void setSize(int size) {mainMemorySize = size;}

    public void Store(String id, int value, String callerName)
    {
        int timeNow = MyClock.INSTANCE.getTime();

        //verify if the id already existed, if so just update the value
        for (int i = 0; i < mainMemory.size(); i++) {
            if(mainMemory.get(i).id.equals(id)) //if exists, overwrite value
            {
                mainMemory.set(i, new Page(id,value,timeNow));
                print(1,id,value,timeNow,callerName);
                System.out.println("UPDATE");
                return;
            }
        }

        // if id is a new entry, check if memory is full, then decide where to save
        if(mainMemory.size() < mainMemorySize) //has empty spot
        {
            //if doesnt exist already just add to main memory
            mainMemory.add(new Page(id,value,timeNow));
            print(1,id,value,timeNow,callerName);
        }else //if main memo full store to vm
        {
            if(writeToVM(id,value,timeNow) == 1)
            {
                print(1,id,value,timeNow,callerName);
            }else{
                System.out.println("Store command failed");
            }
        }
        System.out.println("NEW");
    }

    public void Release(String id, String callerName)
    {
        int timeNow = MyClock.INSTANCE.getTime();
        //1. check if id exists in main memory
        for (int i = 0; i < mainMemory.size(); i++) {
            if(mainMemory.get(i).id.equals(id))
            {
                Page p = mainMemory.get(i);
                print(2, p.id, p.value,timeNow,callerName);
                mainMemory.remove(i);
                return;
            }
        }

        //2. check if id exists in virtual memory
        List<String> vm = readAllFromVM();
        for (int i = 0; i < vm.size(); i++) {
            String[] record = vm.get(i).split(",");
            if(record[0].equals(id)) //if same id exists in the vm replace the string
            {
                print(2,record[0],Integer.parseInt(record[1]),timeNow,callerName);
                vm.remove(i);
                try {
                    Files.write(Path.of(VMPATH),vm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }

        // if not found
        System.out.println("No record found with id = " + id);

    }

    public int Lookup (String id, String callerName)
    {
        int timeNow = MyClock.INSTANCE.getTime();
        //1. check if id exists in main memory
        for (int i = 0; i < mainMemory.size(); i++) {
            if(mainMemory.get(i).id.equals(id))
            {
                Page p = mainMemory.get(i);
                //print(3, p.id, p.value,timeNow,callerName);
                return p.value;
            }
        }

        //2. check if id exists in virtual memory
        List<String> vm = readAllFromVM();
        for (int i = 0; i < vm.size(); i++) {
            String[] record = vm.get(i).split(",");
            if(record[0].equals(id)) //if same id exists in the vm replace the string
            {
                vm.remove(i);
                if(mainMemory.size() < mainMemorySize) //main memory has empty spot, save it and remove from vm
                {
                    mainMemory.add(new Page(record[0],Integer.parseInt(record[1]), timeNow));
                    printSwap(record[0],"NONE", timeNow,"VMManager");
                }
                else //otherwise swap
                {
                    mainMemory.sort(Comparator.comparingInt(m -> m.value));
                    Page p_oldest = mainMemory.get(0);
                    mainMemory.remove(0);
                    String page = p_oldest.id+","+p_oldest.value+","+timeNow;
                    vm.add(page);
                    printSwap(record[0],p_oldest.id,timeNow,"VMManager");
                }
                try {
                    Files.write(Path.of(VMPATH),vm);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Integer.parseInt(record[1]);
            }
        }

        System.out.println("Record not found with id =" + id);
        return -1;
    }

    //write pair to vm
    public int writeToVM(String id, int value, int time)
    {
        List<String> vm = readAllFromVM();
        String page = id+","+value+","+time;

        boolean dup = false;
        for (int i = 0; i < vm.size(); i++) {
            if(vm.get(i).split(",")[0].equals(id)) //if same id exists in the vm replace the string
            {
                vm.set(i,page);
                dup = true;
            }
        }
        if(!dup)
        {
            vm.add(page);
        }
        try {
            Files.write(Path.of(VMPATH),vm);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 1; //1 for success otherwise exception will be caught
    }

    //read all lines from vm
    public List<String> readAllFromVM()
    {
        List<String> lines = new ArrayList<>();
        try {
            lines = Files.readAllLines(Paths.get(VMPATH));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    //mode 1 - store, mode 2 - release, mode 3 - lookup
    public void print(int mode, String id, int value, int time, String callerName)
    {
        switch (mode)
        {
            case 1:
            {
                String s = "Clock:"+ time + ", " + callerName +", Store: " + id + ", value = " + value;
                System.out.println(s);
                OutputLogger.getInstance().writeLine(s);
                break;
            }
            case 2:
            {
                String s = "Clock:"+ time + ", " + callerName + ", Release: " + id;
                System.out.println(s);
                OutputLogger.getInstance().writeLine(s);
                break;
            }
            case 3:
            {
                String s = "Clock:"+ time + ", " + callerName + ", Lookup: " + id + ", value = " + value;
                System.out.println(s);
                OutputLogger.getInstance().writeLine(s);
                break;
            }
        }

    }

    public void printSwap(String id, String swappedId, int time, String callerName)
    {
        String s = "Clock:"+ time + ", " + callerName +", Swap: " + id + " with " + swappedId;
        System.out.println(s);
        OutputLogger.getInstance().writeLine(s);
    }

    // always listen to shared memory to see if there's any new commands
    void listen()
    {
        if(Commands.commandQ.size()!= 0)
        {
            Command c = Commands.commandQ.get(0);

            if(c.getType() == Command.CommandType.Store)
            {
                String[] id_value = c.getParams().split(" ");
                Store(id_value[0],Integer.parseInt(id_value[1]),c.getCaller().getProcessName());
            }else if(c.getType() == Command.CommandType.Release)
            {
                Release(c.getParams(),c.getCaller().getProcessName());
            }else if(c.getType() == Command.CommandType.Lookup)
            {
                int value = Lookup(c.getParams(),c.getCaller().getProcessName());
                print(3, c.getParams(), value,MyClock.INSTANCE.getTime(),c.getCaller().getProcessName());
            }
            // command completed, remove from Q
            Commands.commandQ.remove(0);
        }
    }


    public void quit() {quit = true;}
    @Override
    public void run() {
        while (!quit)
        {
            try {
                sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            listen();   // keep listening for new command to be excuted
        }
    }
}
