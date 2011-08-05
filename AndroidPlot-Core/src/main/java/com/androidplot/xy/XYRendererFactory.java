/*
 * Copyright (c) 2011 AndroidPlot.com. All rights reserved.
 *
 * Redistribution and use of source without modification and derived binaries with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ANDROIDPLOT.COM ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL ANDROIDPLOT.COM OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of AndroidPlot.com.
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
