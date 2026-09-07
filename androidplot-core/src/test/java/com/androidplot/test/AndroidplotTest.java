// SPDX-License-Identifier: Apache-2.0

package com.androidplot.test;

import android.content.Context;

import com.androidplot.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.junit.*;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.*;

/**
 * Base class for all Androidplot tests that make use of Android platform classes.
 */
@RunWith(MyTestRunner.class)
@Config(manifest=Config.NONE)
public abstract class AndroidplotTest {

    /**
     * Enable mockito without requiring usage of {@link org.mockito.runners.MockitoJUnitRunner}.
     * See: http://site.mockito.org/mockito/docs/current/org/mockito/junit/MockitoRule.html
     */
    @Rule
    public MockitoRule rule = MockitoJUnit.rule();

    {
        PixelUtils.init(getContext());
    }

    /**
     * Convience method - to access the application context.
     * @return
     */
    protected Context getContext() {
        return RuntimeEnvironment.application;
    }
}
