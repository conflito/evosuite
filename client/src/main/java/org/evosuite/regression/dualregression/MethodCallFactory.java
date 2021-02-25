package org.evosuite.regression.dualregression;

import java.util.ArrayList;
import java.util.List;

import org.evosuite.Properties;
import org.evosuite.testsuite.AbstractFitnessFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MethodCallFactory extends AbstractFitnessFactory<MethodCallTestFitness>{

	protected static final Logger logger = LoggerFactory.getLogger(MethodCallFactory.class);

	@Override
	public List<MethodCallTestFitness> getCoverageGoals() {
		List<MethodCallTestFitness> goals = new ArrayList<>();

		String targetMethodList = Properties.COVER_METHODS;
		if(!targetMethodList.equals("")) {
			String[] methods = targetMethodList.split(":");
			boolean shouldAppearInTest = true;
			for(String method: methods) {
				int lastDotIndex = method.lastIndexOf('.');
				String className = method.substring(0, lastDotIndex);
				goals.add(new MethodCallTestFitness(className, method, shouldAppearInTest));
				shouldAppearInTest = false;
			}
		}

		return goals;
	}

}
