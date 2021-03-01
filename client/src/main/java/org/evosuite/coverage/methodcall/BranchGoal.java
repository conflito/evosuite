package org.evosuite.coverage.methodcall;

import java.io.Serializable;

import org.evosuite.coverage.ControlFlowDistance;
import org.evosuite.coverage.branch.BranchCoverageGoal;
import org.evosuite.coverage.branch.BranchCoverageTestFitness;
import org.evosuite.testcase.execution.ExecutionResult;

public class BranchGoal implements Serializable{

	private static final long serialVersionUID = -5626940111601714005L;
	
	private final BranchCoverageGoal goal;

	public BranchGoal(BranchCoverageGoal goal) {
		super();
		this.goal = goal;
	}
	
	public BranchGoal(BranchCoverageTestFitness b) {
		super();
		this.goal = b.getBranchGoal();
	}
	
	public double distanceToGoal(ExecutionResult result) {
		ControlFlowDistance cfd = goal.getDistance(result);
		return cfd.getResultingBranchFitness();
	}
}
