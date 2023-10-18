import org.junit.Test;
import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
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
        
        assertEquals("\tClass Name: ClassA", i.getClassName(cA.getClass()));
    }

    @Test 
    public void testGetSuperClass() {
        ClassB cB;
        try {
            cB = new ClassB();
            assertEquals("\tSuperclass: ClassC", i.getSuperClass(cB.getClass()));

        } catch (Exception e) {}    
    }

    @Test 
    public void testGetInterface() {
        ClassB cB;
        try {
            cB = new ClassB();
            assertEquals("\tInterfaces:\n\t\tjava.lang.Runnable", i.getInterfaces(cB.getClass()));
            
        } catch (Exception e) {}    
    }

    @Test 
    public void testGetArrayInfo() {
        Vector objsToInspect = new Vector();
        ClassB[] cB;
        try {
            cB = new ClassB[3];
            assertEquals("\t\tLength: 3\n\t\tComponent Type: class ClassB\n\t\tArray Values: 0=null, 1=null, 2=null", 
                i.getArrayInfo(cB, cB.getClass(), objsToInspect));
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

            assertEquals("\t   getVal3\n\t\tReturn Type: int\n\t\tModifiers: public\n" + //
                "\t\tParameter Types:\n\t\tExceptions: ", i.getMethodInfo(m1));
            assertEquals("\t   toString\n\t\tReturn Type: java.lang.String\n\t\tModifiers:" + //
                " public\n\t\tParameter Types:\n\t\tExceptions: ", i.getMethodInfo(m2));

        } catch ( Exception e) {}
    }

    @Test 
    public void testGetConstructorInfo() {
        ClassD cD = new ClassD();
        Constructor c1, c2;

        try {
            c1 = cD.getClass().getConstructor(new Class[] {});
            c2 = cD.getClass().getConstructor(new Class[] {int.class});

            assertEquals("\t   ClassD\n\t\tModifiers: public" + //
                "\n\t\tParameter Types: ", i.getConstructorInfo(c1));
            assertEquals("\t   ClassD\n\t\tModifiers: public" + //
                "\n\t\tParameter Types: int", i.getConstructorInfo(c2));

        } catch ( Exception e) {} 
    }
}