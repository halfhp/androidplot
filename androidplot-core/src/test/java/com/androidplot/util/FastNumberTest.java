package com.androidplot.util;

import android.annotation.SuppressLint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"UnnecessaryBoxing", "ObjectEqualsNull", "EqualsBetweenInconvertibleTypes", "EqualsWithItself", "NumberEquality"})
@SuppressLint("UseValueOf")
public class FastNumberTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullConstructor() {
        //noinspection ConstantConditions
        new FastNumber(null);
    }

    @Test
    public void testEquals() {
        assertEquals(new FastNumber(new Double(0)), new FastNumber(new Double(0)));
        assertNotEquals(new FastNumber(new Double(0)), new FastNumber(new Long(0)));
    }


    @Test
    public void testEqualsInteger() {
        assertEquals(new Integer(0), Integer.valueOf(0));
        assertEquals(new Integer(1), Integer.valueOf(1));
        assertEquals(new Integer(-1), Integer.valueOf(-1));

        Integer fixture = new Integer(25);
        assertEquals(fixture, fixture);
        assertFalse(fixture.equals(null));
        assertFalse(fixture.equals("Not a Integer"));
    }

    @Test
    public void testEqualsInteger2() {
        // Test for method boolean java.lang.Integer.equals(java.lang.Object)
        FastNumber i1 = new FastNumber(new Integer(1000));
        FastNumber i2 = new FastNumber(new Integer(1000));
        FastNumber i3 = new FastNumber(new Integer(-1000));
        assertTrue("Equality test failed", i1.equals(i2) && !(i1.equals(i3)));
    }

    @Test
    public void testEqualsLong() {
        assertEquals(new FastNumber(new Long(0)), new FastNumber(Long.valueOf(0)));
        assertEquals(new FastNumber(new Long(1)), new FastNumber(Long.valueOf(1)));
        assertEquals(new FastNumber(new Long(-1)), new FastNumber(Long.valueOf(-1)));

        FastNumber fixture = new FastNumber(new Long(25));
        assertEquals(fixture, fixture);
        assertFalse(fixture.equals(null));
        assertFalse(fixture.equals("Not a Long"));
    }

    @Test
    public void testEqualsDouble() {
        FastNumber d1 = new FastNumber(new Double(87654321.12345d));
        FastNumber d2 = new FastNumber(new Double(87654321.12345d));
        FastNumber d3 = new FastNumber(new Double(0.0002f));
        assertTrue("Assert 0: Equality test failed", d1.equals(d2) && !(d1.equals(d3)));

        assertTrue("Assert 2: NaN should not be == Nan", Double.NaN != Double.NaN);
        assertTrue("Assert 3: NaN should not be == Nan", new FastNumber(new Double(Double.NaN))
                .equals(new FastNumber(new Double(Double.NaN))));
        assertTrue("Assert 4: -0d should be == 0d", 0d == -0d);
        assertTrue("Assert 5: -0d should not be equals() 0d", !new FastNumber(new Double(0d))
                .equals(new FastNumber(new Double(-0d))));

        FastNumber dmax = new FastNumber(new Double(Double.MAX_VALUE));
        FastNumber dmax1 = new FastNumber(new Double(Double.MAX_VALUE));

        assertTrue("Equality test failed", dmax.equals(dmax1) && !(dmax.equals(new Object())));
    }

    @Test
    public void testEqualsFloat() {
        FastNumber f1 = new FastNumber(new Float(8765.4321f));
        FastNumber f2 = new FastNumber(new Float(8765.4321f));
        FastNumber f3 = new FastNumber(new Float(-1.0f));
        assertTrue("Assert 0: Equality test failed", f1.equals(f2) && !(f1.equals(f3)));

        assertTrue("Assert 1: NaN should not be == Nan", Float.NaN != Float.NaN);
        assertTrue("Assert 2: NaN should not be == Nan", new FastNumber(new Float(Float.NaN)).equals(new FastNumber(new Float(
                Float.NaN))));
        assertTrue("Assert 3: -0f should be == 0f", 0f == -0f);
        assertTrue("Assert 4: -0f should not be equals() 0f", !new FastNumber(new Float(0f)).equals(new FastNumber(new Float(
                -0f))));

        f1 = new FastNumber(new Float(1098.576f));
        f2 = new FastNumber(new Float(1098.576f));
        f3 = new FastNumber(new Float(1.0f));
        assertTrue("Equality test failed", f1.equals(f2) && !(f1.equals(f3)));

        assertTrue("NaN should not be == Nan", Float.NaN != Float.NaN);
        assertTrue("NaN should not be == Nan", new FastNumber(new Float(Float.NaN))
                .equals(new FastNumber(new Float(Float.NaN))));
        assertTrue("-0f should be == 0f", 0f == -0f);
        assertTrue("-0f should not be equals() 0f", !new FastNumber(new Float(0f)).equals(new FastNumber(new Float(-0f))));
    }

    @Test
    public void testHashCodeInteger() {
        assertEquals(1, new FastNumber(new Integer(1)).hashCode());
        assertEquals(2, new FastNumber(new Integer(2)).hashCode());
        assertEquals(0, new FastNumber(new Integer(0)).hashCode());
        assertEquals(-1, new FastNumber(new Integer(-1)).hashCode());
    }

    @Test
    public void testHashCodeInteger2() {
        // Test for method int java.lang.Integer.hashCode()

        FastNumber i1 = new FastNumber(new Integer(1000));
        FastNumber i2 = new FastNumber(new Integer(-1000));
        assertTrue("Returned incorrect hashcode", i1.hashCode() == 1000
                && (i2.hashCode() == -1000));
    }

    @SuppressWarnings("PointlessBitwiseExpression")
    @Test
    public void testHashCodeLong() {
        assertEquals((int) (1L ^ (1L >>> 32)), new FastNumber(new Long(1)).hashCode());
        assertEquals((int) (2L ^ (2L >>> 32)), new FastNumber(new Long(2)).hashCode());
        assertEquals((int) (0L ^ (0L >>> 32)), new FastNumber(new Long(0)).hashCode());
        assertEquals((int) (-1L ^ (-1L >>> 32)), new FastNumber(new Long(-1)).hashCode());
    }

    @Test
    public void testHashCodeDouble() {
        // Test for method int java.lang.Double.hashCode()
        for (int i = -1000; i < 1000; i++) {
            FastNumber d = new FastNumber(new Double(i));
            FastNumber dd = new FastNumber(new Double(i));
            assertTrue("Should not be identical ", d != dd);
            assertTrue("Should be equals 1 ", d.equals(dd));
            assertTrue("Should be equals 2 ", dd.equals(d));
            assertTrue("Should have identical values ", dd.doubleValue() == d.doubleValue());
            assertTrue("Invalid hash for equal but not identical doubles ", d.hashCode() == dd
                    .hashCode());
        }
        assertEquals("Magic assumption hasCode (0.0) = 0 failed", 0, new FastNumber(new Double(0.0)).hashCode());
    }

    @Test
    public void testHashCodeFloat() {
        // Test for method int java.lang.Float.hashCode()
        FastNumber f = new FastNumber(new Float(1908.8786f));
        assertTrue("Returned invalid hash code for 1908.8786f", f.hashCode() == Float
                .floatToIntBits(1908.8786f));

        f = new FastNumber(new Float(-1.112f));
        assertTrue("Returned invalid hash code for -1.112", f.hashCode() == Float
                .floatToIntBits(-1.112f));

        f = new FastNumber(new Float(0f));
        assertTrue("Returned invalid hash code for 0", f.hashCode() == Float.floatToIntBits(0f));
    }

}