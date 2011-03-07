package com.androidplot;

public class PlotEvent {
    public enum Type {
        PLOT_REDRAWN
    }
    private final Plot source;
    private final Type type;
    public PlotEvent(Plot source, Type type) {
        this.source = source;
        this.type = type;
    }

    public Plot getSource() {
        return source;
    }
}
