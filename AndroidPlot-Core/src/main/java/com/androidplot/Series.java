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

package com.androidplot;

/**
 * Base interface for all Series implementations
 */
public interface Series<T> {

    /**
     *
     * @return The title of this Series.
     */
    public String getTitle();



    // used primarily for synchronization.  can also be used to hang a condition on updates.

    /**
     * Called whenever the plot initiates a read of a Series.  In most cases this means that
     * a complete read of the Series contents will proceed.
     *//*
    public void onReadBegin();

    *//**
     * Called when a Plot concludes reading of a Series.
     *//*
    public void onReadEnd();*/
}
