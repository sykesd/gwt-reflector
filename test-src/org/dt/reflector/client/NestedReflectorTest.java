/**
 * org.dt.reflector.client.SimpleTestReflection.java
 *
 * Copyright (c) 2007-2014 UShareSoft SAS, All rights reserved
 * @author UShareSoft
 */
package org.dt.reflector.client;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import org.dt.reflector.client.PropertyUtils;
import org.dt.reflector.client.Reflectable;
import org.dt.reflector.client.Reflector;

import com.google.gwt.junit.client.GWTTestCase;

public class NestedReflectorTest extends GWTTestCase {

    public NestedReflectorTest() {
    }

    @Override
    public String getModuleName() { return "org.dt.reflector.Reflector"; }

    public static class MyClass implements Reflectable {
        private int myInt;
        private boolean myBool;
        public MyClass(){
        }

        public int getMyInt() {
            return myInt;
        }

        public void setMyInt(int myInt) {
            this.myInt = myInt;
        }

        public boolean isMyBool() {
            return myBool;
        }

        public void setMyBool(boolean myBool) {
            this.myBool = myBool;
        }
    }

    public void testReflection(){
        MyClass myobject = new MyClass();
        myobject.setMyBool(true);
        myobject.setMyInt(10);
        Reflector refl = PropertyUtils.getReflector(MyClass.class);
        assertNotNull(refl);
        List<String> props = Arrays.asList(refl.list(myobject));
        assertTrue(props.contains("myInt"));
        assertTrue(props.contains("myBool"));
        assertEquals("10", refl.get(myobject, "myInt").toString());
        assertEquals("true", refl.get(myobject, "myBool").toString());
    }
}
