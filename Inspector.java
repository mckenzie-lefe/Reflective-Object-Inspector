/*==========================================================================
File: Inspector.java
Purpose: CPSC 501-F23 Assignmnet 2
Object inspector that does a complete introspection of an object at runtime.

Location: University of Calgary, Alberta, Canada
Created By: McKenzie
Created on:  Oct 17, 2023
Last Updated: Oct 17, 2023

========================================================================*/
import java.util.*;
import java.lang.reflect.*;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        Vector objsToInspect = new Vector();

        System.out.println("\nInspecting: " + obj + " (recursive = "+recursive+")");

        // handle null objects
        if (obj == null) {
            System.out.println(" Object is null");
            return;
        }

        Class<?> clazz = obj.getClass();

        // handle Array Objects
        if (clazz.isArray()) {
            System.out.println(getArrayInfo(obj, clazz, objsToInspect));
        } 
        
        System.out.println(getClassName(clazz));
        System.out.println(getSuperClass(clazz));
        System.out.println(getInterfaces(clazz));
        
        Method[] methods = clazz.getDeclaredMethods();
        System.out.println("\tMethods:");;
        for (Method method : methods) {
            System.out.println(getMethodInfo(method));
        }
    }

    protected String getMethodInfo(Method m) {
        String str = "\t   " + m.getName() + //
            "\n\t\tReturn Type: " + m.getReturnType().getName() + //
            "\n\t\tModifiers: " + Modifier.toString(m.getModifiers()) + //
            "\n\t\tParameter Types: ";

        Class<?>[] paramTypes = m.getParameterTypes();
        for (Class<?> pType: paramTypes) {
            str = str + pType.getName() + ", ";
        }
        str.substring(0, str.length()-2);

        str = str + "\n\t\tExceptions: " ;
        Class<?>[] exceptTypes = m.getExceptionTypes();
        for (Class<?> eType: exceptTypes) {
            str = str + eType.getName()+ ", ";
        }

        return str.substring(0, str.length()-2);
    }

    protected String getArrayInfo(Object obj, Class<?> clazz, Vector objsToInspect) {
        int length = Array.getLength(obj);
        String str = "\tLength: " + length + "\n\tComponent Type: " +  //
                    clazz.getComponentType() + "\n\tArray Values: ";
        Object el;

        for (int i = 0; i < length; i++) {
            el =  Array.get(obj, i);
            str = str + i + "=" + el + ", ";
            if (el != null && !clazz.isPrimitive())
                objsToInspect.addElement(el);
        }
        
        return str.substring(0, str.length()-2);
    }

    protected String getClassName(Class<?> clazz) {
        return "\tClass Name: " + clazz.getName();
    }

    protected String getSuperClass(Class<?> clazz) {
        Class<?> superClazz = clazz.getSuperclass();
        return "\tSuperclass: " + superClazz.getName();
    }

    protected String getInterfaces(Class<?> clazz) {
        String str = "\tInterfaces:\n\t\t";
        Class<?>[] interfaces = clazz.getInterfaces();

        for (Class<?> intf : interfaces) {
            str = str + intf.getName() + "\n\t\t";
        }
        return str.substring(0, str.length()-3);
    }    

}
