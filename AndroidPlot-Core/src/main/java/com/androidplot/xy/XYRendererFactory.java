/*
 * Copyright 2012 AndroidPlot.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
