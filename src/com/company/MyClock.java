package com.company;

public enum MyClock implements Runnable {
    INSTANCE(1000);
    private int time;
    private boolean finishProg;

    MyClock(int i){
        time = i;
        finishProg = false;
    }

    public boolean isFinishProg(){
        return finishProg;
    }
    public void setFinishProg(boolean finishProg){
        this.finishProg = finishProg;
    }

    public  int getTime(){
        return time;
    }

    public void printEvent(String ev){
        System.out.println("ev = " + ev);
    }

    @Override
    public void run() {
        while(!finishProg){
            try{
                Thread.sleep(100);
            }catch (Exception e){
                e.printStackTrace();
            }
            time += 100;
        }
    }
}
