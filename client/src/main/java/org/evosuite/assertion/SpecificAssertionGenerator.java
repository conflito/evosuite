package org.evosuite.assertion;

import java.util.Set;

import org.evosuite.Properties;
import org.evosuite.testcase.TestCase;
import org.evosuite.testcase.execution.ExecutionResult;
import org.evosuite.testcase.statements.MethodStatement;
import org.evosuite.testcase.statements.Statement;
import org.evosuite.testcase.variable.VariableReference;

public class SpecificAssertionGenerator extends AssertionGenerator {
	
	@Override
	public void addAssertions(TestCase test) {
		ExecutionResult result = runTest(test);
		for (OutputTrace<?> trace : result.getTraces()) {
			trace.getAllAssertions(test);
		}

		for (int i = 0; i < test.size(); i++) {
			Statement s = test.getStatement(i);
			if(isAllFieldsMethod(s))
				filterAssertions(s, true);
			else {
				handleOthers(s, test);
			}
				
		}
	}
	
	private void handleOthers(Statement s, TestCase test) {
		if(isRelevant(s, test)) {
			filterAssertions(s, false);
		}
		else {
			s.removeAssertions();
		}
	}

	private void filterAssertions(Statement s, boolean justOne) {
		VariableReference returnVal = s.getReturnValue();
		
		if(s.hasAssertions()) {
			Assertion[] assertions = s.getAssertions()
					.stream().toArray(Assertion[]::new);
			for (int j = 0; j < assertions.length; j++) {
				Assertion a = assertions[j];
				if(a != null) {
					Set<VariableReference> referenced = a.getReferencedVariables();
					if((justOne && referenced.size() != 1) || !referenced.contains(returnVal)) {
						s.removeAssertion(a);
					}
				}
			}
		}

	}
	
	private boolean isAllFieldsMethod(Statement s) {
		if (s instanceof MethodStatement) {
			MethodStatement ms = (MethodStatement) s;
			String methodName = ms.getMethodName();
			String methodDesc = ms.getDescriptor();
			return methodName.equals(Properties.ALL_FIELDS_METHOD_NAME) &&
					methodDesc.equals(Properties.ALL_FIELDS_METHOD_DESC);
		}
		return false;
	}

	private boolean isRelevant(Statement s, TestCase test) {		
		if (s instanceof MethodStatement) {
			MethodStatement ms = (MethodStatement) s;
			String declaringClass = ms.getMethod().getDeclaringClass().getName();
			
			while (declaringClass.contains("$"))
				declaringClass = declaringClass.substring(0, declaringClass.indexOf("$"));

			if (declaringClass.equals(Properties.TARGET_CLASS) || 
					(!Properties.TARGET_CLASS_PREFIX.isEmpty() 
							&& declaringClass.startsWith(Properties.TARGET_CLASS_PREFIX)))
				return true;
		}
		return false;
	}

}
