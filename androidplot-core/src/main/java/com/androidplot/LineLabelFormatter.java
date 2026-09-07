// SPDX-License-Identifier: Apache-2.0

package com.androidplot;

import android.graphics.Paint;

public interface LineLabelFormatter {


    /**
     *
     * @param value The value being rendered by this formatter.
     * @return Paint instance that should be used to render the specified value.
     */
    Paint getPaint(Number value);
}
