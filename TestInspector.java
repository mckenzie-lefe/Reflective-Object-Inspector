import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Vector;

import org.junit.Before;

public class TestInspector{
    
    private Inspector i;

    @Before
    public void setUp() {
        i = new Inspector();
    }

    @Test 
    public void testGetClassName() {
        ClassA cA = new ClassA();
        
        assertEquals("Class Name: ClassA", i.getClassName(cA.getClass(), 0));
    }

    @Test 
    public void testGetSuperClass() {
        ClassB cB;
        try {
            cB = new ClassB();
            assertEquals("   Superclass: ClassC", i.getSuperClass(cB.getClass(), 1));

        } catch (Exception e) {}    
    }

    @Test 
    public void testGetInterface() {
        ClassB cB;
        try {
            cB = new ClassB();
            assertEquals("Interfaces:\n   java.lang.Runnable", i.getInterfaces(cB.getClass(), 0));
            
        } catch (Exception e) {}    
    }

    @Test 
    public void testGetArrayInfo() {
        Vector objsToInspect = new Vector();
        ClassB[] cB;

        try {
            cB = new ClassB[3];
            assertEquals("Length: 3\n" + //
                        "Component Type: class ClassB\n" + // 
                        "Values: [\n" + //
                        "   null, null, null ]", 
                i.getArrayInfo(cB, cB.getClass(), 0));

            assertTrue(objsToInspect.isEmpty());
            
        } catch (Exception e) {}   
    }

    @Test 
    public void testGetMethodInfo() {
        ClassD cD = new ClassD();
        Method m1, m2;

        try {
            m1 = cD.getClass().getMethod("getVal3", new Class[] {ClassD.class});
            m2 = cD.getClass().getMethod("toString", new Class[] {ClassD.class});

            assertEquals("getVal3\n" + //
                        "   Return Type: int\n" + //
                        "   Modifiers: public\n" + //
                        "   Parameter Types:\n" + //
                        "   Exceptions: ", i.getMethodInfo(m1, 0));

            assertEquals("toString\n" + // 
                        "   Return Type: java.lang.String\n" + //
                        "   Modifiers: public\n" + //
                        "   Parameter Types:\n" + //
                        "   Exceptions: ", i.getMethodInfo(m2, 0));

        } catch ( Exception e) {}
    }

    @Test 
    public void testGetConstructorInfo() {
        ClassD cD = new ClassD();
        Constructor c1, c2;

        try {
            c1 = cD.getClass().getConstructor(new Class[] {});
            c2 = cD.getClass().getConstructor(new Class[] {int.class});

            assertEquals("ClassD\n" + //
                        "   Modifiers: public\n" + //
                        "   Parameter Types: ", i.getConstructorInfo(c1, 0));
            assertEquals("ClassD\n" + //
                        "   Modifiers: public\n" + //
                        "   Parameter Types: int", i.getConstructorInfo(c2, 0));

        } catch ( Exception e) {} 
    }

    @Test 
    public void testGetFieldInfo() {
        Vector vObjs = new Vector();
        ClassD cD = new ClassD();
        Field f1, f2;

        try {
            f1 = cD.getClass().getField("val3");
            f2 = cD.getClass().getField("vallarray");

            assertEquals("val3\n" + //
                        "   Type: int\n" + //
                        "   Modifiers: private\n" + //
                        "   Value: 34", i.getFieldInfo(cD, f1, vObjs, 0));

            assertEquals("vallarray\n" + //
                        "   Type: [LClassA\n" + //
                        "   Modifiers: private\n" + //
                        "   Length: 10\n" + //
                        "   Component Type: class ClassA\n" + //
                        "   Values: [\n      null, null, null, null,\n" +
                        "      null, null, null, null,\n      null,null ]" , i.getFieldInfo(cD, f2, vObjs, 0));

            assertTrue(vObjs.isEmpty());

        } catch ( Exception e) {} 
    }
    
    @Test 
    public void testAddElementsToFieldObj() {
        Vector vObjs = new Vector();
        ClassD cD = new ClassD();
        Field f1;

        try {
            f1 = cD.getClass().getField("val");

            assertEquals("val\n" + //
                        "   Type: class ClassA\n" + //
                        "   Modifiers: private\n" + //
                        "   Value: ClassA@6f539caf", i.getFieldInfo(cD, f1, vObjs, 0));

            assertFalse(vObjs.isEmpty());

        } catch ( Exception e) {} 
    }

    @Test 
    public void testGetObjectType() {
        Object cA = new ClassA();
        Object cArr = new ClassA[12];
        
        assertEquals("Object Type: Class", i.getObjectType(cA.getClass(), 0));
        assertEquals("Object Type: null", i.getObjectType(null, 0));
        assertEquals("Object Type: Array", i.getObjectType(cArr.getClass(), 0));
    }
}
