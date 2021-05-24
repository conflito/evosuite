package org.evosuite.coverage.methodcall;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.evosuite.DualRegressionController;
import org.evosuite.Properties;
import org.evosuite.TestGenerationContext;
import org.evosuite.ga.Chromosome;
import org.evosuite.ga.ConstructionFailedException;
import org.evosuite.regression.ObjectDistanceCalculator;
import org.evosuite.testcase.DefaultTestCase;
import org.evosuite.testcase.ExecutableChromosome;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestMutationHistoryEntry;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.execution.TestCaseExecutor;
import org.evosuite.testsuite.TestSuiteFitnessFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MultiTestChromosome extends TestChromosome{

	private static final long serialVersionUID = 8876952790988988737L;

	private static final Logger logger = LoggerFactory.getLogger(MultiTestChromosome.class);

	private MultiTestObserver observer;

	private TestChromosome theSameTestForTheOtherClassLoader;

	private TestChromosome theSameTestForTheSecondClassLoader;

	private double objectDistance;

	private boolean reachedMethods;
	
	private boolean notInFirstRegression;
	private boolean notInSecondRegression;

	public MultiTestChromosome() {
		super();
		this.observer = new MultiTestObserver();
	}

	@Override
	public ExecutionResult executeForFitnessFunction(
			TestSuiteFitnessFunction testSuiteFitnessFunction) {

		ExecutionResult result = null;
		ExecutionResult otherResult = null;
		ExecutionResult secondOtherResult = null;
		boolean compilesInRegression = true;
		boolean compilesInSecondRegression = true;

		if(!Properties.RUN_OTHER_TESTS_BEFORE_REACHING && !reachedMethods) {
			result = TestCaseExecutor.getInstance().execute(this.test);
			this.setLastExecutionResult(result);
			return result;
		}
		
		observer.enable();
		observer.resetObjPool();
		observer.setRegressionFlag(false);
		observer.setSecondRegressionFlag(false);

		TestCaseExecutor.getInstance().newObservers();
		TestCaseExecutor.getInstance().addObserver(observer);

		result = TestCaseExecutor.getInstance().execute(this.test);
		observer.setRegressionFlag(true);
		try {
			otherResult = 
				TestCaseExecutor.getInstance().execute(theSameTestForTheOtherClassLoader.getTestCase());
			compilesInRegression = true;
		}
		catch(Exception e) {
			if(Properties.SHOW_FF)
				logger.warn("No compile in regression");
			compilesInRegression = false;
		}
		observer.setSecondRegressionFlag(true);
		try {
			secondOtherResult =
				TestCaseExecutor.getInstance().execute(theSameTestForTheSecondClassLoader.getTestCase());
			compilesInSecondRegression = true;
		}
		catch(Exception e) {
			if(Properties.SHOW_FF)
				logger.warn("No compile in second regression");
			compilesInSecondRegression = false;
		}
		observer.setRegressionFlag(false);
		observer.setSecondRegressionFlag(false);
		observer.disable();



		this.setLastExecutionResult(result);
		if(compilesInRegression)
			theSameTestForTheOtherClassLoader.setLastExecutionResult(otherResult);
		if(compilesInSecondRegression)
			theSameTestForTheSecondClassLoader.setLastExecutionResult(secondOtherResult);

		double firstObjectDistance;
		
		if(this.notInFirstRegression || !compilesInRegression)
			firstObjectDistance = Double.MAX_VALUE;
		else
			firstObjectDistance= getTestObjectDistance(
				observer.getCurrentObjectMapPool(),
				observer.getCurrentRegressionObjectMapPool());

		double secondObjectDistance;
		
		if(this.notInSecondRegression || !compilesInSecondRegression)
			secondObjectDistance = Double.MAX_VALUE;
		else
			secondObjectDistance = getTestObjectDistance(
				observer.getCurrentObjectMapPool(),
				observer.getCurrentSecondRegressionObjectMapPool());

		this.objectDistance = Math.min(firstObjectDistance, secondObjectDistance);

		return result;
	}

	@Override
	public void copyCachedResults(ExecutableChromosome other) {
		MultiTestChromosome mtc = (MultiTestChromosome) other;
		super.copyCachedResults(other);
		if(theSameTestForTheOtherClassLoader != null)
			theSameTestForTheOtherClassLoader
			.copyCachedResults(mtc.theSameTestForTheOtherClassLoader);
		if(theSameTestForTheSecondClassLoader != null)
			theSameTestForTheSecondClassLoader
			.copyCachedResults(mtc.theSameTestForTheSecondClassLoader);
	}

	@Override
	public Chromosome clone() {
		MultiTestChromosome c = new MultiTestChromosome();
		c.test = this.test.clone();
		c.setFitnessValues(getFitnessValues());
		c.setPreviousFitnessValues(getPreviousFitnessValues());
		c.copyCachedResults(this);
		c.setChanged(isChanged());
		c.setLocalSearchApplied(hasLocalSearchBeenApplied());
		if (Properties.LOCAL_SEARCH_SELECTIVE) {
			for (TestMutationHistoryEntry mutation : mutationHistory) {
				if(test.contains(mutation.getStatement()))
					c.mutationHistory.addMutationEntry(mutation.clone(c.getTestCase()));
			}
		}
		c.setNumberOfMutations(this.getNumberOfMutations());
		c.setNumberOfEvaluations(this.getNumberOfEvaluations());
		c.setKineticEnergy(getKineticEnergy());
		c.setNumCollisions(getNumCollisions());

		c.objectDistance = this.objectDistance;
		c.reachedMethods = this.reachedMethods;
		c.notInFirstRegression = this.notInFirstRegression;
		c.notInSecondRegression = this.notInSecondRegression;
  
		if(theSameTestForTheOtherClassLoader != null)
			c.theSameTestForTheOtherClassLoader = 
			(TestChromosome) theSameTestForTheOtherClassLoader.clone();

		if(theSameTestForTheSecondClassLoader != null)
			c.theSameTestForTheSecondClassLoader =
			(TestChromosome) theSameTestForTheSecondClassLoader.clone();
  
  
		return c;
	}

	protected void updateClassloader() {
		if(super.isChanged()) {
			DualRegressionController.getInstance().setAnalyzingFirstRegression(true);
			
			theSameTestForTheOtherClassLoader = (TestChromosome) super.clone();
			((DefaultTestCase) theSameTestForTheOtherClassLoader.getTestCase())
			.changeClassLoader(TestGenerationContext.getInstance().getRegressionClassLoaderForSUT());

			DualRegressionController.getInstance().setAnalyzingFirstRegression(false);
			DualRegressionController.getInstance().setAnalyzingSecondRegression(true);
			
			theSameTestForTheSecondClassLoader = (TestChromosome) super.clone();
			((DefaultTestCase) theSameTestForTheSecondClassLoader.getTestCase())
			.changeClassLoader(TestGenerationContext.getInstance().getSecondRegressionClassLoaderForSUT());
			
			DualRegressionController.getInstance().setAnalyzingSecondRegression(false);
			
			if(DualRegressionController.getInstance().notFoundInFirstRegression())
				this.notInFirstRegression = true;
			else if(DualRegressionController.getInstance().notFoundInSecondRegression())
				this.notInSecondRegression = true;

		}
	}

	@Override
	public void setTestCase(TestCase testCase) {
		super.setTestCase(testCase);
		updateClassloader();
	}

	@Override
	public void mutate() {
		super.mutate();
		updateClassloader();
	}

	@Override
	public void crossOver(Chromosome other, int position1, int position2)
			throws ConstructionFailedException {
		super.crossOver(other, position1, position2);
		updateClassloader();
	}

	public TestChromosome getTheSameTestForTheOtherClassLoader() {
		return theSameTestForTheOtherClassLoader;
	}

	public TestChromosome getTheSameTestForTheSecondClassLoader() {
		return theSameTestForTheSecondClassLoader;
	}

	public ExecutionResult getLastRegressionExecutionResult() {
		return theSameTestForTheOtherClassLoader.getLastExecutionResult();
	}

	public ExecutionResult getLastSecondRegressionExecutionResult() {
		return theSameTestForTheSecondClassLoader.getLastExecutionResult();
	}

	public double getObjectDistance() {
		return this.objectDistance;
	}

	public boolean reachedMethods() {
		return reachedMethods;
	}

	public void setReachedMethods(boolean reachedMethods) {
		this.reachedMethods = reachedMethods;
	}

	private double getTestObjectDistance(
			List<Map<Integer, Map<String, Map<String, Object>>>> originalMap,
			List<Map<Integer, Map<String, Map<String, Object>>>> regressionMap) {

		ObjectDistanceCalculator distanceCalculator = new ObjectDistanceCalculator();

		double distance = 0.0;

		Map<String, Double> maxClassDistance = new HashMap<String, Double>();

		for (int j = 0; j < originalMap.size(); j++) {
			Map<Integer, Map<String, Map<String, Object>>> map1 = originalMap.get(j);

			if (regressionMap.size() <= j) {
				continue;
			}
			Map<Integer, Map<String, Map<String, Object>>> map2 = regressionMap.get(j);

			for (Map.Entry<Integer, Map<String, Map<String, Object>>> map1_entry : map1.entrySet()) {

				Map<String, Map<String, Object>> map1_values = map1_entry.getValue();
				Map<String, Map<String, Object>> map2_values = map2.get(map1_entry.getKey());

				if (map1_values == null || map2_values == null) {
					continue;
				}
				for (Map.Entry<String, Map<String, Object>> internal_map1_entries : map1_values
						.entrySet()) {

					Map<String, Object> map1_value = internal_map1_entries.getValue();
					Map<String, Object> map2_value = map2_values.get(internal_map1_entries.getKey());
					if (map1_value == null || map2_value == null) {
						continue;
					}

					double objectDistance = distanceCalculator.getObjectMapDistance(map1_value, map2_value);

					if (!maxClassDistance.containsKey(internal_map1_entries.getKey())
							|| (maxClassDistance.get(internal_map1_entries.getKey()) < objectDistance)) {
						maxClassDistance.put(internal_map1_entries.getKey(), objectDistance);
					}
				}
			}
		}

		double tmpDistance = 0.0;

		for (Map.Entry<String, Double> maxEntry : maxClassDistance.entrySet()) {
			tmpDistance += maxEntry.getValue();
		}

		distance += tmpDistance;

		distance += distanceCalculator.getNumDifferentVariables();

		return distance;

	}
}
