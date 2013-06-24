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
import android.util.Log;
import com.androidplot.mock.MockContext;
import mockit.Mockit;
import mockit.UsingMocksAndStubs;
import org.junit.Test;

import java.lang.reflect.Method;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.fail;

@UsingMocksAndStubs({Log.class})
public class ConfiguratorTest {

    class A {
        int d = 0;

        public int getD() {
            return d;
        }

        public void setD(int d) {
            this.d = d;
        }
    }

    class B {
        A a = new A();

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }

    class C {
        B b = new B();

        public B getB() {
            return b;
        }

        public void setB(B a) {
            this.b = b;
        }
    }

    @org.junit.Before
    public void setUp() throws Exception {

    }

    @org.junit.After
    public void tearDown() throws Exception {

    }


    @Test
    public void testGetFieldAt() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        C c = new C();
        assertEquals(c, Configurator.getObjectContaining(c, "b"));
        assertEquals(c.getB(), Configurator.getObjectContaining(c, "b.a"));
        assertEquals(c.getB().getA(), Configurator.getObjectContaining(c, "b.a.d"));
    }

    @Test
    public void testGetSetter() throws Exception {
        Context context = Mockit.setUpMock(new MockContext());
        C c = new C();

        Method m = Configurator.getSetter(c.getClass(), "b");
        assertEquals(1, m.getParameterTypes().length);
        assertEquals(B.class, m.getParameterTypes()[0]);
    }
}
