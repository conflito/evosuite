package org.evosuite.instrumentation;

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
	
	private static final String ALL_FIELDS_METHOD_NAME = "__allFieldsMethod";
	
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
			createAllFieldsMethod();
		}
		super.visitEnd();
	}

	private void createAllFieldsMethod() {
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC,
				ALL_FIELDS_METHOD_NAME, "()I", null, null);
		
		mv.visitCode();
		
		Label resultStart = new Label();
		Label resultEnd = new Label();
		Label primeStart = new Label();
		Label primeEnd = new Label();
		
		int thisId = 0;
		
		int nextId = fields.size() + 1;
		
		// int result = 1
		int result = nextId;
		nextId++;
		mv.visitLocalVariable("result", "I", null, resultStart, resultEnd, result);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitVarInsn(Opcodes.ISTORE, result);
		
		// int prime = 31
		int prime = nextId;
		nextId++;
		mv.visitLocalVariable("prime", "I", null, primeStart, primeEnd, prime);
		mv.visitLdcInsn(31);
		mv.visitVarInsn(Opcodes.ISTORE, prime);

		////////
//		mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", 
//				"Ljava/io/PrintStream;");
//        mv.visitLdcInsn("Hello world!");
//        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", 
//        		"(Ljava/lang/String;)V", false);
        /////////////
        
		for(Field f: fields) {
			String type = f.type.getDescriptor();
			
			//add field value to stack
			switch(type) {
				case "I":
				case "B":
				case "C":
				case "S":
					mv.visitVarInsn(Opcodes.ALOAD, thisId);
					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
					break;
				case "Z":
					mv.visitVarInsn(Opcodes.ALOAD, thisId);
					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
					Label label = new Label();
					mv.visitJumpInsn(Opcodes.IFEQ, label);
					mv.visitLdcInsn(1231);//field = true
					Label end = new Label();
					mv.visitJumpInsn(Opcodes.GOTO, end);
					mv.visitLabel(label);
					mv.visitLdcInsn(1237);//field = false
					mv.visitLabel(end);
					break;
				case "F":
					mv.visitVarInsn(Opcodes.ALOAD, thisId);
					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
					mv.visitInsn(Opcodes.F2I);
					break;
				case "J":
					mv.visitVarInsn(Opcodes.ALOAD, thisId);
					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
					mv.visitInsn(Opcodes.L2I);
					break;
				case "D":
					mv.visitVarInsn(Opcodes.ALOAD, thisId);
					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
					mv.visitInsn(Opcodes.D2I);
					break;
				default:
					mv.visitVarInsn(Opcodes.ALOAD, thisId);
					mv.visitFieldInsn(Opcodes.GETFIELD, className, f.name, f.type.getDescriptor());
					mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, f.type.getClassName(), 
							ALL_FIELDS_METHOD_NAME, "()I", false);
					break;
			}
			
			//add result var  to stack
			mv.visitVarInsn(Opcodes.ILOAD, result);
			//add prime var to stack
			mv.visitVarInsn(Opcodes.ILOAD, prime);
			//multiply prime and result, keep result in stack
			mv.visitInsn(Opcodes.IMUL); 
			//sum field value and (prime * result), keep result in stack
			mv.visitInsn(Opcodes.IADD);
			//save into result var
			mv.visitVarInsn(Opcodes.ISTORE, result);
		}
		
		
		
		mv.visitVarInsn(Opcodes.ILOAD, result);
		
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
		
	}
	
}
