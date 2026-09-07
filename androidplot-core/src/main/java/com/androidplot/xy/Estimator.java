// SPDX-License-Identifier: Apache-2.0
package com.androidplot.xy;

import androidx.annotation.NonNull;

/**
 * Base for all estimation management schemes.
 */
public abstract class Estimator {

   public abstract void run(@NonNull XYPlot plot, @NonNull XYSeriesBundle sf);

}
