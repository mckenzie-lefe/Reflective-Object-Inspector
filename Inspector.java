/*==========================================================================
File: Inspector.java
Purpose: CPSC 501-F23 Assignmnet 2
Object inspector that does a complete introspection of an object at runtime.

Location: University of Calgary, Alberta, Canada
Created By: McKenzie
Created on:  Oct 17, 2023
Last Updated: Oct 17, 2023

========================================================================*/
import java.lang.reflect.*;

public class Inspector {

    public void inspect(Object obj, boolean recursive) {
        System.out.println("\nInspecting: " + obj + " (recursive = "+recursive+")");

        // handle null objects
        if (obj == null) {
            System.out.println(" Object is null");
            return;
        }

        // handle Array Objects
        if (obj.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(obj); i++) {
                inspect(Array.get(obj, i), recursive);
            }
        } 
    }
}
