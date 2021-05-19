package org.evosuite;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

public class AllFieldsCalculator {

	//Names of the classes to ignore
	private static Set<String> ignoreClasses;
	//Names of the fields to ignore (e.g. added by Mockito)
	private static Set<String> ignoreFields;

	static {
		ignoreClasses = new HashSet<>();
		ignoreFields = new HashSet<>();
		
		ignoreClasses.add("java.lang.Class");

		ignoreClasses.add("java.util.concurrent");
		ignoreClasses.add("java.io");
		//Includes CookieHandler, CookieManage, CookiePolicy, CookieStore and CookieStoreImpl
		ignoreClasses.add("java.net.Cookie");
		ignoreClasses.add("java.net.FileNameMap");
		ignoreClasses.add("java.net.Socket");
		ignoreClasses.add("java.net.Proxy");
		
		ignoreClasses.add("javax.net.ssl.SSLSocketFactory");
		
		ignoreFields.add("mockitoInterceptor");
	}

	/**
	 * Travels an object through reflection and calculates a value for its fields
	 * @param o - the object in question
	 * @return a long value unique to the object
	 * @throws Exception - if something goes wrong during the processing
	 */
	public static long allFieldsMethod(Object o) throws Exception {
		if(o == null || ignoreClass(o.getClass()) || ignoreObject(o)) {
			return 0l;
		}
		return allFieldsMethod(o, new HashSet<>());
	}

	/**
	 * Auxiliary method for allFieldsMethod
	 * @param o - the object to travel
	 * @param visited - a set of already seen objects
	 * @return a long value unique to the object
	 * @throws Exception - if something goes wrong during the processing
	 */
	private static long allFieldsMethod(Object o, Set<Object> visited) throws Exception {
		long result = 1;
		final long prime = 7;
		if(o != null && !visited.contains(o) && !ignoreClass(o.getClass())) {
			visited.add(o);
			if(validCanonicalName(o)) {
				String className = o.getClass().getCanonicalName();
				result = prime * result + className.hashCode();
				Field[] fields = o.getClass().getDeclaredFields();
				for(Field field: fields) {
					if(!Modifier.isStatic(field.getModifiers()) && 
							!field.isSynthetic() && !ignoreField(field)) {
						field.setAccessible(true);
						Class<?> fieldType = field.getType();
						if(fieldType.isPrimitive()) {
							result = prime * result + handlePrimitiveField(o, field);
						}
						else {
							Object fieldValue = field.get(o);
							if(fieldValue != null && !ignoreObject(fieldValue)) {
								if(isString(fieldType)) {
									result = prime * result + ((String)fieldValue).hashCode();
								}
								else if (fieldType.isArray()){
									result += handleArrayField(fieldValue, visited);
								}
								else if(isIterable(fieldType)) {
									result += handleIterableField(fieldValue, visited);
								}
								else if(fieldType.isEnum()) {
									result = prime * result + ((Enum<?>)fieldValue).hashCode();
								}
								else {
									if(!ignoreClass(fieldType))
										result += allFieldsMethod(fieldValue, visited);
								}
							}
						}
					}
				}
			}
		}
		else if(o == null && !visited.contains(o))
			result = prime;
		else
			result = 0;
		return result;
	}

	/**
	 * Calculates a value for a primitive field of an object
	 * @param o - the object in question
	 * @param field - the field in question
	 * @return a long value representative of the field
	 * @throws Exception - if something goes wrong during the processing
	 */
	private static int handlePrimitiveField(Object o, Field field) throws Exception {
		Class<?> fieldType = field.getType();
		int result = 0;
		if(isInt(fieldType))
			result = field.getInt(o);
		else if(isBoolean(fieldType))
			result = field.getBoolean(o)? 1231 : 1237;
		else if(isByte(fieldType))
			result = field.getByte(o);
		else if(isShort(fieldType))
			result = field.getShort(o);
		else if(isChar(fieldType))
			result = field.getChar(o);
		else if(isDouble(fieldType)) {
			long temp = Double.doubleToLongBits(field.getDouble(o));
			result = (int) (temp ^ (temp >>> 32));
		}
		else if(isLong(fieldType))
			result = (int) (field.getLong(o) ^ (field.getLong(o) >>> 32));
		else if(isFloat(fieldType))
			result = Float.floatToIntBits(field.getFloat(o));
		return result;
	}

	/**
	 * Iterates over an Array object and calculates its value
	 * @param array - the array in question
	 * @param visited - a set of already seen objects
	 * @return a long value of the array
	 * @throws Exception - if something goes wrong during processing
	 */
	private static long handleArrayField(Object array, 
			Set<Object> visited) throws Exception {
		long result = 0;
		final int length = Array.getLength(array);
		for (int i = 0; i < length; i ++) {
			Object arrayElement = Array.get(array, i);
			result += allFieldsMethod(arrayElement, visited);
		}	
		return result;
	}

	/**
	 * Iterates over an Iterable object and calculates its value
	 * @param iterable - the iterable in question
	 * @param visited - a set of already seen objects
	 * @return a long value of the Iterable
	 * @throws Exception - if something goes wrong during processing
	 */
	private static long handleIterableField(Object iterable, 
			Set<Object> visited) throws Exception {
		long result = 0;
		Iterable<?> i = (Iterable<?>) iterable;
		for(Object o: i) {
			result += allFieldsMethod(o, visited);
		}
		return result;
	}

	/**
	 * Checks if a class object is of primitive type 'int'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'int'; false otherwise
	 */
	public static boolean isInt(Class<?> clazz) {
		return clazz.getTypeName().equals("int");
	}

	/**
	 * Checks if a class object is of primitive type 'boolean'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'boolean'; false otherwise
	 */
	private static boolean isBoolean(Class<?> clazz) {
		return clazz.getTypeName().equals("boolean");
	}

	/**
	 * Checks if a class object is of primitive type 'byte'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'byte'; false otherwise
	 */
	private static boolean isByte(Class<?> clazz) {
		return clazz.getTypeName().equals("byte");
	}

	/**
	 * Checks if a class object is of primitive type 'short'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'short'; false otherwise
	 */
	private static boolean isShort(Class<?> clazz) {
		return clazz.getTypeName().equals("short");
	}

	/**
	 * Checks if a class object is of primitive type 'char'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'char'; false otherwise
	 */
	private static boolean isChar(Class<?> clazz) {
		return clazz.getTypeName().equals("char");
	}

	/**
	 * Checks if a class object is of primitive type 'double'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'double'; false otherwise
	 */
	private static boolean isDouble(Class<?> clazz) {
		return clazz.getTypeName().equals("double");
	}

	/**
	 * Checks if a class object is of primitive type 'long'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'long'; false otherwise
	 */
	private static boolean isLong(Class<?> clazz) {
		return clazz.getTypeName().equals("long");
	}

	/**
	 * Checks if a class object is of primitive type 'float'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'float'; false otherwise
	 */
	private static boolean isFloat(Class<?> clazz) {
		return clazz.getTypeName().equals("float");
	}

	/**
	 * Checks if a class object is of type 'String'
	 * @param clazz - the class object in question
	 * @return true if clazz is of type 'String'; false otherwise
	 */
	private static boolean isString(Class<?> clazz) {
		return clazz.getTypeName().equals("java.lang.String");
	}

	/**
	 * Checks if a class object is Iterable
	 * @param clazz - the class object in question
	 * @return true if clazz is iterable; false otherwise
	 */
	private static boolean isIterable(Class<?> clazz) {
		return Iterable.class.isAssignableFrom(clazz);
	}

	/**
	 * Checks if the object has a valid name
	 * @param o - the object in question
	 * @return true if the name is valid (i.e. not null); false otherwise
	 */
	private static boolean validCanonicalName(Object o) {
		return o.getClass().getCanonicalName() != null;
	}

	/**
	 * Checks if a given class is to be ignored when processing an object
	 * @param clazz - the given class
	 * @return true if the class' name is on the list of class prefixes to ignore; 
	 * 	false otherwise
	 */
	private static boolean ignoreClass(Class<?> clazz) {
		String className = clazz.getCanonicalName();
		for(String s: ignoreClasses) {
			if(className.startsWith(s) || className.equals(s))
				return true;
		}
		return false;//ignoreClasses.stream().anyMatch(c -> className.startsWith(c));
	}

	/**
	 * Checks if a given field is to be ignored when processing an object
	 * @param field - the given field
	 * @return true if the field's name is on the list of field names to ignore; 
	 * 	false otherwise
	 */
	private static boolean ignoreField(Field field) {
		String fieldName = field.getName();
		return ignoreFields.contains(fieldName);
	}
	
	/**
	 * Checks if a given object has been mocked
	 * @param o - the object in question
	 * @return true if it has been mocked by Mockito; false otherwise
	 */
	private static boolean ignoreObject(Object o) {
		for(Field f: o.getClass().getDeclaredFields()) {
			if(ignoreField(f))
				return true;
		}
		return false;
	}
}
