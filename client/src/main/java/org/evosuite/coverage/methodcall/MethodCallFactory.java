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
		String alteredLinesList = Properties.COVER_LINES;
		String regressionCP = Properties.REGRESSIONCP;
		String secondRegressionCP = Properties.SECOND_REGRESSIONCP;
		
		if(!targetMethodList.equals("") &&  
				!regressionCP.equals("") &&
				!secondRegressionCP.equals("")) {
			
			boolean alteredLinesInserted = !alteredLinesList.equals("");
			
			String[] methods = targetMethodList.split(":");
			String[] alteredLines = alteredLinesList.split(":");
			
			if(methods.length <= 0) {
				logger.error("Invalid methods to cover");
				return goals;
			}
			
			if(alteredLinesInserted && alteredLines.length <= 0) {
				logger.error("Invalid altered lines to cover");
				return goals;
			}
			
			if(alteredLinesInserted && methods.length != alteredLines.length) {
				logger.error("Altered methods and altered lines have different sizes");
				return goals;
			}
			
			List<MethodCallGoal> callGoals = new ArrayList<>();
			
			for (int i = 0; i < methods.length; i++) {
				String method = methods[i];
				
				String className = Properties.getClassNameFromMethodFullName(method);
				String methodName = Properties.getMethodNameFromMethodFullName(method);
				
				Set<Integer> lines = LinePool.getLines(className, methodName);
				Optional<Integer> randomLine = lines.stream().findAny();
				if(randomLine.isPresent()) {
					int line = randomLine.get().intValue();
					MethodCallGoal methodCallGoal = 
							new MethodCallGoal(className, methodName, line);
					
					if(alteredLinesInserted) {
						String methodAlteredLines = alteredLines[i];
						processAlteredLines(methodAlteredLines, methodCallGoal);
					}

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
	
	private void processAlteredLines(String methodAlteredLines, MethodCallGoal methodCallGoal) {
		if(!methodAlteredLines.equals("")) {
			for(String alteredLine: methodAlteredLines.split(";")) {
				try {
					int iLine = Integer.parseInt(alteredLine);
					String className = methodCallGoal.getClassName();
					String methodName = methodCallGoal.getMethodName();

					if(iLine != -1) {
						if(LinePool.getLines(className, methodName).contains(iLine))
							methodCallGoal.addAlteredLineGoal(iLine);
						else
							logger.warn("Method " + methodName + " of class " + className
									+ " doesn't contain the line " + iLine);
					}
				}
				catch(NumberFormatException e) {
					logger.error("Invalid format for altered lines to cover");
				}
			}
		}
	}

}
