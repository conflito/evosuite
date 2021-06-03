package org.evosuite.coverage.methodcall;

import java.util.List;
import org.evosuite.Properties;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.TestFitnessFunction;
import org.evosuite.testcase.execution.ExecutionResult;

public class MethodCallTestFitness extends TestFitnessFunction {

	private static final long serialVersionUID = 4257172781114573825L;

	private List<MethodCallGoal> goals;

	public MethodCallTestFitness(List<MethodCallGoal> goals) {
		super();
		this.goals = goals;
	}

	@Override
	public double getFitness(TestChromosome individual, ExecutionResult result) {
		double fitness = 1.0;

		if(result == null)
			return fitness;
		
		if(!(individual instanceof MultiTestChromosome)) {
			return individual.getFitness();
		}

		MultiTestChromosome mtc = (MultiTestChromosome) individual;
		
		double distanceToMethods = goals.stream()
				.mapToDouble(g -> g.distanceToGoal(result))
				.sum();

		if(!Properties.RUN_OTHER_TESTS_BEFORE_REACHING && distanceToMethods == 0.0) {
			mtc.setReachedMethods(true);
		}
		
		if(!Properties.RUN_OTHER_TESTS_BEFORE_REACHING && !mtc.reachedMethods()) {
			fitness = normalize(distanceToMethods + 1);
		}
		else {
			double objectDistance = (1 / (1 + mtc.getObjectDistance()));
			if(objectDistance <= Properties.DISTANCE_THRESHOLD) {
				objectDistance = 0.0;
			}
			fitness = normalize(distanceToMethods + objectDistance);
			
			if(Properties.SHOW_FF)
				logger.warn("Method distance: " + distanceToMethods + 
						" | Object distance: " + mtc.getObjectDistance());
		}
		
		updateIndividual(this, individual, fitness);
		
		return fitness;
	}

	@Override
	public int compareTo(TestFitnessFunction other) {
		return 0;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((goals == null) ? 0 : goals.hashCode());
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
		if (goals == null) {
			if (other.goals != null)
				return false;
		} else if (!goals.equals(other.goals))
			return false;
		return true;
	}

	@Override
	public String getTargetClass() {
		return goals.isEmpty()?null:goals.get(0).getClassName();
	}

	@Override
	public String getTargetMethod() {
		return goals.isEmpty()?null:goals.get(0).getMethodName();
	}

}
