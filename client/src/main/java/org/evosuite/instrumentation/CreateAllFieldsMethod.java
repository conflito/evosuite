package org.evosuite.instrumentation;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.evosuite.Properties;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class CreateAllFieldsMethod extends ClassVisitor{
	
	protected static final Logger logger = LoggerFactory.getLogger(CreateAllFieldsMethod.class);

	private static final Pattern ANONYMOUS_MATCHER1 = Pattern.compile(".*\\$\\d+.*$");
	
	private final String className;
	private final String classNameWithDots;
	
	private boolean isInterface = false;
	private boolean isAnonymous = false;
	private boolean isEnum = false;
	
	private Set<String> secondaryMethodsToCover;
	
	public CreateAllFieldsMethod(ClassVisitor visitor, String className, 
			String classNameWithDots) {
		super(Opcodes.ASM5, visitor);

		this.className = className;
		this.classNameWithDots = classNameWithDots;
		
		this.secondaryMethodsToCover = new HashSet<>();
		
		String[] targetMethods = Properties.COVER_METHODS.split(":");
		for(int i = 1; i < targetMethods.length; i++) {
			String method = targetMethods[i];
			secondaryMethodsToCover.add(method);
		}
		
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
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

		String methodFullName = classNameWithDots + "." +  name + desc;
		
		if(secondaryMethodsToCover.contains(methodFullName)) {
			return super.visitMethod(access | Opcodes.ACC_SYNTHETIC, 
					name, desc, signature, exceptions);
		}
		
		return super.visitMethod(access, name, desc, signature, exceptions);
	}
	
	@Override
	public void visitEnd() {
		if(!isInterface && !isAnonymous && !isEnum) {
			createAuxMethods();
			createIgnoreClassMethod();
			createIgnoreFieldMethod();
			createHandlePrimitiveFieldMethod();
			createHandleArrayFieldMethod();
			createAuxAllFieldsMethod();
			createAllFieldsMethod();
		}
		super.visitEnd();
	}
	
	private void createAllFieldsMethod() {
		String[] exceptions = {"java/lang/Exception"};

		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC 
				| Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				Properties.ALL_FIELDS_METHOD_NAME, Properties.ALL_FIELDS_METHOD_DESC,
				null, exceptions);

		mv.visitCode();
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitTypeInsn(Opcodes.NEW, "java/util/HashSet");
		mv.visitInsn(Opcodes.DUP);
		mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/HashSet", "<init>", 
				"()V", false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, Properties.ALL_FIELDS_METHOD_NAME + "Aux", "(Ljava/lang/Object;Ljava/util/Set;)J", false);
		
		mv.visitInsn(Opcodes.LRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
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
				"handleArrayField", "(Ljava/lang/Object;Ljava/util/Set;)J", null, exceptions);
		
		Label _14 = new Label();
		Label _34 = new Label();
		
		mv.visitCode();
		
		mv.visitInsn(Opcodes.LCONST_0);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/reflect/Array", "getLength", "(Ljava/lang/Object;)I", false);
		mv.visitVarInsn(Opcodes.ISTORE, 4);
		
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitVarInsn(Opcodes.ISTORE, 5);
		mv.visitJumpInsn(Opcodes.GOTO, _34);
		mv.visitLabel(_14);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ILOAD, 5);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/reflect/Array", "get", "(Ljava/lang/Object;I)Ljava/lang/Object;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 6);
		mv.visitVarInsn(Opcodes.LLOAD, 2);
		mv.visitVarInsn(Opcodes.ALOAD, 6);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, Properties.ALL_FIELDS_METHOD_NAME + "Aux", 
				"(Ljava/lang/Object;Ljava/util/Set;)J", false);
		mv.visitInsn(Opcodes.LADD);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitIincInsn(5, 1);
		mv.visitLabel(_34);
		mv.visitVarInsn(Opcodes.ILOAD, 5);
		mv.visitVarInsn(Opcodes.ILOAD, 4);
		mv.visitJumpInsn(Opcodes.IF_ICMPLT, _14);
		mv.visitVarInsn(Opcodes.LLOAD, 2);

		mv.visitInsn(Opcodes.LRETURN);
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
	}
	
	private void createIgnoreClassMethod() {
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				"isClassToIgnore", "(Ljava/lang/Class;)Z", null, null);
		mv.visitCode();
		
		Label _43 = new Label();
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "getCanonicalName", "()Ljava/lang/String;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitJumpInsn(Opcodes.IFNULL, _43);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("java.util.concurrent.ExecutorService");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("java.util.concurrent.Callable");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("java.util.concurrent.ThreadPoolExecutor");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("java.net.FileNameMap");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("java.net.Cookie");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("javax.net.ssl.SSLSocketFactory");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("com.squareup.okhttp.internal.http.HttpEngine");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);		
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitLdcInsn("java.io");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "startsWith", "(Ljava/lang/String;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _43);		
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitLabel(_43);
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void createIgnoreFieldMethod() {
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				"isFieldToIgnore", "(Ljava/lang/reflect/Field;)Z", null, null);
		mv.visitCode();
		
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getName", "()Ljava/lang/String;", false);
		mv.visitLdcInsn("mockitoInterceptor");
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "equals", "(Ljava/lang/Object;)Z", false);
		mv.visitInsn(Opcodes.IRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
	private void createAuxAllFieldsMethod() {
		String[] exceptions = {"java/lang/Exception"};
		
		MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE 
					| Opcodes.ACC_STATIC | Opcodes.ACC_SYNTHETIC,
				Properties.ALL_FIELDS_METHOD_NAME + "Aux", "(Ljava/lang/Object;Ljava/util/Set;)J",
				null, exceptions);
		
		Label _67 = new Label();
		Label _68 = new Label();
		Label _92 = new Label();
		Label _166 = new Label();
		Label _199 = new Label();
		Label _212 = new Label();
		Label _215 = new Label();
		Label _225 = new Label();
		Label _246 = new Label();
		Label _248 = new Label();
		
		mv.visitCode();

		mv.visitInsn(Opcodes.LCONST_1);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitVarInsn(Opcodes.BIPUSH, 17);
		mv.visitVarInsn(Opcodes.ISTORE, 4);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		
		mv.visitJumpInsn(Opcodes.IFNULL, _225);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set", "contains", 
				"(Ljava/lang/Object;)Z", true);
		mv.visitJumpInsn(Opcodes.IFNE, _225);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set", "add", 
				"(Ljava/lang/Object;)Z", true);
		mv.visitInsn(Opcodes.POP);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isClassToIgnore", "(Ljava/lang/Class;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _248);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "getCanonicalName", "()Ljava/lang/String;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 5);
		mv.visitVarInsn(Opcodes.ALOAD, 5);
		mv.visitJumpInsn(Opcodes.IFNULL, _67);
		mv.visitLdcInsn(17l);
		mv.visitVarInsn(Opcodes.LLOAD, 2);
		mv.visitInsn(Opcodes.LMUL);
		mv.visitVarInsn(Opcodes.ALOAD, 5);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/String", "hashCode", "()I", false);
		mv.visitInsn(Opcodes.I2L);
		mv.visitInsn(Opcodes.LADD);
		mv.visitJumpInsn(Opcodes.GOTO, _68);
		mv.visitLabel(_67);
		mv.visitInsn(Opcodes.LCONST_1);
		mv.visitLabel(_68);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Object", "getClass", "()Ljava/lang/Class;", false);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "getDeclaredFields", "()[Ljava/lang/reflect/Field;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 6);
		mv.visitVarInsn(Opcodes.ALOAD, 6);
		mv.visitInsn(Opcodes.DUP);
		mv.visitVarInsn(Opcodes.ASTORE, 10);
		mv.visitInsn(Opcodes.ARRAYLENGTH);
		mv.visitVarInsn(Opcodes.ISTORE, 9);
		mv.visitInsn(Opcodes.ICONST_0);
		mv.visitVarInsn(Opcodes.ISTORE, 8);
		mv.visitJumpInsn(Opcodes.GOTO, _215);
		mv.visitLabel(_92);
		mv.visitVarInsn(Opcodes.ALOAD, 10);
		mv.visitVarInsn(Opcodes.ILOAD, 8);
		mv.visitInsn(Opcodes.AALOAD);
		mv.visitVarInsn(Opcodes.ASTORE, 7);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getModifiers", "()I", false);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				"java/lang/reflect/Modifier", "isStatic", "(I)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _212);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "isSynthetic", "()Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _212);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "isFieldToIgnore", "(Ljava/lang/reflect/Field;)Z", false);
		mv.visitJumpInsn(Opcodes.IFNE, _212);
		mv.visitVarInsn(Opcodes.ALOAD, 7);		
		mv.visitInsn(Opcodes.ICONST_1);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "setAccessible", "(Z)V", false);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "getType", "()Ljava/lang/Class;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 11);
		mv.visitVarInsn(Opcodes.ALOAD, 11);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "isPrimitive", "()Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _166);
		mv.visitLdcInsn(17l);
		mv.visitVarInsn(Opcodes.LLOAD, 2);
		mv.visitInsn(Opcodes.LMUL);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitVarInsn(Opcodes.ALOAD, 11);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "handlePrimitiveField", 
				"(Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Class;)I", false);
		mv.visitInsn(Opcodes.I2L);
		mv.visitInsn(Opcodes.LADD);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitJumpInsn(Opcodes.GOTO, _212);
		mv.visitLabel(_166);
		mv.visitVarInsn(Opcodes.ALOAD, 11);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/Class", "isArray", "()Z", false);
		mv.visitJumpInsn(Opcodes.IFEQ, _199);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
		mv.visitVarInsn(Opcodes.ASTORE, 12);
		mv.visitVarInsn(Opcodes.ALOAD, 12);
		mv.visitJumpInsn(Opcodes.IFNULL, _212);
		mv.visitVarInsn(Opcodes.LLOAD, 2);
		mv.visitVarInsn(Opcodes.ALOAD, 12);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, "handleArrayField", "(Ljava/lang/Object;Ljava/util/Set;)J", false);
		mv.visitInsn(Opcodes.LADD);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitJumpInsn(Opcodes.GOTO, _212);
		mv.visitLabel(_199);
		mv.visitVarInsn(Opcodes.LLOAD, 2);
		mv.visitVarInsn(Opcodes.ALOAD, 7);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, 
				"java/lang/reflect/Field", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitMethodInsn(Opcodes.INVOKESTATIC, 
				className, Properties.ALL_FIELDS_METHOD_NAME + "Aux",
				"(Ljava/lang/Object;Ljava/util/Set;)J", false);
		mv.visitInsn(Opcodes.LADD);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitLabel(_212);
		mv.visitIincInsn(8, 1);
		mv.visitLabel(_215);
		mv.visitVarInsn(Opcodes.ILOAD, 8);
		mv.visitVarInsn(Opcodes.ILOAD, 9);
		mv.visitJumpInsn(Opcodes.IF_ICMPLT, _92);
		mv.visitJumpInsn(Opcodes.GOTO, _248);
		mv.visitLabel(_225);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitJumpInsn(Opcodes.IFNONNULL, _246);
		mv.visitVarInsn(Opcodes.ALOAD, 1);
		mv.visitVarInsn(Opcodes.ALOAD, 0);
		mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, "java/util/Set", "contains", 
				"(Ljava/lang/Object;)Z", true);
		mv.visitJumpInsn(Opcodes.IFNE, _246);
		mv.visitLdcInsn(17l);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitJumpInsn(Opcodes.GOTO, _248);
		mv.visitLabel(_246);
		mv.visitInsn(Opcodes.LCONST_0);
		mv.visitVarInsn(Opcodes.LSTORE, 2);
		mv.visitLabel(_248);
		mv.visitVarInsn(Opcodes.LLOAD, 2);
		
		mv.visitInsn(Opcodes.LRETURN);
		mv.visitMaxs(0, 0);
		mv.visitEnd();
	}
	
}
