package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VMManager {
    List<Page> mainMemory = new ArrayList<>();
    final String VMPATH = "vm.txt";
    int mainMemorySize;

    public VMManager (int size)
    {
        mainMemorySize = size;
    }

    public void Store(String id, int value)
    {
        int timeNow = MyClock.INSTANCE.getTime();

        //verify if the id already existed, if so just update the value
        for (int i = 0; i < mainMemory.size(); i++) {
            if(mainMemory.get(i).id.equals(id)) //if exists, overwrite value
            {
                mainMemory.set(i, new Page(id,value,timeNow));
                print(1,id,value,timeNow);
                return;
            }
        }

        // if id is a new entry, check if memory is full, then decide where to save
        if(mainMemory.size() < mainMemorySize) //has empty spot
        {
            //if doesnt exist already just add to main memory
            mainMemory.add(new Page(id,value,timeNow));
            print(1,id,value,timeNow);
        }else //if main memo full store to vm
        {
            if(writeToVM(id,value,timeNow) == 1)
            {
                print(1,id,value,timeNow);
            }else{
                System.out.println("Store command failed");
            }
        }
    }

    public void Release(String id)
    {

    }

    public int Lookup (String id)
    {
        return -1;
    }

    //write pair to vm
    public int writeToVM(String id, int value, int time)
    {
        List<String> vm = readAllFromVM();
        String page = id+","+value+","+time+"\n";

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
            Files.write(Path.of(VMPATH),vm, StandardOpenOption.CREATE);
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
    public void print(int mode, String id, int value, int time)
    {
        switch (mode)
        {
            case 1:
            {
                System.out.println("Store: " + id + ", value = " + value);
                break;
            }
            case 2:
            {
                System.out.println("Release: " + id);
                break;
            }
            case 3:
            {
                System.out.println("Lookup: " + id + ", value = " + value);
                break;
            }
        }

    }

    public void printSwap(String id, String swappedId)
    {
        System.out.println("Swap: " + id + " with " + swappedId);
    }
}
