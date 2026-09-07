// SPDX-License-Identifier: Apache-2.0

package com.androidplot.xy;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class XYConstraintsTest {

    @Test
    public void contains_rectRegion_checksMinXAgainstDomain() {
        // domain fixed to [0, 50], range unconstrained:
        XYConstraints constraints = new XYConstraints(0, 50, null, null);

        // region extends to x = -50, outside of the domain.  Previously minY was passed
        // as the x coordinate so this region was reported as contained:
        assertFalse(constraints.contains(new RectRegion(-50, 50, 0, 10)));

        // region fully within the domain:
        assertTrue(constraints.contains(new RectRegion(10, 40, 0, 10)));

        // region extends beyond the upper domain boundary:
        assertFalse(constraints.contains(new RectRegion(10, 60, 0, 10)));
    }

    @Test
    public void contains_rectRegion_checksRangeEdges() {
        XYConstraints constraints = new XYConstraints(null, null, 0, 50);

        assertTrue(constraints.contains(new RectRegion(-100, 100, 10, 40)));
        assertFalse(constraints.contains(new RectRegion(-100, 100, -10, 40)));
        assertFalse(constraints.contains(new RectRegion(-100, 100, 10, 60)));
    }
}
