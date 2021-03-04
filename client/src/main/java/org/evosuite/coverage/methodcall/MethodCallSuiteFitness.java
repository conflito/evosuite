package org.evosuite.coverage.methodcall;

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

		int maxCovered = Integer.MIN_VALUE;
		
		for(ExecutableChromosome c: suite.getTestChromosomes()) {
			MultiTestChromosome mtc = (MultiTestChromosome) c;
//			mtc.executeForFitnessFunction(this);
			ExecutionResult result = mtc.getLastExecutionResult();
			int covers = 0;
			for(MethodCallTestFitness goal: allMethodCalls) {
				if(goal.isCovered(mtc, result)) {
					covers++;
				}
			}
			if(covers > maxCovered)
				maxCovered = covers;
		}
		
//		for(ExecutionResult result: results) {
//			int covers = 0;
//			for(MethodCallTestFitness goal: allMethodCalls) {
//				if(goal.isCovered(result)) {
//					covers++;
//				}
//			}
//			if(covers > maxCovered)
//				maxCovered = covers;
//		}
		
		fitness = allMethodCalls.size() - maxCovered;
		
		if(results.size() > 1)
			fitness = fitness / results.size();
		
		updateIndividual(this, suite, fitness);
		
		suite.setNumOfCoveredGoals(this, maxCovered);
		if(!allMethodCalls.isEmpty())
			suite.setCoverage(this, (double) maxCovered / allMethodCalls.size());
		else
			suite.setCoverage(this, 1.0);

		return fitness;
	}

}
