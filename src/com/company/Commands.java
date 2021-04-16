package com.company;

import java.util.ArrayList;
import java.util.List;

public class Commands {
    static List<Command> commands = new ArrayList<>();  // full list of commands represented by <Command> objects
    static int commandIndex = 0;    // the index pointing to the next command in the list
    static int commandCount = 0;    // the total count of the commands in the list

    // shared memory, shared between MMU and processes
    public static List<Command> commandQ = new ArrayList<>();

    public void setCommands(List<String> commandList)
    {
        for (String c : commandList)
        {
            String[] content = c.split(" ");
            if(content[0].equals("Store"))
            {
                commands.add(new Command(null, Command.CommandType.Store, content[1]+" "+ content[2]));
            }else if(content[0].equals("Release"))
            {
                commands.add(new Command(null, Command.CommandType.Release, content[1]));
            }else if(content[0].equals("Lookup"))
            {
                commands.add(new Command(null, Command.CommandType.Lookup, content[1]));
            }else
            {
                System.out.println("found invalid commands: " + c);
            }
        }
        commandCount = commands.size();
    }

    // any process will call this method to pick the next command from the list
    synchronized public static void PickNext(UserProcess up)
    {
        Command c = commands.get(commandIndex);
        c.caller = up;
        commandQ.add(c);
        commandIndex++;
        if(commandIndex > commandCount-1){
            commandIndex = 0;
        }
    }
}
