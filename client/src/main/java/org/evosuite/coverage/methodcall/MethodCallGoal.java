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
	
	public MethodCallGoal(String className, String methodName, Integer line) {
		this.lineGoal = new LineGoal(className, methodName, line);
	}
	
	public String getClassName() {
		return lineGoal.getClassName();
	}
	
	public String getMethodName() {
		return lineGoal.getMethodName();
	}
	
	public double distanceToGoal(ExecutionResult result) {
		return lineGoal.distanceToGoal(result);
	}

}
