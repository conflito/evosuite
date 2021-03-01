package org.evosuite.coverage.methodcall;

import java.io.Serializable;

import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.EntityWithParametersStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodCallGoal implements Serializable{

	protected static final Logger logger = LoggerFactory.getLogger(MethodCallGoal.class);
	
	private static final long serialVersionUID = 1622074548386830377L;
	
	private LineGoal lineGoal;
	
	private boolean shouldAppearInTest;
	
	public MethodCallGoal(String className, String methodName, Integer line, 
			boolean shouldAppearInTest) {
		this.lineGoal = new LineGoal(className, methodName, line);
		this.shouldAppearInTest = shouldAppearInTest;
	}
	
	public String getClassName() {
		return lineGoal.getClassName();
	}
	
	public String getMethodName() {
		return lineGoal.getMethodName();
	}
	
	public boolean shouldAppearInTestStatements() {
		return shouldAppearInTest;
	}
	
	public double distanceToGoal(ExecutionResult result) {
		return lineGoal.distanceToGoal(result);
	}
	
	public boolean appearsInTestStatements(ExecutionResult executionResult) {
		for (Statement stmt : executionResult.test) {
			if (stmt instanceof MethodStatement) {
				EntityWithParametersStatement ps = (EntityWithParametersStatement)stmt;
				String className  = ps.getDeclaringClassName();
				String methodName = ps.getMethodName() + ps.getDescriptor();

				if(this.lineGoal.getClassName().equals(className) &&
						this.lineGoal.getMethodName().equals(methodName)) {
					return true;
				}
			}
		}

		return false;
	}	

}
