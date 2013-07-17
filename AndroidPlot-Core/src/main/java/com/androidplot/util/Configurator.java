/*
 * Copyright 2012 AndroidPlot.com
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

package com.androidplot.util;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;

/**
 * Utility class for "configuring" objects via XML config files.  Supports the following field types:
 * String
 * Enum
 * int
 * float
 * boolean
 * <p/>
 * Config files should be stored in /res/xml.  Given the XML configuration /res/xml/myConfig.xml, one can apply the
 * configuration to an Object instance as follows:
 * <p/>
 * MyObject obj = new MyObject();
 * Configurator.configure(obj, R.xml.myConfig);
 * <p/>
 * WHAT IT DOES:
 * Given a series of parameters stored in an XML file, Configurator iterates through each parameter, using the name
 * as a map to the field within a given object.  For example:
 * <p/>
 * <pre>
 * {@code
 * <config car.engine.sparkPlug.condition="poor"/>
 * }
 * </pre>
 * <p/>
 * Given a Car instance car and assuming the method setCondition(String) exists within the SparkPlug class,
 * Configurator does the following:
 * <p/>
 * <pre>
 * {@code
 * car.getEngine().getSparkPlug().setCondition("poor");
 * }
 * </pre>
 * <p/>
 * Now let's pretend that setCondition takes an instance of the Condition enum as it's argument.
 * Configurator then does the following:
 * <p/>
 * car.getEngine().getSparkPlug().setCondition(Condition.valueOf("poor");
 * <p/>
 * Now let's look at how ints are handled.  Given the following xml:
 * <p/>
 * <config car.engine.miles="100000"/>
 * <p/>
 * would result in:
 * car.getEngine.setMiles(Integer.ParseInt("100000");
 * <p/>
 * That's pretty straight forward.  But colors are expressed as ints too in Android
 * but can be defined using hex values or even names of colors.  When Configurator
 * attempts to parse a parameter for a method that it knows takes an int as it's argument,
 * Configurator will first attempt to parse the parameter as a color.  Only after this
 * attempt fails will Configurator resort to Integer.ParseInt.  So:
 * <p/>
 * <config car.hood.paint.color="Red"/>
 * <p/>
 * would result in:
 * car.getHood().getPaint().setColor(Color.parseColor("Red");
 * <p/>
 * Next lets talk about float.  Floats can appear in XML a few different ways in Android,
 * especially when it comes to defining dimensions:
 * <p/>
 * <config height="10dp" depth="2mm" width="5em"/>
 * <p/>
 * Configurator will correctly parse each of these into their corresponding real pixel value expressed as a float.
 * <p/>
 * One last thing to keep in mind when using Configurator:
 * Values for Strings and ints can be assigned to localized values, allowing
 * a cleaner solution for those developing apps to run on multiple form factors
 * or in multiple languages:
 * <p/>
 * <config thingy.description="@string/thingyDescription"
 * thingy.titlePaint.textSize=""/>
 */
@SuppressWarnings("WeakerAccess")
public abstract class Configurator {

    private static final String TAG = Configurator.class.getName();
    protected static final String CFG_ELEMENT_NAME = "config";

    protected static int parseResId(Context ctx, String prefix, String value) {
        String[] split = value.split("/");
        // is this a localized resource?
        if (split.length > 1 && split[0].equalsIgnoreCase(prefix)) {
            String pack = split[0].replace("@", "");
            String name = split[1];
            return ctx.getResources().getIdentifier(name, pack, ctx.getPackageName());
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected static int parseIntAttr(Context ctx, String value) {
        try {
            return ctx.getResources().getColor(parseResId(ctx, "@color", value));
        } catch (IllegalArgumentException e1) {
            try {
                return Color.parseColor(value);
            } catch (IllegalArgumentException e2) {
                // wasn't a color so try parsing as a plain old int:
                return Integer.parseInt(value);
            }
        }
    }

    /**
     * Treats value as a float parameter.  First value is tested to see whether
     * it contains a resource identifier.  Failing that, it is tested to see whether
     * a dimension suffix (dp, em, mm etc.) exists.  Failing that, it is evaluated as
     * a plain old float.
     * @param ctx
     * @param value
     * @return
     */
    protected static float parseFloatAttr(Context ctx, String value) {
        try {
            return ctx.getResources().getDimension(parseResId(ctx, "@dimen", value));
        } catch (IllegalArgumentException e1) {
            try {
                return PixelUtils.stringToDimension(value);
            } catch (Exception e2) {
                return Float.parseFloat(value);
            }
        }
    }

    protected static String parseStringAttr(Context ctx, String value) {
        try {
            return ctx.getResources().getString(parseResId(ctx, "@string", value));
        } catch (IllegalArgumentException e1) {
            return value;
        }
    }


    protected static Method getSetter(Class clazz, final String fieldId) throws NoSuchMethodException {
        Method[] methods = clazz.getMethods();

        String methodName = "set" + fieldId;
        for (Method method : methods) {
            if (method.getName().equalsIgnoreCase(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException("No such public method (case insensitive): " +
                methodName + " in " + clazz);
    }

    @SuppressWarnings("unchecked")
    protected static Method getGetter(Class clazz, final String fieldId) throws NoSuchMethodException {
        Log.d(TAG, "Attempting to find getter for " + fieldId + " in class " + clazz.getName());
        String firstLetter = fieldId.substring(0, 1);
        String methodName = "get" + firstLetter.toUpperCase() + fieldId.substring(1, fieldId.length());
        return clazz.getMethod(methodName);
    }

    /**
     * Returns the object containing the field specified by path.
     * @param obj
     * @param path Path through member hierarchy to the destination field.
     * @return null if the object at path cannot be found.
     * @throws java.lang.reflect.InvocationTargetException
     *
     * @throws IllegalAccessException
     */
    protected static Object getObjectContaining(Object obj, String path) throws
            InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        if(obj == null) {
            throw new NullPointerException("Attempt to call getObjectContaining(Object obj, String path) " +
                    "on a null Object instance.  Path was: " + path);
        }
        Log.d(TAG, "Looking up object containing: " + path);
        int separatorIndex = path.indexOf(".");

        // not there yet, descend deeper:
        if (separatorIndex > 0) {
            String lhs = path.substring(0, separatorIndex);
            String rhs = path.substring(separatorIndex + 1, path.length());

            // use getter to retrieve the instance
            Method m = getGetter(obj.getClass(), lhs);
            if(m == null) {
                throw new NullPointerException("No getter found for field: " + lhs + " within " + obj.getClass());
            }
            Log.d(TAG, "Invoking " + m.getName() + " on instance of " + obj.getClass().getName());
            Object o = m.invoke(obj);
            // delve into o
            return getObjectContaining(o, rhs);
            //} catch (NoSuchMethodException e) {
            // TODO: log a warning
            //    return null;
            //}
        } else {
            // found it!
            return obj;
        }
    }

    @SuppressWarnings("unchecked")
    private static Object[] inflateParams(Context ctx, Class[] params, String[] vals) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        Object[] out = new Object[params.length];
        int i = 0;
        for (Class param : params) {
            if (Enum.class.isAssignableFrom(param)) {
                out[i] = param.getMethod("valueOf", String.class).invoke(null, vals[i].toUpperCase());
            } else if (param.equals(Float.TYPE)) {
                out[i] = parseFloatAttr(ctx, vals[i]);
            } else if (param.equals(Integer.TYPE)) {
                out[i] = parseIntAttr(ctx, vals[i]);
            } else if (param.equals(Boolean.TYPE)) {
                out[i] = Boolean.valueOf(vals[i]);
            } else if (param.equals(String.class)) {
                out[i] = parseStringAttr(ctx, vals[i]);
            } else {
                throw new IllegalArgumentException(
                        "Error inflating XML: Setter requires param of unsupported type: " + param);
            }
            i++;
        }
        return out;
    }

    /**
     *
     * @param ctx
     * @param obj
     * @param xmlFileId ID of the XML config file within /res/xml
     */
    public static void configure(Context ctx, Object obj, int xmlFileId) {
        XmlResourceParser xrp = ctx.getResources().getXml(xmlFileId);
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
                xrp.next();
                String name = xrp.getName();
                if (xrp.getEventType() == XmlResourceParser.START_TAG) {
                    if (name.equalsIgnoreCase(CFG_ELEMENT_NAME))
                        for (int i = 0; i < xrp.getAttributeCount(); i++) {
                            params.put(xrp.getAttributeName(i), xrp.getAttributeValue(i));
                        }
                    break;
                }
            }
            configure(ctx, obj, params);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            xrp.close();
        }
    }

    public static void configure(Context ctx, Object obj, HashMap<String, String> params) {
        for (String key : params.keySet()) {
            try {
                configure(ctx, obj, key, params.get(key));
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                Log.w(TAG, "Error inflating XML: Setter for field \"" + key + "\" does not exist. ");
                e.printStackTrace();
            }
        }
    }

    /**
     * Recursively descend into an object using key as the pathway and invoking the corresponding setter
     * if one exists.
     *
     * @param key
     * @param value
     */
    protected static void configure(Context ctx, Object obj, String key, String value)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Object o = getObjectContaining(obj, key);
        if (o != null) {
            int idx = key.lastIndexOf(".");
            String fieldId = idx > 0 ? key.substring(idx + 1, key.length()) : key;

            Method m = getSetter(o.getClass(), fieldId);
            Class[] paramTypes = m.getParameterTypes();
            // TODO: add support for generic type params
            if (paramTypes.length >= 1) {

                // split on "|"
                // TODO: add support for String args containing a |
                String[] paramStrs = value.split("\\|");
                if (paramStrs.length == paramTypes.length) {

                    Object[] oa = inflateParams(ctx, paramTypes, paramStrs);
                    Log.d(TAG, "Invoking " + m.getName() + " with arg(s) " + argArrToString(oa));
                    m.invoke(o, oa);
                } else {
                    throw new IllegalArgumentException("Error inflating XML: Unexpected number of argments passed to \""
                            + m.getName() + "\".  Expected: " + paramTypes.length + " Got: " + paramStrs.length);
                }
            } else {
                // Obvious this is not a setter
                throw new IllegalArgumentException("Error inflating XML: no setter method found for param \"" +
                        fieldId + "\".");
            }
        }
    }

    protected static String argArrToString(Object[] args) {
        String out = "";
        for(Object obj : args) {
            out += (obj == null ? (out += "[null] ") :
                    ("[" + obj.getClass() + ": " + obj + "] "));
        }
        return out;
    }
}

