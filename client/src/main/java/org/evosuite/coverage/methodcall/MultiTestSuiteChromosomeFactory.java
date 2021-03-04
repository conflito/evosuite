package org.evosuite.coverage.methodcall;

import org.evosuite.Properties;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.evosuite.testsuite.factories.TestSuiteChromosomeFactory;
import org.evosuite.utils.Randomness;

public class MultiTestSuiteChromosomeFactory extends TestSuiteChromosomeFactory{

	private static final long serialVersionUID = 8087673205617794577L;

	@Override
	public TestSuiteChromosome getChromosome() {
		MultiTestSuiteChromosome suite = new MultiTestSuiteChromosome();
		
		suite.clearTests();
		
		int numTests = Randomness.nextInt(Properties.MIN_INITIAL_TESTS,
		        Properties.MAX_INITIAL_TESTS + 1);
		for (int i = 0; i < numTests; i++) {
			TestChromosome test = testChromosomeFactory.getChromosome();
			suite.addTest(test);
		}
		
		return suite;
	}
}
