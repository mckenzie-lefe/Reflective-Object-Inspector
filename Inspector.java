/*==========================================================================
File: Inspector.java
Purpose: CPSC 501-F23 Assignmnet 2
Object inspector that does a complete introspection of an object at runtime.

Location: University of Calgary, Alberta, Canada
Created By: McKenzie
Created on:  Oct 17, 2023
Last Updated: Oct 19, 2023

========================================================================*/
import java.util.*;
import java.lang.reflect.*;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        if (obj == null) {
            System.out.println(" Object is null");
            return;
        }

        System.out.println("Inspecting: " + obj + " (recursive = "+recursive+")");
        try {
            inspectObject(obj, obj.getClass(), recursive, 1);
        } catch (Exception e) { e.printStackTrace(); }
        
    }

    /**
     * Called by {@code inspect} method to introspect {@code clazz}.
     * If recursive is {@code true} then each field that is an object will be 
     * fully inspected.
     * 
     * Note: main purpose of extracting inspectObject from inspect was to allow 
     * for nicely formated output. Inculding {@code level} as a param allows 
     * the indent to match the object being inspected while travering the 
     * inheritance hierarchy or recusivly inspecting class objects.
     * 
     * @param obj       the object containing {@code clazz} 
     * @param clazz     the declaring class of elements in {@code fieldObjs}
     * @param recursive indicates when to recursivly inspect object fields.
     * @param level     min number of indents to start a new line with
     */
    protected void inspectObject(Object obj, Class<?> clazz, boolean recursive, int level) {
        if (clazz == null) {
            System.out.println("Class is null");
            return;
        }

        System.out.println(getObjectType(clazz, level));

        // handle Array Objects
        if (clazz.isArray()) {
            System.out.println(getArrayInfo(obj, clazz, level));

            if (recursive)
                inspectArrayObjects(obj, level);
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

        Vector fieldObjs = new Vector();
        System.out.println(indent(level) + "Fields:");
        for (Field field : clazz.getDeclaredFields()) {
            System.out.println(getFieldInfo(obj, field, fieldObjs, level+1));
        }

        System.out.println();

        if (recursive)
            inspectFieldObjects(obj, clazz, fieldObjs, level);

        inspectInheritance(obj, clazz, level);
    }
      
    
    /**
     * Called by {@code inspectObject} method to recursivly introspect the object 
     * elements in an array if recursion is {@code true}.
     * 
     * @param obj       the array object with object elements to introspect
     * @param level     min number of indents to start a new line with
     */
    private void inspectArrayObjects(Object obj, int level) {
        for (int i = 0; i < Array.getLength(obj); i++) {
            Object el =  Array.get(obj, i);

            if (el != null && !el.getClass().isPrimitive()) {
                System.out.println(indent(level)+ //
                                    "Inspecting Array Object Value: " +el);
                inspectObject(el, el.getClass(), true, level+1);
            }
        }
    }

    /**
     * Called by {@code inspectObject} method to introspect the inheritance 
     * hierarchy of {@code clazz}.
     * 
     * @param obj       the object containing {@code clazz} 
     * @param clazz     the class which superclasses and superinterfaces 
     *                  traverse and introspect
     * @param level     min number of indents to start a new line with
     */
    protected void inspectInheritance(Object obj, Class<?> clazz, int level) {
        pprint(indent(level)+ "START " +clazz.getName()+ //
                " Inheritance Hierarchy Traversal", "-");
        
        try {
            Class<?> superClazz = clazz.getSuperclass();
            if (superClazz != null) {
                System.out.println(indent(level+1)+ //
                            "Inspecting Superclass: " +superClazz.getName());
                inspectObject(obj, superClazz, false, level+2);
            }
        } catch(Exception exp) { exp.printStackTrace(); }
        
        if (clazz.getInterfaces().length > 0) {
            for (Class<?> intf : clazz.getInterfaces()) {
                System.out.println(indent(level+1) + //
                                "Inspecting Interface: " +intf.getName());
                inspectObject(obj, intf, false, level+2);
            }
        }
        pprint(indent(level)+ "END " +clazz.getName()+ //
                         " Inheritance Hierarchy Traversal", "-");
        System.out.println();
    }

    /**
     * Called by {@code inspectObject} method to recursivly introspect class 
     * field objects when recursion is {@code true}.
     * 
     * @param obj       the object {@code clazz} belongs to 
     * @param clazz     the declaring class of elements in {@code fieldObjs}
     * @param fieldObjs Fields from {@ode clazz} that are objects that are to 
     *                  be instrospected if recursive is true
     * @param level     min number of indents to start a new line with
     */
    protected void inspectFieldObjects(Object obj, Class clazz, 
                                        Vector fieldObjs, int level) {
        if(fieldObjs.size() <= 0 )
            return;
        
        pprint(indent(level)+ "START Inspecting " +clazz.getName()+ //
                        " Field Objects", "*");
        
        Enumeration e = fieldObjs.elements();
        while(e.hasMoreElements()) {
            Field f = (Field) e.nextElement();

            System.out.println(indent(level)+ "---Inspecting " + //
                                clazz.getName()+"'s Field: " +f.getName());
            System.out.println(indent(level+2)+ "Declaring Class: " +//
                                clazz.getName());

            try {
                Object fObj = f.get(obj);
                inspectObject(fObj , fObj.getClass(), true, level+2);

            } catch(Exception exp) { exp.printStackTrace(); }

            System.out.println(indent(level)+ "---END '" +f.getName()+ //
                                "' field inspection\n");
        }

        pprint(indent(level)+ "END Inspecting " +clazz.getName()+ //
                " Field Objects", "*");
        System.out.println();
    }

    /**
     * Called by {@code inspectObject} method to introspect the given field.
     * Will add {@code field} to {@code fieldObjs} if it is a class or an array 
     * object.
     * 
     * @param obj       the object containing {@code field} 
     * @param field     the field which to introspect
     * @param fieldObjs objects to be instrospected if recursive is true
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} containing information of {@code field} type, 
     *                  modifiers and value.
     */
    protected String getFieldInfo(Object obj, Field field, 
                                    Vector fieldObjs, int level) {
        Object fieldObj = null;
        Class<?> fType = field.getType();
        String str = indent(level)+field.getName()+ "\n" + //
                    indent(level+1)+ "Type: " +fType+ "\n" + //
                    indent(level+1)+ "Modifiers: " + //
                    Modifier.toString(field.getModifiers());

        try {
            field.setAccessible(true);
        } catch (Exception e) {
            return str + "\n" +indent(level+1)+ "WARNING: Unable to make " 
                    +field.getName()+" field accessible";
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

    /**
     * Called by {@code inspectObject} method to introspect the given 
     * constructor.
     * Will add {@code field} to {@code fieldObjs} if it is a class or an array 
     * object.
     * 
     * @param c     constructor to introspect  
     * @param level min number of indents to start a new string with
     * 
     * @return          {@code String} containing information of {@code c} 
     *                  name, modifiers and parameter types.
     */
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

    /**
     * Called by {@code inspectObject} method to introspect the given method.
     * 
     * @param m         method to introspect
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} containing information of {@code m} 
     *                  name, return type, modifiers, parameter types and 
     *                  exceptions thrown.
     */
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

    /**
     * Called by {@code inspectObject} method to introspect the given 
     * 
     * @param obj       the object containing {@code arr} 
     * @param arr       array object to introspect
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} containing information of {@code arr}
     *                  length, component type and values.
     */
    protected String getArrayInfo(Object obj, Class<?> arr, int level) {
        int length = Array.getLength(obj);
        String str = indent(level)+ "Length: " +length+ "\n" + //
                    indent(level)+ "Component Type: " +arr.getComponentType()+ //
                    "\n"+indent(level)+ "Values: [";

        if (length <= 0) return str + "]";

        for (int i = 0; i < length; i++) {
            if (i % 4 == 0) str = str + "\n" +indent(level+1);
            str = str +Array.get(obj, i)+ ", ";
        }

        return str.substring(0, str.length()-2) + " ]";
    }

    /**
     * Called by {@code inspectObject} method to get the name of the given
     * class object.
     * Note: main purpose to have extracted this method is for testing
     * 
     * @param clazz     class whose name to find
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} containing class name
     */
    protected String getClassName(Class<?> clazz, int level) {
        return indent(level)+ "Class Name: " +clazz.getName();
    }

    /**
     * Called by {@code inspectObject} method to get {@code clazz} superclass.
     * Note: main purpose to have extracted this method is for testing
     * 
     * @param clazz     class whose superclass to find
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} containing superclass
     */
    protected String getSuperClass(Class<?> clazz, int level) {
        return indent(level)+ "Superclass: " +clazz.getSuperclass().getName();
    }
    
    /**
     * Called by {@code inspectObject} method to get the interfaces of 
     * {@code clazz}
     * 
     * @param clazz     class whose interfaces to find
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} containing interfaces
     */
    protected String getInterfaces(Class<?> clazz, int level) {
        String str = indent(level)+ "Interfaces:\n";

        for (Class<?> intf : clazz.getInterfaces()) {
            str = str +indent(level+1)+ intf.getName() + "\n";
        }
        
        return str.substring(0, str.length()-1);
    }    

    /**
     * Called by {@code inspectObject} method to get an objects type
     * 
     * @param clazz     class whose object type to find
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} containing object type
     */
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

    /**
     * Used to pretty print openning and closing headers for class and 
     * object inspections
     * 
     * @param msg   message to pretty print     
     * @param fill  character to fill empty space after msg up to column 80     
     */
    private void pprint(String msg, String fill) {
        int pad = 80 - msg.length();
        if (pad > 0) {
            for (int i = 0; i < pad; i++) {
                msg = msg + fill;
            }
        }
        System.out.println(msg);
    }

    /**
     * Used to create string of spaces used to indent a new line
     * @param level     min number of indents to start a new string with
     * 
     * @return          {@code String} of spaces correponding to {@code level}
     */
    private String indent(int level) {
        String str = "";
        for (int i = 0; i < level; i++) {
            str = str + ("   ");
        }
        return str;
    }
}
