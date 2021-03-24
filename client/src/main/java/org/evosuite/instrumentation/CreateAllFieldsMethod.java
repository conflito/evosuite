package org.evosuite.instrumentation;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateAllFieldsMethod extends ClassVisitor{
	
	private class Field{
		
		private String name;
		private Type type;
		
		private Field(String name, Type type) {
			this.name = name;
			this.type = type;
		}
		
	}
	
	protected static final Logger logger = LoggerFactory.getLogger(CreateAllFieldsMethod.class);

	private static final Pattern ANONYMOUS_MATCHER1 = Pattern.compile(".*\\$\\d+.*$");
	
	private static final String ALL_FIELDS_METHOD_NAME = "allFieldsMethod";
	
	private final String className;
	
	private boolean isInterface = false;
	private boolean isAnonymous = false;
	private boolean isEnum = false;
	
	private final List<Field> fields = new ArrayList<>();
	
	public CreateAllFieldsMethod(ClassVisitor visitor, String className) {
		super(Opcodes.ASM5, visitor);
		this.className = className.replace(".", "/");
	}
	
	@Override
	public void visit(int version, int access, String name, String signature, 
			String superName, String[] interfaces) {
		super.visit(version, access, name, signature, superName, interfaces);
		isInterface = ((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE);
		if (ANONYMOUS_MATCHER1.matcher(name).matches()) {
			isAnonymous = true;
		}
		if (superName.equals(java.lang.Enum.class.getName().replace(".", "/"))) {
			isEnum = true;
		}
	}
	
	@Override
	public FieldVisitor visitField(int access, String name, String desc, 
			String signature, Object value) {
		
		fields.add(new Field(name, Type.getType(desc)));
		
		return super.visitField(access, name, desc, signature, value);
	}
	
	@Override
	public void visitEnd() {
		if(!isInterface && !isAnonymous && !isEnum) {
//			createAllFieldsMethod();
			createAuxMethods();
			createHandlePrimitiveFieldMethod();
			createHandleArrayFieldMethod();
			createStaticAllFieldsMethod();
		}
		super.visitEnd();
	}
	
	private void createHandlePrimitiveFieldMethod() {
		String[] exceptions = {"java/lang/Exception"};
		
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				"handlePrimitiveField", "(Ljava/lang/Object;Ljava/lang/reflect/Field;"
						+ "Ljava/lang/Class;)I", null, exceptions);
		
		Label _18 = new Label();
		Label _39 = new Label();
		Label _42 = new Label();
		Label _46 = new Label();
		Label _62 = new Label();
		Label _78 = new Label();
		Label _94 = new Label();
		Label _124 = new Label();
		Label _150 = new Label();
		Label _166 = new Label();
		
		mv.visitCode();
		
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isInt", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _18);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getInt", "(Ljava/lang/Object;)I", false);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitJumpInsn(Opcodes.GOTO, _166);
		mv.visitLabel(_18);
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isBoolean", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _46);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getBoolean", "(Ljava/lang/Object;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _39);
		mv.visitIntInsn(Opcodes.SIPUSH, 1231);
		mv.visitJumpInsn(Opcodes.GOTO, _42);
		mv.visitLabel(_39);
		mv.visitIntInsn(Opcodes.SIPUSH, 1237);
		mv.visitLabel(_42);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitJumpInsn(Opcodes.GOTO, _166);
		mv.visitLabel(_46);

		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isByte", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _62);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getByte", "(Ljava/lang/Object;)B", false);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitJumpInsn(Opcodes.GOTO, _166);
		mv.visitLabel(_62);
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isShort", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _78);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getShort", "(Ljava/lang/Object;)S", false);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitJumpInsn(Opcodes.GOTO, _166);
		mv.visitLabel(_78);
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isChar", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _94);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getChar", "(Ljava/lang/Object;)C", false);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitJumpInsn(Opcodes.GOTO, _166);
		mv.visitLabel(_94);
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isDouble", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _124);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getDouble", "(Ljava/lang/Object;)D", false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/Double", "doubleToLongBits", "(D)J", false);
		mv.visitVarInsn(Opcodes.LSTORE, 4);
		mv.visitVarInsn(Opcodes.LLOAD, 4);
		mv.visitVarInsn(Opcodes.LLOAD, 4);
		mv.visitVarInsn(Opcodes.BIPUSH, 32);
		mv.visitInsn(Opcodes.LUSHR);
		mv.visitInsn(Opcodes.LXOR);
		mv.visitInsn(Opcodes.L2I);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitJumpInsn(Opcodes.GOTO, _166);
		mv.visitLabel(_124);
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isLong", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _150);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getLong", "(Ljava/lang/Object;)J", false);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getLong", "(Ljava/lang/Object;)J", false);
		mv.visitVarInsn(Opcodes.BIPUSH, 32);
		mv.visitInsn(Opcodes.LUSHR);
		mv.visitInsn(Opcodes.LXOR);
		mv.visitInsn(Opcodes.L2I);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitJumpInsn(Opcodes.GOTO, _166);
		mv.visitLabel(_150);
		
		mv.visitVarInsn(Opcodes.ALOAD, 2);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isFloat", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _166);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getFloat", "(Ljava/lang/Object;)F", false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/Float", "floatToIntBits", "(F)I", false);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		
		mv.visitLabel(_166);
		mv.visitVarInsn(Opcodes.ILOAD, 3);
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void createHandleArrayFieldMethod() {
		String[] exceptions = {"java/lang/Exception"};
		
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				"handleArrayField", "(Ljava/lang/Object;II)I", null, exceptions);
		
		Label _14 = new Label();
		Label _35 = new Label();
		
		mv.visitCode();
		
		mv.visitVarInsn(Opcodes.ILOAD, 2);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/reflect/Array", "getLength", "(Ljava/lang/Object;)I", false);
		mv.visitVarInsn(Opcodes.ISTORE, 4);
		
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitVarInsn(Opcodes.ISTORE, 5);
		mv.visitJumpInsn(Opcodes.GOTO, _35);
		mv.visitLabel(_14);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 5);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/reflect/Array", "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 6);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitVarInsn(Opcodes.ILOAD, 3);
		mv.visitInsn(Opcodes.IMUL);
		mv.visitVarInsn(Opcodes.ALOAD, 6);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, ALL_FIELDS_METHOD_NAME, "(Ljava/lang/Object;)I", false);
		mv.visitInsn(Opcodes.IADD);
		mv.visitVarInsn(Opcodes.ISTORE, 3);
		mv.visitIincInsn(5, 1);
		mv.visitLabel(_35);
		mv.visitVarInsn(Opcodes.ILOAD, 5);
		mv.visitVarInsn(Opcodes.ILOAD, 4);
		mv.visitJumpInsn(Opcodes.IF_ICMPLT, _14);
		mv.visitVarInsn(Opcodes.ILOAD, 3);

		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
	}
	
	private void createAuxMethods() {
		String[] primitives = {"Int", "Boolean", "Byte", "Short", "Char",
				"Double", "Long", "Float"};
		
		for(String type: primitives) {
			String lower = type.toLowerCase();
			MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
					"is" + type, "(Ljava/lang/Class;)Z", null, null);
			
			mv.visitCode();
			
			mv.visitVarInsn(Opcodes.ALOAD, 0);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
					"java/lang/Class", "getTypeName", "()Ljava/lang/String;", false);
			mv.visitLdcInsn(lower);
			mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
					"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
			
			mv.visitInsn(Opcodes.IRETURN);
			mv.visitMaxs(0, 0);
			mv.visitEnd();
		}
		
//		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC,
//				"isIterable", "(Ljava/lang/Class;)Z", null, null);
//		mv.visitCode();
//		
//		mv.visitLdcInsn(Iterable.class);
//		mv.visitVarInsn(Opcodes.ALOAD, 0);
//		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
//				"java/lang/Class", "isAssignableFrom", "(Ljava/lang/Class;)Z", false);
//		mv.visitInsn(Opcodes.IRETURN);
//		mv.visitMaxs(0, 0);
//		mv.visitEnd();
	}
	
	private void createStaticAllFieldsMethod() {
		String[] exceptions = {"java/lang/Exception"};
		
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				ALL_FIELDS_METHOD_NAME, "(Ljava/lang/Object;)I", null, exceptions);
		
		Label _30 = new Label();
		Label _86 = new Label();
		Label _119 = new Label();
		Label _134 = new Label();
		Label _137 = new Label();
		Label _144 = new Label();
		
		mv.visitCode();
		
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitVarInsn(Opcodes.ISTORE, 1);
		mv.visitVarInsn(Opcodes.BIPUSH, 17);
		mv.visitVarInsn(Opcodes.ISTORE, 2);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		
		mv.visitJumpInsn(Opcodes.IFNULL, _144);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "getDeclaredFields", "()[Ljava/lang/reflect/Field;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 3);
		mv.visitVarInsn(Opcodes.ALOAD, 3);
		mv.visitInsn(Opcodes.DUP);
		mv.visitVarInsn(Opcodes.ASTORE, 7);
		mv.visitInsn(Opcodes.ARRAYLENGTH);
		mv.visitVarInsn(Opcodes.ISTORE, 6);
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitVarInsn(Opcodes.ISTORE, 5);
		mv.visitJumpInsn(Opcodes.GOTO, _137);
		mv.visitLabel(_30);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitVarInsn(Opcodes.ILOAD, 5);
		mv.visitInsn(Opcodes.AALOAD);
		mv.visitVarInsn(Opcodes.ASTORE, 4);
		mv.visitVarInsn(Opcodes.ALOAD, 4);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getModifiers", "()I", false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/reflect/Modifier", "isStatic", "(I)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _134);
		mv.visitVarInsn(Opcodes.ALOAD, 4);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "setAccessible", "(Z)V", false);
		mv.visitVarInsn(Opcodes.ALOAD, 4);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 8);
		mv.visitVarInsn(Opcodes.ALOAD, 8);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "isPrimitive", "()Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _86);
		mv.visitVarInsn(Opcodes.BIPUSH, 17);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitInsn(Opcodes.IMUL);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 4);
		mv.visitVarInsn(Opcodes.ALOAD, 8);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "handlePrimitiveField", 
				"(Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Class;)I", false);
		mv.visitInsn(Opcodes.IADD);
		mv.visitVarInsn(Opcodes.ISTORE, 1);
		mv.visitJumpInsn(Opcodes.GOTO, _134);
		mv.visitLabel(_86);
		mv.visitVarInsn(Opcodes.ALOAD, 8);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "isArray", "()Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _119);
		mv.visitVarInsn(Opcodes.ALOAD, 4);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 9);
		mv.visitVarInsn(Opcodes.ALOAD, 9);
		mv.visitJumpInsn(Opcodes.IFNULL, _134);
		mv.visitVarInsn(Opcodes.ALOAD, 9);
		mv.visitVarInsn(Opcodes.BIPUSH, 17);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "handleArrayField", "(Ljava/lang/Object;II)I", false);
		mv.visitVarInsn(Opcodes.ISTORE, 1);
		mv.visitJumpInsn(Opcodes.GOTO, _134);
		mv.visitLabel(_119);
		mv.visitVarInsn(Opcodes.BIPUSH, 17);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		mv.visitInsn(Opcodes.IMUL);
		mv.visitVarInsn(Opcodes.ALOAD, 4);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "allFieldsMethod:", "(Ljava/lang/Object;)I", false);
		mv.visitInsn(Opcodes.IADD);
		mv.visitVarInsn(Opcodes.ISTORE, 1);
		mv.visitLabel(_134);
		mv.visitIincInsn(5, 1);
		mv.visitLabel(_137);
		mv.visitVarInsn(Opcodes.ILOAD, 5);
		mv.visitVarInsn(Opcodes.ILOAD, 6);
		mv.visitJumpInsn(Opcodes.IF_ICMPLT, _30);
		mv.visitLabel(_144);
		mv.visitVarInsn(Opcodes.ILOAD, 1);
		
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}

//	private void createAllFieldsMethod() {
//		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
//				ALL_FIELDS_METHOD_NAME, "()I", null, null);
//		
//		mv.visitCode();
//		
//		Label resultStart = new Label();
//		Label resultEnd = new Label();
//		Label primeStart = new Label();
//		Label primeEnd = new Label();
//		
//		int thisId = 0;
//		
//		int nextId = fields.size() + 1;
//		
//		// int result = 1
//		int result = nextId;
//		nextId++;
//		mv.visitLocalVariable("result", "I", null, resultStart, resultEnd, result);
//		mv.visitInsn(Opcodes.ICONST_1);
//		mv.visitVarInsn(Opcodes.ISTORE, result);
//		
//		// int prime = 31
//		int prime = nextId;
//		nextId++;
//		mv.visitLocalVariable("prime", "I", null, primeStart, primeEnd, prime);
//		mv.visitLdcInsn(31);
//		mv.visitVarInsn(Opcodes.ISTORE, prime);
//
//		////////
////		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", 
////				"Ljava/io/PrintStream;");
////        mv.visitLdcInsn("Hello world!");
////        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", 
////        		"(Ljava/lang/String;)V", false);
//        /////////////
//        
//		for(Field f: fields) {
//			String type = f.type.getDescriptor();
//			
//			//add field value to stack
//			switch(type) {
//				case "I":
//				case "B":
//				case "C":
//				case "S":
//					mv.visitVarInsn(Opcodes.ALOAD, thisId);
//					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
//					break;
//				case "Z":
//					mv.visitVarInsn(Opcodes.ALOAD, thisId);
//					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
//					Label label = new Label();
//					mv.visitJumpInsn(Opcodes.IFEQ, label);
//					mv.visitLdcInsn(1231);//field = true
//					Label end = new Label();
//					mv.visitJumpInsn(Opcodes.GOTO, end);
//					mv.visitLabel(label);
//					mv.visitLdcInsn(1237);//field = false
//					mv.visitLabel(end);
//					break;
//				case "F":
//					mv.visitVarInsn(Opcodes.ALOAD, thisId);
//					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
//					mv.visitInsn(Opcodes.F2I);
//					break;
//				case "J":
//					mv.visitVarInsn(Opcodes.ALOAD, thisId);
//					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
//					mv.visitInsn(Opcodes.L2I);
//					break;
//				case "D":
//					mv.visitVarInsn(Opcodes.ALOAD, thisId);
//					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
//					mv.visitInsn(Opcodes.D2I);
//					break;
//				default:
//					mv.visitVarInsn(Opcodes.ALOAD, thisId);
//					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
//					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, f.type.getClassName(), 
//							ALL_FIELDS_METHOD_NAME, "()I", false);
//					break;
//			}
//			
//			//add result var  to stack
//			mv.visitVarInsn(Opcodes.ILOAD, result);
//			//add prime var to stack
//			mv.visitVarInsn(Opcodes.ILOAD, prime);
//			//multiply prime and result, keep result in stack
//			mv.visitInsn(Opcodes.IMUL); 
//			//sum field value and (prime * result), keep result in stack
//			mv.visitInsn(Opcodes.IADD);
//			//save into result var
//			mv.visitVarInsn(Opcodes.ISTORE, result);
//		}
//		
//		
//		
//		mv.visitVarInsn(Opcodes.ILOAD, result);
//		
//		mv.visitInsn(Opcodes.IRETURN);
//		mv.visitMaxs(0, 0);
//		mv.visitEnd();
//		
//	}
	
}
