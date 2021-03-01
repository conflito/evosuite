package org.evosuite.coverage.methodcall;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.evosuite.TestGenerationContext;
import org.evosuite.coverage.branch.BranchCoverageFactory;
import org.evosuite.graphs.cfg.BytecodeInstruction;
import org.evosuite.graphs.cfg.BytecodeInstructionPool;
import org.evosuite.graphs.cfg.ControlDependency;
import org.evosuite.testcase.execution.ExecutionResult;

public class LineGoal implements Serializable{

	private static final long serialVersionUID = 2458822264150133163L;

	private final String className;
	private final String methodName;
	private final Integer line;
	
	private List<BranchGoal> branchGoals;

	protected transient BytecodeInstruction goalInstruction;
	
	public LineGoal(String className, String methodName, Integer line) {
		super();
		this.className = className;
		this.methodName = methodName;
		this.line = line;
		this.branchGoals = new ArrayList<>();
		setupDependencies();
	}
	
	private void setupDependencies() {
		goalInstruction = BytecodeInstructionPool
				.getInstance(TestGenerationContext.getInstance().getClassLoaderForSUT())
				.getFirstInstructionAtLineNumber(className, methodName, line);
		
		if(goalInstruction == null)
			return;
		
		Set<ControlDependency> cds = goalInstruction.getControlDependencies();
		
		for (ControlDependency cd : cds) {
			branchGoals.add(new BranchGoal(
					BranchCoverageFactory.createBranchCoverageTestFitness(cd)));
		}
		
		if (goalInstruction.isRootBranchDependent())
			branchGoals.add(new BranchGoal(
					BranchCoverageFactory.createRootBranchTestFitness(goalInstruction)));
	}
	
	public double distanceToGoal(ExecutionResult result) {
		double distance = 1.0;
		
		if (result.getTrace().getCoveredLines(className).contains(line)) {
			distance = 0.0;
		}
		else {
			double r = Double.MAX_VALUE;
			
			for (BranchGoal goal : branchGoals) {
				double goalDistance = goal.distanceToGoal(result);
				if(goalDistance < r)
					r = goalDistance;
			}
			
			distance = r;
		}
		
		return distance;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}
	
	
}
