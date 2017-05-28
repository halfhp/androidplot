package com.androidplot.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * An extension of {@link Number} optimized for speed at the cost of memory.
 */
public class FastNumber extends Number {

    @NonNull private final Number number;
    private boolean hasDoublePrimitive;
    private boolean hasFloatPrimitive;
    private boolean hasIntPrimitive;

    private double doublePrimitive;
    private float floatPrimitive;
    private int intPrimitive;

    /**
     * Safe-instantiator of FastNumber; returns a null result if the input Number is also null.
     * @param number
     * @return
     */
    public static FastNumber orNull(@NonNull Number number) {
        if(number == null) {
            return null;
        } else {
            return new FastNumber(number);
        }
    }

    private FastNumber(@NonNull Number number) {

        //noinspection ConstantConditions //in case someone ignores the @NonNull annotation
        if (number == null) {
            throw new IllegalArgumentException("number parameter cannot be null");
        }

        // avoid nested instances of FastNumber :
        if(number instanceof  FastNumber) {
            FastNumber rhs = (FastNumber) number;
            this.number = rhs.number;
            this.hasDoublePrimitive = rhs.hasDoublePrimitive;
            this.hasFloatPrimitive = rhs.hasFloatPrimitive;
            this.hasIntPrimitive = rhs.hasIntPrimitive;
            this.doublePrimitive = rhs.doublePrimitive;
            this.floatPrimitive = rhs.floatPrimitive;
            this.intPrimitive = rhs.intPrimitive;
        } else {
            this.number = number;
        }
    }

    @Override
    public int intValue() {
        if(!hasIntPrimitive) {
            intPrimitive = number.intValue();
            hasIntPrimitive = true;
        }
        return intPrimitive;
    }

    @Override
    public long longValue() {
        // TODO: optimize me!
        return number.longValue();
    }

    @Override
    public float floatValue() {
        if(!hasFloatPrimitive) {
            floatPrimitive = number.floatValue();
            hasFloatPrimitive = true;
        }
        return floatPrimitive;
    }

    @Override
    public double doubleValue() {
        if(!hasDoublePrimitive) {
            doublePrimitive = number.doubleValue();
            hasDoublePrimitive = true;
        }
        return doublePrimitive;
    }

    /**
     * To be equal, two instances must both be instances of {@link FastNumber}. The inner {@link
     * #number} field must also be a common type. Numbers which are mathematically equal are not
     * necessarily equal. This keeps with the java implementation of common Number classes where for
     * instance {@code new Integer(0).equals(new Double(0))} returns {@code false}
     */
    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FastNumber that = (FastNumber) o;

        return number.equals(that.number);

    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @NonNull
    @Override
    public String toString() {
        return String.valueOf(doubleValue());
    }
}
