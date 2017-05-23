package com.androidplot.util;

/**
 * An extension of {@link Number} optimized for speed at the cost of memory.
 */
public class FastNumber extends Number {

    private Number number;
    private boolean hasDoublePrimitive;
    private boolean hasFloatPrimitive;
    private boolean hasIntPrimitive;

    private double doublePrimitive;
    private float floatPrimitive;
    private int intPrimitive;

    public FastNumber(Number number) {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FastNumber that = (FastNumber) o;

        if (hasDoublePrimitive != that.hasDoublePrimitive) {
            return false;
        }
        if (hasFloatPrimitive != that.hasFloatPrimitive) {
            return false;
        }
        if (hasIntPrimitive != that.hasIntPrimitive) {
            return false;
        }
        if (Double.compare(that.doublePrimitive, doublePrimitive) != 0) {
            return false;
        }
        if (Float.compare(that.floatPrimitive, floatPrimitive) != 0) {
            return false;
        }
        //noinspection SimplifiableIfStatement
        if (intPrimitive != that.intPrimitive) {
            return false;
        }
        return number != null ? number.equals(that.number) : that.number == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = number != null ? number.hashCode() : 0;
        result = 31 * result + (hasDoublePrimitive ? 1 : 0);
        result = 31 * result + (hasFloatPrimitive ? 1 : 0);
        result = 31 * result + (hasIntPrimitive ? 1 : 0);
        temp = Double.doubleToLongBits(doublePrimitive);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (floatPrimitive != +0.0f ? Float.floatToIntBits(floatPrimitive) : 0);
        result = 31 * result + intPrimitive;
        return result;
    }

    @Override
    public String toString() {
        return String.valueOf(doubleValue());
    }
}
