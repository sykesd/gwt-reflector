Reflector - A lightweight reflection library for [GWT](http://code.google.com/webtoolkit/ "GWT Homepage")
====================================================

GWT does not implement the [Java Reflection API](http://docs.oracle.com/javase/7/docs/api/java/lang/reflect/package-summary.html).
This project provides a very simple replacement. 

The goals of the project are:

*  Not to implement the whole of the reflection API
*  Provide an easy way to read and write properties of JavaBean like objects
*  Provide an easy way to read the type of properties of JavaBean like objects

**Usage**

Add `<inherits name="org.dt.reflector.Reflector" />` to your .gwt.xml file.

Add `implements org.dt.reflector.Reflectable` to any of your Java classes that you would like to inspect at runtime.

Make calls to the helper methods in `org.dt.reflector.PropertyUtils` to get/set values or to get the types of the 
properties, for example:

    Object o = ...  // some type that implements Reflectable
    Object value = PropertyUtils.getProperty( (Reflectable) o, "name" );
    

