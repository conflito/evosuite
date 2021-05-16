package org.evosuite.coverage.methodcall;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.evosuite.testcase.execution.ExecutionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodCallGoal implements Serializable{

	protected static final Logger logger = LoggerFactory.getLogger(MethodCallGoal.class);
	
	private static final long serialVersionUID = 1622074548386830377L;
	
	private LineGoal lineGoal;
	
	private List<LineGoal> alteredLinesGoals;
	
	public MethodCallGoal(String className, String methodName, int line) {
		this.lineGoal = new LineGoal(className, methodName, line);
		alteredLinesGoals = new ArrayList<>();
	}
	
	public void addAlteredLineGoal(int line) {
		alteredLinesGoals.add(new LineGoal(getClassName(), getMethodName(), line));
	}
	
	public String getClassName() {
		return lineGoal.getClassName();
	}
	
	public String getMethodName() {
		return lineGoal.getMethodName();
	}
	
	public double distanceToGoal(ExecutionResult result) {
		return lineGoal.distanceToGoal(result) + distanceToAlteredLines(result);
	}
	
	private double distanceToAlteredLines(ExecutionResult result) {
		return alteredLinesGoals.stream()
					.mapToDouble(lg -> lg.distanceToGoal(result))
					.sum();
	}

}
