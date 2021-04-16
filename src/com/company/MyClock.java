package com.company;

public enum MyClock implements Runnable {
    INSTANCE(1000);
    private int time;
    private boolean quit = false;

    MyClock(int i){
        time = i;
    }

    public void quit(){
        quit= true;
    }

    public  int getTime(){
        return time;
    }

    public void printEvent(String ev){
        System.out.println("ev = " + ev);
    }

    @Override
    public void run() {
        while(!quit){
            try{
                Thread.sleep(100);
            }catch (Exception e){
                e.printStackTrace();
            }
            time += 100;
        }
    }
}
