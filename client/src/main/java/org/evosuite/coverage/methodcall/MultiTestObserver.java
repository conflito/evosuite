package org.evosuite.coverage.methodcall;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.evosuite.regression.ObjectFields;
import org.evosuite.testcase.execution.ExecutionObserver;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.Scope;
import org.evosuite.testcase.statements.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiTestObserver extends ExecutionObserver implements Serializable{

	private static final long serialVersionUID = 2006723080937926169L;

	private List<Map<Integer, Map<String, Map<String, Object>>>> currentObjectMapPool = new ArrayList<>();
	private List<Map<Integer, Map<String, Map<String, Object>>>> currentRegressionObjectMapPool = new ArrayList<>();
	private List<Map<Integer, Map<String, Map<String, Object>>>> currentSecondRegressionObjectMapPool = new ArrayList<>();
	
	private boolean isRegression;
	private boolean isSecondRegression;
	private boolean isDisabled;
	
	private static final Logger logger = LoggerFactory.getLogger(MultiTestObserver.class);

	public MultiTestObserver() {
		super();
		this.isRegression = false;
		this.isDisabled = true;
	}

	public List<Map<Integer, Map<String, Map<String, Object>>>> getCurrentObjectMapPool() {
		return currentObjectMapPool;
	}

	public List<Map<Integer, Map<String, Map<String, Object>>>> getCurrentRegressionObjectMapPool() {
		return currentRegressionObjectMapPool;
	}

	public List<Map<Integer, Map<String, Map<String, Object>>>> getCurrentSecondRegressionObjectMapPool() {
		return currentSecondRegressionObjectMapPool;
	}

	public void enable() {
		isDisabled = false;
	}

	public void disable() {
		isDisabled = true;
	}

	public void setRegressionFlag(boolean isRegression) {
		this.isRegression = isRegression;
	}
	
	public void setSecondRegressionFlag(boolean isSecondRegression) {
		this.isSecondRegression = isSecondRegression;
	}

	public void resetObjPool() {
		currentObjectMapPool = new ArrayList<>();
		currentRegressionObjectMapPool = new ArrayList<>();
		currentSecondRegressionObjectMapPool = new ArrayList<>();
	}

	@Override
	public void output(int position, String output) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeStatement(Statement statement, Scope scope) {
		// TODO Auto-generated method stub
	}

	@Override
	public void afterStatement(Statement statement, Scope scope, Throwable exception) {
		if (isDisabled) {
			return;
		}
		ObjectFields scopeObjectFields = new ObjectFields(scope);
		if (isRegression) {
			if(isSecondRegression)
				currentSecondRegressionObjectMapPool.add(scopeObjectFields.getObjectVariables());
			else
				currentRegressionObjectMapPool.add(scopeObjectFields.getObjectVariables());
		} 
		else {
			currentObjectMapPool.add(scopeObjectFields.getObjectVariables());
		}
	}

	@Override
	public void testExecutionFinished(ExecutionResult r, Scope s) {
		if (isDisabled) {
			return;
		}
		ObjectFields scopeObjectFields = new ObjectFields(s);
		if (isRegression) {
			if(isSecondRegression)
				currentSecondRegressionObjectMapPool.add(scopeObjectFields.getObjectVariables());
			else
				currentRegressionObjectMapPool.add(scopeObjectFields.getObjectVariables());
		} 
		else {
			currentObjectMapPool.add(scopeObjectFields.getObjectVariables());
		}

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

}
