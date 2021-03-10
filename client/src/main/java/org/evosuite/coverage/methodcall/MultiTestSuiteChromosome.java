package org.evosuite.coverage.methodcall;

import java.util.Collection;

import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testsuite.TestSuiteChromosome;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultiTestSuiteChromosome extends TestSuiteChromosome {

	private static final long serialVersionUID = -48615528088549654L;
	
	private static final Logger logger = LoggerFactory.getLogger(MultiTestSuiteChromosome.class);

	public MultiTestSuiteChromosome() {
		this(new MultiTestChromosomeFactory());
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
