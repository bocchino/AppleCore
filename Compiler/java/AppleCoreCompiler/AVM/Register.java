/**
 * Class representing an AppleCore Virtual Machine register.
 */
package AppleCoreCompiler.AVM;

public enum Register {
    SP(0,"SP"),IP(1,"IP"),FP(2,"FP");
    public int code;
    public String name;
    Register(int code, String name) {
	this.code = code;
	this.name = name;
    }
    public String toString() {
	return name;
    }
}
