package com.example.Game;

public class Stopwatch {
    private int seconds ;
    private int minutes ;

    public Stopwatch() {
        this.seconds = 0;
        this.minutes = 0;
    }


    public int getSeconds() {
        return seconds;
    }

    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public void reset(){
        seconds = 0;
        minutes = 0;
    }


    public String getTime(){
        return String.format("%02d:%02d", minutes, seconds);
    }
}
