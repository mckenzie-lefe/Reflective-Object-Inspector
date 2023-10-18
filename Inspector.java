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
            System.out.println("\tArray Object:");
            System.out.println(getArrayInfo(obj, clazz, objsToInspect));
        } 
        
        System.out.println(getClassName(clazz));
        System.out.println(getSuperClass(clazz));
        System.out.println(getInterfaces(clazz));
        
        Method[] methods = clazz.getDeclaredMethods();
        System.out.println("\tMethods:");
        for (Method method : methods) {
            System.out.println(getMethodInfo(method));
        }

        System.out.println("\tConstructors:");
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        for (Constructor<?> constructor : constructors) {
            System.out.println(getConstructorInfo(constructor));
        }

        System.out.println("\tFields:");
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            System.out.println(getFieldInfo(obj, field, objsToInspect));
        }
    }

    protected String getFieldInfo(Object obj, Field field, Vector objsToInspect) {
        field.setAccessible(true);
        Object fieldObj = null;
        Class fType = field.getType();
        String str = "\t   " + field.getName() + "\n\t\tType: " + fType.getName();
        
        int mod = field.getModifiers();
        if(mod > 0)
            str = str + "\n\t\tModifiers: " + Modifier.toString(mod);
        else
            str = str + "\n\t\tModifiers:  NONE";

        try {
            fieldObj= field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (fType.isArray()) 
            str = str + "\n" + getArrayInfo(fieldObj, fType, objsToInspect);
        else if (fType.isPrimitive())
            str = str + "\n\t\tValue: " + fieldObj.toString();
        else {
            str = str + "\n\t\tValue: " + fType.getName() + "@" + Integer.toHexString(System.identityHashCode(fieldObj));
            
            if (fieldObj != null && !field.getType().isPrimitive())
                objsToInspect.addElement(field);
        }
        return str;
    }

    protected String getConstructorInfo(Constructor<?> c) {
        String str = "\t   " + c.getName() + "\n\t\t" + //
            "Modifiers: " + Modifier.toString(c.getModifiers()) + //
            "\n\t\tParameter Types: ";

        Class<?>[] paramTypes = c.getParameterTypes();
        for (Class<?> pType: paramTypes) {
            str = str + pType.getName() + ", ";
        }
        if (paramTypes.length > 0) str = str.substring(0, str.length()-2);

        return str;
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
        if (paramTypes.length > 0) str = str.substring(0, str.length()-2);

        str = str + "\n\t\tExceptions: ";
        Class<?>[] exceptTypes = m.getExceptionTypes();
        for (Class<?> eType: exceptTypes) {
            str = str + eType.getName()+ ", ";
        }
        if (exceptTypes.length > 0) str = str.substring(0, str.length()-2);
        
        return str;
    }

    protected String getArrayInfo(Object obj, Class<?> clazz, Vector objsToInspect) {
        int length = Array.getLength(obj);
        String str = "\t\tLength: " + length + "\n\t\tComponent Type: " +  //
                    clazz.getComponentType() + "\n\t\tArray Values: ";
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
