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

package com.androidplot.test;

import android.content.Context;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

/**
 * Base class for all Androidplot tests that make use of Android platform classes.
 */
@RunWith(RobolectricTestRunner.class)
public abstract class AndroidplotTest {

    /**
     * Convience method - to access the application context.
     * @return
     */
    protected Context getContext() {
        return RuntimeEnvironment.application;
    }
}
