package org.mulima.util;

/**
 * General object utility methods.
 */
public class ObjectUtil {
	private ObjectUtil() {
		throw new AssertionError("Cannot instantiate this class.");
	}
	
	/**
	 * Determines if two objects are equal.  This method
	 * handles nulls safely (assuming the obj1 equals
	 * method does as well).
	 * @param obj1 the first object
	 * @param obj2 the second object
	 * @return {@code true} if the two objects are equal
	 */
	public static boolean isEqual(Object obj1, Object obj2) {
		if (obj1 == null) {
			return obj2 == null;
		} else {
			return obj1.equals(obj2);
		}
	}
}
