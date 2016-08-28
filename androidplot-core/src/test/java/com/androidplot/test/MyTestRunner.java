package com.androidplot.test;

import org.junit.runners.model.*;
import org.robolectric.*;

public class MyTestRunner extends RobolectricTestRunner {

    /**
     * Constructs a new instance of the test runner.
     *
     * @throws InitializationError if the test class is malformed
     */
    public MyTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }
}
