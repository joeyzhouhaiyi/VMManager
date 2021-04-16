package com.company;

public class Command {
    public enum CommandType{
        Store,
        Release,
        Lookup
    }
    UserProcess caller;
    CommandType type;
    String params;


    public Command(UserProcess caller, CommandType type, String params)
    {
        this.caller = caller;
        this.type = type;
        this.params = params;
    }

    public UserProcess getCaller(){
        return caller;
    }

    public CommandType getType(){
        return type;
    }

    public String getParams(){
        return params;
    }


}
