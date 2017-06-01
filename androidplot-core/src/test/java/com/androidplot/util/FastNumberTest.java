package com.androidplot.util;

import android.annotation.SuppressLint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"UnnecessaryBoxing", "ObjectEqualsNull", "EqualsBetweenInconvertibleTypes", "EqualsWithItself", "NumberEquality"})
@SuppressLint("UseValueOf")
public class FastNumberTest {

    private static final Number[] NUMBERS = new Number[]{
            new Integer(0),
            new Integer(1),
            new Integer(-1),
            new Integer(Integer.MAX_VALUE),
            new Integer(Integer.MIN_VALUE),

            new Long(0),
            new Long(1),
            new Long(-1),
            new Long(Long.MAX_VALUE),
            new Long(Long.MIN_VALUE),

            new Double(0),
            new Double(1),
            new Double(-1),
            new Double(87654321.12345d),
            new Double(0.0002f),
            new Double(Double.MAX_VALUE),
            new Double(Double.MIN_VALUE),

            new Float(0),
            new Float(1.0f),
            new Float(-1.0f),
            new Float(8765.4321f),
            new Float(1098.576f),
            new Float(Float.MIN_VALUE),
            new Float(Float.MAX_VALUE),
    };

    private static final Number[] NUMBERS_CLONE = new Number[]{
            new Integer(0),
            new Integer(1),
            new Integer(-1),
            new Integer(Integer.MAX_VALUE),
            new Integer(Integer.MIN_VALUE),

            new Long(0),
            new Long(1),
            new Long(-1),
            new Long(Long.MAX_VALUE),
            new Long(Long.MIN_VALUE),

            new Double(0),
            new Double(1),
            new Double(-1),
            new Double(87654321.12345d),
            new Double(0.0002f),
            new Double(Double.MAX_VALUE),
            new Double(Double.MIN_VALUE),

            new Float(0),
            new Float(1.0f),
            new Float(-1.0f),
            new Float(8765.4321f),
            new Float(1098.576f),
            new Float(Float.MIN_VALUE),
            new Float(Float.MAX_VALUE),
    };

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void orNull_returnsNull_ifNullNumber() {
        //noinspection ConstantConditions
        assertNull(FastNumber.orNull(null));
    }

    @Test
    public void equals_returnsTrue_ifFromSameNumberInstance() {
        for (Number number : NUMBERS) {
            assertTrue("Equality test failed on " + number, FastNumber.orNull(number).equals(FastNumber.orNull(number)));
        }
    }

    @Test
    public void equals_returnsTrue_ifFromSameNumber() {
        assertEquals("misconfigured test values", NUMBERS.length, NUMBERS_CLONE.length);
        for (int i = 0; i < NUMBERS.length; i++) {
            assertEquals("misconfigured test values", NUMBERS[i], NUMBERS_CLONE[i]);
            assertTrue(FastNumber.orNull(NUMBERS[i]).equals(FastNumber.orNull(NUMBERS_CLONE[i])));
        }
    }

    @Test
    public void equals_returnsFalse_ifNumberIsDifferent() {
        for (int i = 0; i < NUMBERS.length; i++) {
            for (int j = 0; j < NUMBERS.length; j++) {
                if (j == i) {
                    continue;
                }
                assertNotEquals("duplicate test values at index " + i + " and " + j, NUMBERS[i], NUMBERS[j]);
                assertFalse(FastNumber.orNull(NUMBERS[i]).equals(FastNumber.orNull(NUMBERS[j])));
            }
        }
    }

    @Test
    public void hashCode_isEqual_ifInstanceIsEqual() {
        for (Number number : NUMBERS) {
            for (Number number2 : NUMBERS) {
                FastNumber fastNumber1 = FastNumber.orNull(number);
                FastNumber fastNumber2 = FastNumber.orNull(number2);
                if (fastNumber1.equals(fastNumber2)) {
                    assertEquals(fastNumber1.hashCode(), fastNumber2.hashCode());
                }
            }
        }
    }

}