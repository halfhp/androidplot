package com.example;


import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
    public static final int SINE1 = 0;
    public static final int SINE2 = 1;
    private static final int SAMPLE_SIZE = 30;
    private int phase = 0;
    private int sinAmp = 20;
    private MyObservable notifier;

    {
        notifier = new MyObservable();
    }

    //@Override
    public void run() {
        try {
            boolean isRising = true;
            while (true) {

                Thread.sleep(50); // decrease or remove to speed up the refresh rate.
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
        return 30;
    }

    public Number getX(int series, int index) {
        if (index >= SAMPLE_SIZE) {
            throw new IllegalArgumentException();
        }
        return index;
    }

    public Number getY(int series, int index) {
         if (index >= SAMPLE_SIZE) {
             throw new IllegalArgumentException();
         }
        double amp = sinAmp * Math.sin(index + phase + 4);
        switch (series) {
            case SINE1:
               return amp;
            case SINE2:
                return -amp;
            default:
                throw new IllegalArgumentException();
        }
    }

    public void addObserver(Observer observer) {
        notifier.addObserver(observer);
    }

    public void removeObserver(Observer observer) {
        notifier.deleteObserver(observer);
    }

}
