package com.example;


import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.os.SystemClock;
import android.util.Log;

public class SampleDynamicXYDatasource implements Runnable {

    // encapsulates management of the observers watching this datasource for update events:
    class MyObservable extends Observable {
    @Override
    public void notifyObservers() {
        setChanged();
        super.notifyObservers();
    }
}

    private static final int MAX_AMP_SEED = 100;
    private static final int MIN_AMP_SEED = 10;
    private static final int AMP_STEP = 5;

    private int mPoints = 200;
    
    private double phase = 0;
    private double sinAmp = 20;
    private MyObservable notifier;
    private boolean exit;
    private boolean sin_on = true;

    {
        notifier = new MyObservable();
    }

    //@Override
    public void run() {
        phase = 0;
        sinAmp = 20;
        exit = false;
        
        try {
            boolean isRising = true; 
            while (!exit) {
            	//Thread.sleep(50); // decrease or remove to speed up the refresh rate.                
                Thread.sleep(1); // decrease or remove to speed up the refresh rate.
                phase++;
                if (sinAmp >= MAX_AMP_SEED) {
                    isRising = false;
                } else if (sinAmp <= MIN_AMP_SEED) {
                    isRising = true;
                }

                if (isRising) {
                    sinAmp += AMP_STEP;
                } else {
                    sinAmp -= AMP_STEP;
                }
                notifier.notifyObservers();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public int getItemCount(int series) {
        return mPoints;
    }

    public Number getX(int series, int index) {
        if (index >= mPoints) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    public Number getY(int series, int index) {
         if (index >= mPoints) {
             throw new IllegalArgumentException();
         }
         if (sin_on){
             double amp = sinAmp * Math.sin((series + 1) * ((index*2*Math.PI) / mPoints) + phase);
             return amp;
         } else {
             double amp = sinAmp * (series+1) * 0.2;
             return amp;
         }
    }
    
    public void setPoints(int points){
        mPoints = points;
    }

    public void setSinOn(boolean on){
        sin_on = on;
    }
    
    public void setExit(){
        exit = true;
    }

    public void addObserver(Observer observer) {
        notifier.addObserver(observer);
    }

    public void removeObserver(Observer observer) {
        notifier.deleteObserver(observer);
    }

}
