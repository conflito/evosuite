package org.evosuite.regression.dualregression;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.ExecutionTracer;
import org.evosuite.testsuite.AbstractTestSuiteChromosome;
import org.evosuite.testsuite.TestSuiteFitnessFunction;

public class MethodCallSuiteFitness extends TestSuiteFitnessFunction{

	private static final long serialVersionUID = -915126696615399117L;

	private final Set<MethodCallTestFitness> allMethodCalls = new HashSet<>();

	public MethodCallSuiteFitness() {
		allMethodCalls.addAll(new MethodCallFactory().getCoverageGoals());

		ExecutionTracer.enableTraceCalls();
	}

	@Override
	public double getFitness(
			AbstractTestSuiteChromosome<? extends ExecutableChromosome> suite) {
		double fitness = 1.0;

		List<ExecutionResult> results = runTestSuite(suite);
		Set<MethodCallTestFitness> coveredMethodCalls = new HashSet<>();

		for(MethodCallTestFitness goal: allMethodCalls) {
			for(ExecutionResult result : results) {
				if(goal.isCovered(result)) {
					coveredMethodCalls.add(goal);
					break;
				}
			}
		}

		fitness = allMethodCalls.size() - coveredMethodCalls.size();

		for (ExecutionResult result : results) {
			if (result.hasTimeout() || result.hasTestException()) {
				fitness = allMethodCalls.size();
				break;
			}
		}

		updateIndividual(this, suite, fitness);

		suite.setNumOfCoveredGoals(this, coveredMethodCalls.size());
		if(!allMethodCalls.isEmpty()) {
			 suite.setCoverage(this, 
					 (double) coveredMethodCalls.size() / (double) allMethodCalls.size());
		}
		else {
			suite.setCoverage(this, 1.0);
		}

		return fitness;
	}

}
