### Declaration

 * Added being_declared flag to find "int x = x" errors
 
 * Added getMember function. Get a member of this declaration's type.
 * Added allowStaticReference function. 
	 * Utility function for checking if a declaration can be referenced from a static frame.

### Reference

 * Added getRuntimeMonitor

### ClassDecl

 * Override getMember to do checking for static frame
 * Added instanceSize field
 
### ArrayIdxDecl

 * Special declaration for an element of an array
 
### ThisDecl

 * Special declaration for a reference to "this"

### NullLiteral

### Type

 * Added getMember function. Get a member of this type. Returns null if none found
 
### Grammar

 * Allow "class" to be optionally prefixed by "public" (Makes testing easier)