package com.androidplot.xy;

/**
 * All XYRenderers and their formatters need to be registered with this factory to be useable.
 * TODO: create a static hash lookup for renderer->formatter lookups.
 */
public class XYRendererFactory {
    static XYSeriesRenderer getInstance(XYPlot owner, Class clazz) {
        if (clazz == LineAndPointRenderer.class) {
            return new LineAndPointRenderer(owner);
        } else if (clazz == BarRenderer.class) {
            return new BarRenderer(owner);
        } else if (clazz == StepRenderer.class) {
            return new StepRenderer(owner);
        } else if (clazz == BezierLineAndPointRenderer.class) {
            return new BezierLineAndPointRenderer(owner);
        } else {
            return null;
        }
    }

    static Class getRendererClass(XYSeriesFormatter formatter) {
        Class clazz = formatter.getClass();
        if(clazz == LineAndPointFormatter.class) {
            return LineAndPointRenderer.class;
        } else if(clazz == BarFormatter.class) {
            return BarRenderer.class;
        } else if(clazz == StepFormatter.class) {
            return StepRenderer.class;
        } else if(clazz == BezierLineAndPointFormatter.class) {
            return BezierLineAndPointRenderer.class;
        } else {
            return null;
        }
    }
}
