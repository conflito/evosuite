package org.evosuite.junit.writer;

import static org.evosuite.junit.writer.TestSuiteWriterUtils.*;

public class AllFieldsMethod {

	public static final String INNER_INNER_INNER_INNER_BLOCK_SPACE = "            ";
	public static final String INNER_INNER_INNER_INNER_INNER_BLOCK_SPACE = "              ";
	public static final String INNER_INNER_INNER_INNER_INNER_INNER_BLOCK_SPACE = "                ";
	
	public static String getSpecificMethod() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(getAllFieldsMethod());
		sb.append(getAuxAllFieldsMethod());
		sb.append(getHandleArrayFieldMethod());
		sb.append(getHandlePrimitiveFieldMethod());
		sb.append(getAuxMethods());
		
		return sb.toString();
	}
	
	private static Object getAllFieldsMethod() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n");
		sb.append(METHOD_SPACE);
		sb.append("public static long allFieldsMethod(Object o) throws Exception { \n");
		sb.append(BLOCK_SPACE + "return allFieldsMethodAux(o, new HashSet<>());\n");
		sb.append(METHOD_SPACE + "}\n");
		
		return sb.toString();
	}

	private static String getAuxAllFieldsMethod() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n");
		sb.append(METHOD_SPACE);
		sb.append("private static long allFieldsMethodAux(Object o, Set<Object> visited) throws Exception { \n");
		sb.append(BLOCK_SPACE + "long result = 1;\n");
		sb.append(BLOCK_SPACE + "final int prime = 17;\n");
		
		sb.append(BLOCK_SPACE + "if(o != null && !visited.contains(o)) {\n");
		sb.append(INNER_BLOCK_SPACE + "visited.add(o);\n");
		sb.append(INNER_BLOCK_SPACE + "Field[] fields = o.getClass().getDeclaredFields();\n");
		sb.append(INNER_BLOCK_SPACE + "for(Field field: fields) { \n");
		sb.append(INNER_INNER_BLOCK_SPACE + "if(!Modifier.isStatic(field.getModifiers()) "
				+ "&& !isFieldToIgnore(field)) {\n");
		
		sb.append(INNER_INNER_INNER_BLOCK_SPACE + "field.setAccessible(true);\n");
		sb.append(INNER_INNER_INNER_BLOCK_SPACE + "Class<?> fieldType = field.getType();\n");
		sb.append("\n");
		
		sb.append(INNER_INNER_INNER_BLOCK_SPACE + "if(!isClassToIgnore(fieldType)) {\n");

		sb.append(INNER_INNER_INNER_INNER_BLOCK_SPACE + "if(fieldType.isPrimitive()) \n");
		sb.append(INNER_INNER_INNER_INNER_INNER_BLOCK_SPACE + "result = prime * result + "
				+ "handlePrimitiveField(o, field, fieldType);\n");
		sb.append(INNER_INNER_INNER_INNER_BLOCK_SPACE + "else if(fieldType.isArray()) {\n");
		sb.append(INNER_INNER_INNER_INNER_INNER_BLOCK_SPACE + "final Object array = field.get(o);\n");
		sb.append(INNER_INNER_INNER_INNER_INNER_BLOCK_SPACE + "if(array != null) \n");
		sb.append(INNER_INNER_INNER_INNER_INNER_INNER_BLOCK_SPACE + "result += "
				+ "handleArrayField(array, visited);\n");
		sb.append(INNER_INNER_INNER_INNER_BLOCK_SPACE + "}\n");
		sb.append(INNER_INNER_INNER_INNER_BLOCK_SPACE + "else\n");
		sb.append(INNER_INNER_INNER_INNER_INNER_BLOCK_SPACE + "result += "
				+ "allFieldsMethodAux(field.get(o), visited);\n");
		
		sb.append(INNER_INNER_INNER_BLOCK_SPACE + "}\n");
		sb.append(INNER_INNER_BLOCK_SPACE + "}\n");
		sb.append(INNER_BLOCK_SPACE + "}\n");
		sb.append(BLOCK_SPACE + "}\n");
		sb.append(BLOCK_SPACE + "else {\n");
		sb.append(INNER_BLOCK_SPACE + "result = 0;\n");
		sb.append(BLOCK_SPACE + "}\n");
		
		sb.append(BLOCK_SPACE + "return result;\n");
		
		sb.append(METHOD_SPACE + "}\n");
		
		return sb.toString();
	}
	
	private static String getHandleArrayFieldMethod() {
		StringBuilder sb = new StringBuilder();
	
		sb.append("\n");
		sb.append(METHOD_SPACE);
		sb.append("private static long handleArrayField(Object array, \n"); 
		sb.append(INNER_BLOCK_SPACE + "Set<Object> visited) throws Exception {\n");
		
		sb.append(BLOCK_SPACE + "long result = 0;\n");
		sb.append(BLOCK_SPACE + "final int length = Array.getLength(array);\n");
		sb.append(BLOCK_SPACE + "for (int i = 0; i < length; i ++) {\n");
		sb.append(INNER_BLOCK_SPACE + "Object arrayElement = Array.get(array, i);\n");
		sb.append(INNER_BLOCK_SPACE + "result += allFieldsMethodAux(arrayElement, visited);\n");
		sb.append(BLOCK_SPACE + "}\n");
		sb.append(BLOCK_SPACE + "return result;\n");
		sb.append(METHOD_SPACE + "}\n");
		return sb.toString();
	}
	
	private static String getHandlePrimitiveFieldMethod() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("\n");
		sb.append(METHOD_SPACE);
		sb.append("private static int handlePrimitiveField(Object o, Field field, "
				+ "Class<?> fieldType) \n");
		sb.append(INNER_BLOCK_SPACE + "throws Exception {\n");
		
		sb.append(BLOCK_SPACE + "int result = 0;\n");
		sb.append(BLOCK_SPACE + "if(isInt(fieldType))\n");
		sb.append(INNER_BLOCK_SPACE + "result = field.getInt(o);\n");
		sb.append(BLOCK_SPACE + "else if(isBoolean(fieldType))\n");
		sb.append(INNER_BLOCK_SPACE + "result = field.getBoolean(o)? 1231 : 1237;\n");
		sb.append(BLOCK_SPACE + "else if(isByte(fieldType))\n");
		sb.append(INNER_BLOCK_SPACE + "result = field.getByte(o);\n");
		sb.append(BLOCK_SPACE + "else if(isShort(fieldType))\n");
		sb.append(INNER_BLOCK_SPACE + "result = field.getShort(o);\n");
		sb.append(BLOCK_SPACE + "else if(isChar(fieldType))\n");
		sb.append(INNER_BLOCK_SPACE + "result = field.getChar(o);\n");
		sb.append(BLOCK_SPACE + "else if(isDouble(fieldType)) {\n");
		sb.append(INNER_BLOCK_SPACE + "long temp = "
				+ "Double.doubleToLongBits(field.getDouble(o));\n");
		sb.append(INNER_BLOCK_SPACE + "result = (int) (temp ^ (temp >>> 32));\n");
		sb.append(BLOCK_SPACE + "}\n");
		sb.append(BLOCK_SPACE + "else if(isLong(fieldType))\n");
		sb.append(INNER_BLOCK_SPACE + "result = (int) (field.getLong(o) ^ "
				+ "(field.getLong(o) >>> 32));\n");
		sb.append(BLOCK_SPACE + "else if(isFloat(fieldType))\n");
		sb.append(INNER_BLOCK_SPACE + "result = Float.floatToIntBits"
				+ "(field.getFloat(o));\n");
		sb.append(BLOCK_SPACE + "return result;\n");
		sb.append(METHOD_SPACE + "}\n");
		
		return sb.toString();
	}
	
	private static String getAuxMethods() {
		StringBuilder sb = new StringBuilder();
		
		String[] primitives = {"Int", "Boolean", "Byte", "Short", "Char",
				"Double", "Long", "Float"};
		
		for(String type: primitives) {
			String lower = type.toLowerCase();
			
			sb.append("\n");
			sb.append(METHOD_SPACE);
			sb.append("private static boolean is" + type + "(Class<?> clazz) {\n");
			sb.append(BLOCK_SPACE + "return clazz.getTypeName()"
					+ ".equals(\"" + lower +"\");\n");
			sb.append(METHOD_SPACE + "}\n");
		}
		
		sb.append("\n");
		sb.append(METHOD_SPACE);
		sb.append("private static boolean isClassToIgnore(Class<?> clazz) {\n");
		sb.append(BLOCK_SPACE + "String className = clazz.getCanonicalName();\n");
		sb.append(BLOCK_SPACE + "return className.equals"
				+ "(\"java.util.concurrent.ExecutorService\") ||\n");
		sb.append(INNER_INNER_INNER_INNER_BLOCK_SPACE + "className.equals"
				+ "(\"com.squareup.okhttp.internal.DiskLruCache\");\n");
		sb.append(METHOD_SPACE + "}\n");
		
		
		sb.append("\n");
		sb.append(METHOD_SPACE);
		sb.append("private static boolean isFieldToIgnore(Field field) {\n");
		sb.append(BLOCK_SPACE + "return field.getName().equals(\"mockitoInterceptor\");\n");
		sb.append(METHOD_SPACE + "}\n");
		
		return sb.toString();
	}
}
