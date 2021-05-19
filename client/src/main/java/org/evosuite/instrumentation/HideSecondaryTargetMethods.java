package org.evosuite.instrumentation;

import java.util.HashSet;
import java.util.Set;

import org.evosuite.Properties;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class HideSecondaryTargetMethods extends ClassVisitor{

	private final String classNameWithDots;
	private Set<String> secondaryMethodsToCover;
	
	public HideSecondaryTargetMethods(ClassVisitor visitor,	
			String classNameWithDots) {
		super(Opcodes.ASM5, visitor);
		
		this.classNameWithDots = classNameWithDots;
		this.secondaryMethodsToCover = new HashSet<>();
		String[] targetMethods = Properties.COVER_METHODS.split(":");
		for(int i = 1; i < targetMethods.length; i++) {
			String method = targetMethods[i];
			secondaryMethodsToCover.add(method);
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
}
