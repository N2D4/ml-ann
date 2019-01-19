package com.n2d4.rachel.vectorization;

public class TestSet extends SupervisedInOutSet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TestSet(InOutSet<InputSet, OutputSet> inout) {
		this(inout.getInputSet(), inout.getOutputSet());
	}
	
	public TestSet(InputSet inputSet, OutputSet outputSet) {
		super(inputSet, outputSet);
	}

}
