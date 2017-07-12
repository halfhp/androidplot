/*
 * Copyright 2015 AndroidPlot.com
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

package com.androidplot.ui;

import com.androidplot.Plot;
import com.androidplot.Series;

import java.util.ArrayList;
import java.util.List;

/**
 * A stack of series to be rendered.  The stack order is immutable but individual elements may be
 * manipulated via the public methods of {@link RenderStack.StackElement}.
 */
public class RenderStack<SeriesType extends Series, FormatterType extends Formatter> {

    private final Plot plot;

    private final ArrayList<StackElement<SeriesType, FormatterType>> elements;

    public ArrayList<StackElement<SeriesType, FormatterType>> getElements() {
        return elements;
    }

    /**
     * An element on the render stack.
     */
    public class StackElement<SeriesType extends Series, FormatterType extends Formatter> {
        private SeriesBundle<SeriesType, FormatterType> seriesBundle;
        private boolean isEnabled = true;

        public StackElement(SeriesBundle<SeriesType, FormatterType> seriesBundle) {
            set(seriesBundle);
        }


        public SeriesBundle<SeriesType, FormatterType> get() {
            return seriesBundle;
        }

        public void set(SeriesBundle<SeriesType, FormatterType> seriesBundle) {
            this.seriesBundle = seriesBundle;
        }

        public boolean isEnabled() {
            return isEnabled;
        }

        /**
         * Enable or disable a stack element for rendering.  Has no effect on StackElements that
         * have already been rendered.
         * @param isEnabled
         */
        public void setEnabled(boolean isEnabled) {
            this.isEnabled = isEnabled;
        }
    }

    public RenderStack(Plot plot) {
        this.plot = plot;
        elements = new ArrayList<>(plot.getRegistry().size());
    }

    /**
     * Syncs the stack structure with plot's current state.  Should be called before
     * rendering series data to an XYGraphWidget.
     */
    public void sync() {
        getElements().clear();
        List<SeriesBundle<SeriesType, FormatterType>> pairList
                = plot.getRegistry().getSeriesAndFormatterList();
        for(SeriesBundle<SeriesType, FormatterType> thisPair: pairList) {
            getElements().add(new StackElement<>(thisPair));
        }
    }

    /**
     * Invokes {@link RenderStack.StackElement#setEnabled(boolean)} with a value
     * of false on all stack elements associated with the specified renderer.
     * @param rendererClass
     */
    public void disable(Class<? extends SeriesRenderer> rendererClass) {
        for(RenderStack.StackElement element : getElements()) {
            if(element.get().getFormatter().getRendererClass() == rendererClass) {
                element.setEnabled(false);
            }
        }
    }
}
