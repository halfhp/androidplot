// SPDX-License-Identifier: Apache-2.0

package com.androidplot.util;

import org.junit.Test;
import java.util.regex.Pattern;
import static junit.framework.Assert.assertTrue;

public class PixelUtilsTest {

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void testDimensionPattern() {
        Pattern DIMENSION_PATTERN = Pattern.compile(PixelUtils.DIMENSION_REGEX);
        assertTrue("Dimension failed dimension pattern match", DIMENSION_PATTERN.matcher("20dp").matches());
        assertTrue("Negative dimension failed dimension pattern match", DIMENSION_PATTERN.matcher("-20dp").matches());
    }
}
