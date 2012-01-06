/**
 * Class representing an AppleCore Virtual Machine address.
 */
package AppleCoreCompiler.AVM;

public class Address {
    public final int value;
    public final String label;

    public Address(int value, String label) {
	this.value=value;
	this.label=label;
    }

    public Address(int value) {
	this(value,null);
    }

    public Address(String label) {
	this(-1,label);
    }

    public static String asHexString(int v) {
	return "$"+Integer.toHexString(v).toUpperCase();
    }

    public String toString() {
	if (value >= 0) {
	    return asHexString(value);
	}
	return label;
    }
}
