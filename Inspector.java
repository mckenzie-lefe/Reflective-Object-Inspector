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
        // handle null object
        if (obj == null) {
            System.out.println(" Object is null");
            return;
        }

        System.out.println("Inspecting: " + obj + " (recursive = "+recursive+")");
        inspectObject(obj, obj.getClass(), recursive, 1);
    }

    protected void inspectObject(Object obj, Class<?> clazz, boolean recursive, int level) {
        Vector fieldObjs = new Vector();

        // handle null class
        if (clazz == null) {
            System.out.println("Class is null");
            return;
        }

        System.out.println(getObjectType(clazz, level));

        // handle Array Objects
        if (clazz.isArray()) {
            System.out.println(getArrayInfo(obj, clazz, level));

            if (recursive)
                inspectArrayObjects(obj, clazz, level);
        }
        
        System.out.println(getClassName(clazz, level));

        try {
            System.out.println(getSuperClass(clazz, level));
        } catch (Exception e) {}
        
        System.out.println(getInterfaces(clazz, level));
        
        System.out.println(indent(level) + "Methods:");
        for (Method method : clazz.getDeclaredMethods()) {
            System.out.println(getMethodInfo(method, level+1));
        }

        System.out.println(indent(level) + "Constructors:");
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            System.out.println(getConstructorInfo(constructor, level+1));
        }

        System.out.println(indent(level) + "Fields:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(getFieldInfo(obj, field, fieldObjs, level+1));
        }

        System.out.println();

        if (recursive)
            inspectFieldObjects(obj, clazz, fieldObjs, level);

        inspectInheritance(obj, clazz, level);
    }
           
    private void inspectArrayObjects(Object obj, Class<?> clazz, int level) {
        for (int i = 0; i < Array.getLength(obj); i++) {
            Object el =  Array.get(obj, i);
            if (el != null && !clazz.isPrimitive()) {
                System.out.println(indent(level)+ "Inspecting Array Object Value: " +el);
                inspectObject(el, el.getClass(), true, level+1);
            }
        }
    }

    protected void inspectInheritance(Object obj, Class<?> clazz, int level) {
        pprint(indent(level)+ "START " +clazz.getName()+ " Inheritance Hierarchy Traversal", "-");
        
        try {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                System.out.println(indent(level+1)+ "Inspecting Superclass: " +superClazz.getName());
                inspectObject(obj, superClazz, false, level+2);
            }
        } catch(Exception exp) { exp.printStackTrace(); }
        
        if (clazz.getInterfaces().length > 0) {
            for (Class<?> intf : clazz.getInterfaces()) {
                System.out.println(indent(level+1) +"Inspecting Interface: " + intf.getName());
                inspectObject(obj, intf, false, level+2);
            }
        }
        pprint(indent(level)+ "END " +clazz.getName()+ " Inheritance Hierarchy Traversal", "-");
        System.out.println();
    }

    protected void inspectFieldObjects(Object obj, Class clazz, Vector fieldObjs, int level) {
        if(fieldObjs.size() <= 0 )
            return;
        
        pprint(indent(level)+ "START Inspecting " +clazz.getName()+ " Field Objects", "*");
        
        Enumeration e = fieldObjs.elements();
        while(e.hasMoreElements()) {
            Field f = (Field) e.nextElement();

            System.out.println(indent(level)+ "---Inspecting " +clazz.getName()+ "'s Field: " +f.getName());
            System.out.println(indent(level+2)+ "Declaring Class: " +clazz.getName());

            try {
                Object fObj = f.get(obj);
                inspectObject(fObj , fObj.getClass(), true, level+2);

            } catch(Exception exp) { exp.printStackTrace(); }

            System.out.println(indent(level)+ "---END '" +f.getName()+ "' field inspection\n");
        }

        pprint(indent(level)+ "END Inspecting " +clazz.getName()+ " Field Objects", "*");
        System.out.println();
    }

    protected String getFieldInfo(Object obj, Field field, Vector fieldObjs, int level) {
        Object fieldObj = null;
        Class<?> fType = field.getType();
        String str = indent(level)+field.getName()+ "\n" + //
                    indent(level+1)+ "Type: " +fType+ "\n" + //
                    indent(level+1)+ "Modifiers: " +Modifier.toString(field.getModifiers());

        try {
            field.setAccessible(true);
        } catch (Exception e) {
            str = str+ "\n" +indent(level+1)+ "WARNING: Unable to make " +field.getName()+" field accessible";
            return str;
        }

        try {
            fieldObj= field.get(obj);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }

        if (fType.isArray()) 
            str = str+ "\n" +getArrayInfo(fieldObj, fType, level+1);
        else if (fType.isPrimitive())
            str = str+ "\n" +indent(level+1)+ "Value: " +fieldObj.toString();
        else {
            str = str+ "\n" +indent(level+1)+ "Value: " +fType.getName()+ //
                "@" +Integer.toHexString(System.identityHashCode(fieldObj));
            
            if (fieldObj != null && !fType.isPrimitive())
                fieldObjs.addElement(field);
        }
        return str;
    }

    protected String getConstructorInfo(Constructor<?> c, int level) {
        String str = indent(level)+c.getName()+ "\n" +indent(level+1)+ //
            "Modifiers: " +Modifier.toString(c.getModifiers())+ //
            "\n" +indent(level+1)+ "Parameter Types: ";

        Class<?>[] paramTypes = c.getParameterTypes();
        for (Class<?> pType: paramTypes) {
            str = str +pType.getName()+ ", ";
        }

        if (paramTypes.length > 0) str = str.substring(0, str.length()-2);

        return str;
    }

    protected String getMethodInfo(Method m, int level) {
        String str = indent(level)+m.getName()+ "\n" +indent(level+1) + //
            "Return Type: "+m.getReturnType().getName()+ "\n" +indent(level+1)+ //
            "Modifiers: " +Modifier.toString(m.getModifiers())+ "\n" + //
            indent(level+1)+ "Parameter Types: ";

        Class<?>[] paramTypes = m.getParameterTypes();
        for (Class<?> pType: paramTypes) {
            str = str +pType.getName()+ ", ";
        }
        if (paramTypes.length > 0) str = str.substring(0, str.length()-2);

        str = str+ "\n" +indent(level+1)+ "Exceptions: ";
        Class<?>[] exceptTypes = m.getExceptionTypes();
        for (Class<?> eType: exceptTypes) {
            str = str +eType.getName()+ ", ";
        }
        if (exceptTypes.length > 0) str = str.substring(0, str.length()-2);
        
        return str;
    }

    protected String getArrayInfo(Object obj, Class<?> clazz, int level) {
        int length = Array.getLength(obj);
        String str = indent(level)+ "Length: " +length+ "\n" + //
                    indent(level)+ "Component Type: " +clazz.getComponentType()+ //
                    "\n"+indent(level)+ "Values: [";

        if (length <= 0) return str + "]";

        for (int i = 0; i < length; i++) {
            if (i % 4 == 0) str = str + "\n" +indent(level+1);
            str = str +Array.get(obj, i)+ ", ";
        }
        return str.substring(0, str.length()-2) + " ]";
    }

    protected String getClassName(Class<?> clazz, int level) {
        return indent(level)+ "Class Name: " +clazz.getName();
    }

    protected String getSuperClass(Class<?> clazz, int level) {
        return indent(level)+ "Superclass: " +clazz.getSuperclass().getName();
    }
    
    protected String getInterfaces(Class<?> clazz, int level) {
        String str = indent(level)+ "Interfaces:\n";

        for (Class<?> intf : clazz.getInterfaces()) {
            str = str +indent(level+1)+ intf.getName() + "\n";
        }
        
        return str.substring(0, str.length()-1);
    }    

    protected String getObjectType(Class<?> clazz, int level) {
        if (clazz == null) 
            return indent(level) + "Object Type: null";
        else if (clazz.isArray()) 
            return indent(level) + "Object Type: Array";
        else if (clazz.isInterface()) 
            return indent(level) + "Object Type: Interface";
        else if (clazz.isPrimitive()) 
            return indent(level) + "Object Type: Primitive";
        else 
            return indent(level) + "Object Type: Class";
    }

    private void pprint(String msg, String fill) {
        int pad = 80 - msg.length();
        if (pad > 0) {
            for (int i = 0; i < pad; i++) {
                msg = msg + fill;
            }
        }
        System.out.println(msg);
    }

    private String indent(int level) {
        String str = "";
        for (int i = 0; i < level; i++) {
            str = str + ("   ");
        }
        return str;
    }
}
