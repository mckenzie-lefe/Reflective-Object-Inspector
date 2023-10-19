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
        // handle null objects
        if (obj == null) {
            System.out.println(" Object is null");
            return;
        }

        Vector arrayObjs = new Vector();
        Class<?> clazz = obj.getClass();

        System.out.println("Object:" + obj + " (recursive = "+recursive+")");

        // handle Array Objects
        if (clazz.isArray()) {
            System.out.println("\tArray Object:");
            System.out.println(getArrayInfo(obj, clazz, arrayObjs));

            Enumeration e = arrayObjs.elements();
            while(e.hasMoreElements()) {
                Object arrayObj = (Field) e.nextElement();
                inspect(arrayObj, recursive);
            }
        } else
            inspectObject(obj, clazz, recursive);
    }

    protected void inspectObject(Object obj, Class<?> clazz, boolean recursive) {
        Vector fieldObjs = new Vector();

        // handle null objects
        if (clazz == null) {
            System.out.println(" Class is null");
            return;
        }

        System.out.println(getClassName(clazz));

        try {
            System.out.println(getSuperClass(clazz));
        } catch (Exception e) {}
        
        System.out.println(getInterfaces(clazz));
        
        System.out.println("\tMethods:");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(getMethodInfo(method));
        }

        System.out.println("\tConstructors:");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            System.out.println(getConstructorInfo(constructor));
        }

        System.out.println("\tFields:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(getFieldInfo(obj, field, fieldObjs));
        }

        if (recursive)
            inspectFieldObjects(obj, clazz, fieldObjs);

        inspectInheritance(obj, clazz);
    }
            
    protected void inspectInheritance(Object obj, Class<?> clazz) {
        System.out.println("START "+ clazz.getName() + " Inheritance Hierarchy Traversal");
        
        try {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                System.out.println("Inspecting Superclass: " + superClazz.getName());
                inspectObject(obj, superClazz, false);
            }
        } catch(Exception exp) { exp.printStackTrace(); }
        
        if (clazz.getInterfaces().length > 0) {
            for (Class<?> intf : clazz.getInterfaces()) {
                System.out.println("Inspecting Interface: " + intf.getName());
                inspectObject(obj, intf, false);
            }
        }
        System.out.println("END of " + clazz.getName() + " Inheritance Hierarchy Traversal");
    }

    protected void inspectFieldObjects(Object obj, Class clazz, Vector fieldObjs) {
	
        if(fieldObjs.size() > 0 )
            System.out.println("\n---- Inspecting " + clazz.getName() + " Field Objects ----");
        
        Enumeration e = fieldObjs.elements();
        while(e.hasMoreElements()) {
            Field f = (Field) e.nextElement();

            System.out.println("***Inspecting "+ clazz.getName() + " Field: " + f.getName() + "***");
            try {
                inspect( f.get(obj) , true);
            } catch(Exception exp) { exp.printStackTrace(); }
            System.out.println("***END " + f.getName() + " field inspection***");
        }
    }

    protected String getFieldInfo(Object obj, Field field, Vector fieldObjs) {
        Object fieldObj = null;
        Class fType = field.getType();
        String str = "\t   " + field.getName() + "\n\t\tType: " + fType.getName();
        
        int mod = field.getModifiers();
        if(mod > 0)
            str = str + "\n\t\tModifiers: " + Modifier.toString(mod);
        else
            str = str + "\n\t\tModifiers:  NONE";

        try {
            field.setAccessible(true);
        } catch (Exception e) {
            str = str + "\n\t\tWARNING: Unable to make " + field.getName() +" field accessible";
            return str;
        }

        try {
            fieldObj= field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (fType.isArray()) 
            str = str + "\n" + getArrayInfo(fieldObj, fType, fieldObjs);
        else if (fType.isPrimitive())
            str = str + "\n\t\tValue: " + fieldObj.toString();
        else {
            str = str + "\n\t\tValue: " + fType.getName() + "@" + Integer.toHexString(System.identityHashCode(fieldObj));
            
            if (fieldObj != null && !field.getType().isPrimitive())
                fieldObjs.addElement(field);
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
                    clazz.getComponentType() + "\n\t\tArray Values: [";
        Object el;

        if (length <= 0) return str + "]";

        for (int i = 0; i < length; i++) {
            if (i % 4 == 0) str = str + "\n\t\t\t";
            el =  Array.get(obj, i);
            str = str + el + ", ";
            if (el != null && !clazz.isPrimitive())
                objsToInspect.addElement(el);
        }
        return str.substring(0, str.length()-2) + " ]";
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
