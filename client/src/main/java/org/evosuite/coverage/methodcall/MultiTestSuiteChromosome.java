package org.evosuite.coverage.methodcall;

import java.util.Collection;

import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;

public class MultiTestSuiteChromosome extends TestSuiteChromosome {

	private static final long serialVersionUID = -48615528088549654L;

	public MultiTestSuiteChromosome() {
		super();
	}

	public MultiTestSuiteChromosome(ChromosomeFactory<TestChromosome> testChromosomeFactory) {
		this.testChromosomeFactory = testChromosomeFactory;
	}

	protected MultiTestSuiteChromosome(MultiTestSuiteChromosome source) {
		super(source);
	}

	@Override
	public void addTest(TestChromosome test) {
		if(test instanceof MultiTestChromosome) {
			tests.add(test);
		}
		else {
			MultiTestChromosome c = new MultiTestChromosome();
			c.setTestCase(test.getTestCase());
			tests.add(c);
		}
		this.setChanged(true);
	}

	@Override
	public void addTests(Collection<TestChromosome> tests) {
		for (TestChromosome test : tests) {
			test.setChanged(true);
			addTest(test);
		}
	}
}
