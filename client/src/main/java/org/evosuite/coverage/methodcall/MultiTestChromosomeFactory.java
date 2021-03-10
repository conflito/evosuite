package org.evosuite.coverage.methodcall;

import org.evosuite.ga.ChromosomeFactory;
import org.evosuite.testcase.TestChromosome;
import org.evosuite.testcase.factories.RandomLengthTestFactory;

public class MultiTestChromosomeFactory implements ChromosomeFactory<TestChromosome>{

	private static final long serialVersionUID = 2515459460372154770L;
	
	private RandomLengthTestFactory f;
	
	public MultiTestChromosomeFactory() {
		super();
		f = new RandomLengthTestFactory();
	}

	@Override
	public TestChromosome getChromosome() {
		MultiTestChromosome mtc = new MultiTestChromosome();
		mtc.setTestCase(f.getChromosome().getTestCase());
		return mtc;
	}

	
	
}
