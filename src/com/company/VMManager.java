package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class VMManager {
    List<Page> mainMemory = new ArrayList<>();
    final String VMPATH = "vm.txt";
    File vm = new File(VMPATH);
    int mainMemorySize;
    public VMManager (int size)
    {
        mainMemorySize = size;
    }

    public void Store(String id, int value)
    {
        if(mainMemory.size() < mainMemorySize) //has empty spot
        {
            mainMemory.add(new Page(id,value,MyClock.INSTANCE.getTime()));
        }else
        {
            try {
                Scanner s = new Scanner(vm);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
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
}
