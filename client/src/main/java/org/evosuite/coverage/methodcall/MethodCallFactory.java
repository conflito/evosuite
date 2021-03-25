package org.evosuite.coverage.methodcall;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.instrumentation.LinePool;
import org.evosuite.testsuite.AbstractFitnessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodCallFactory extends AbstractFitnessFactory<MethodCallTestFitness>{

	protected static final Logger logger = LoggerFactory.getLogger(MethodCallFactory.class);

	@Override
	public List<MethodCallTestFitness> getCoverageGoals() {
		List<MethodCallTestFitness> goals = new ArrayList<>();
		
		String targetMethodList = Properties.COVER_METHODS;
		String regressionCP = Properties.REGRESSIONCP;
		String secondRegressionCP = Properties.SECOND_REGRESSIONCP;
		
		if(!targetMethodList.equals("") && !regressionCP.equals("") &&
				!secondRegressionCP.equals("")) {
			String[] methods = targetMethodList.split(":");
			boolean shouldAppearInTest = true;
			
			List<MethodCallGoal> callGoals = new ArrayList<>();
			
			for(String method: methods) {
				String className = Properties.getClassNameFromMethodFullName(method);
				String methodName = Properties.getMethodNameFromMethodFullName(method);
				
				Set<Integer> lines = LinePool.getLines(className, methodName);
				Optional<Integer> randomLine = lines.stream().findAny();
				if(randomLine.isPresent()) {
					int line = randomLine.get().intValue();
					MethodCallGoal methodCallGoal = 
							new MethodCallGoal(className, methodName, 
									line, shouldAppearInTest);
					shouldAppearInTest = false;
					
					callGoals.add(methodCallGoal);
				}
				else {
					logger.error("Failed to find a line for method " + 
							methodName + " of class " + className);
				}
				
			}
			goals.add(new MethodCallTestFitness(callGoals));
			
		}

		return goals;
	}

}
