package org.evosuite.regression.dualregression;

import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.EntityWithParametersStatement;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;

public class MethodCallTestFitness extends TestFitnessFunction {

	private static final long serialVersionUID = 4257172781114573825L;

	private String className;
	private String methodName;
	
	private boolean shouldAppearInTest;

	public MethodCallTestFitness(String className, String methodName, boolean shouldAppearInTest) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.shouldAppearInTest = shouldAppearInTest;
	}

	@Override
	public double getFitness(TestChromosome individual, ExecutionResult result) {
		double fitness = 1.0;
		
		boolean appearsInTestStatements = checkIfCallInTestStatements(result);
		
		if(!shouldAppearInTest && appearsInTestStatements) {
			updateIndividual(this, individual, fitness);
			return fitness;
		}

		for(String method: result.getTrace().getCoveredMethods()) {
			if(methodName.equals(method)) {
				fitness = 0.0;
				break;
			}
		}

		updateIndividual(this, individual, fitness);
		return fitness;
	}
	
	private boolean checkIfCallInTestStatements(ExecutionResult executionResult) {
		boolean result = false;
		
		for (Statement stmt : executionResult.test) {
			if (stmt instanceof MethodStatement) {
				EntityWithParametersStatement ps = (EntityWithParametersStatement)stmt;
				String className  = ps.getDeclaringClassName();
				String methodName = ps.getMethodName() + ps.getDescriptor();
				if(this.className.equals(className) 
						&& this.methodName.equals(methodName)) {
					result = true;
				}
			}
		}
		
		return result;
	}

	@Override
	public int compareTo(TestFitnessFunction other) {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((className == null) ? 0 : className.hashCode());
		result = prime * result + ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MethodCallTestFitness))
			return false;
		MethodCallTestFitness other = (MethodCallTestFitness) obj;
		if (className == null) {
			if (other.className != null)
				return false;
		} else if (!className.equals(other.className))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}

	@Override
	public String getTargetClass() {
		return className;
	}

	@Override
	public String getTargetMethod() {
		return methodName;
	}

}
