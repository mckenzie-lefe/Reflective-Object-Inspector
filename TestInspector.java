import org.junit.Test;
import static org.junit.Assert.*;

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
            assertEquals("\tLength: 3\n\tComponent Type: class ClassB\n\tArray Values: 0=null, 1=null, 2=null", 
                i.getArrayInfo(cB, cB.getClass(), objsToInspect));
            assertTrue(objsToInspect.isEmpty());
            
        } catch (Exception e) {}   
    }
}
