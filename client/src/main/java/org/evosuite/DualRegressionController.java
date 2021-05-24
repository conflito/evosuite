package org.evosuite;

public class DualRegressionController {

	private static DualRegressionController instance;
	
	private boolean notFoundInFirstRegression;
	private boolean notFoundInSecondRegression;
	
	private boolean analyzingFirstRegression;
	private boolean analyzingSecondRegression;
	
	private DualRegressionController(){
		
	}
	
	public boolean notFoundInFirstRegression() {
		return notFoundInFirstRegression;
	}
	
	public boolean notFoundInSecondRegression() {
		return notFoundInSecondRegression;
	}
	
	public void setNotFound() {
		if(analyzingFirstRegression)
			notFoundInFirstRegression = true;
		if(analyzingSecondRegression)
			notFoundInSecondRegression = true;
	}
	
	public void setAnalyzingFirstRegression(boolean b) {
		analyzingFirstRegression = b;
	}
	
	public void setAnalyzingSecondRegression(boolean b) {
		analyzingSecondRegression = b;
	}
	
	public static DualRegressionController getInstance() {
		if(instance == null)
			instance = new DualRegressionController();
		return instance;
	}
	
	
}
