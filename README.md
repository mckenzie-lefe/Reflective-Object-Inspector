# Reflective-Object-Inspector
A reflective object inspector that does a complete introspection of an object 
at runtime. The inspector is invoked using the method:
    public void inspect(Object obj, boolean recursive)

This method will introspect on the object specified by the first parameter, 
printing what it finds to standard output. You should find the following 
information about the object:

•	The name of the declaring class
•	The name of the immediate superclass
•	The name of the interfaces the class implements
•	The methods the class declares. For each, also find the following:
        •	The exceptions thrown
        •	The parameter types
        •	The return type
        •	The modifiers
•	The constructors the class declares. For each, also find the following:
        •	The parameter types
        •	The modifiers
•	The fields the class declares. For each, also find the following:
        •	The type
        •	The modifiers
        •	The current value of each field. If the field is an object 
        reference, and recursive is set to false, simply print out the 
        “reference value” directly.

The inspect method will also traverse the inheritance hierarchy to find all 
the methods, constructors, fields, and field values that each superclass and 
superinterface declares. Be sure you can also handle any array you might 
encounter, printing out its name, component type, length, and all its contents.
