package com.n2d4.rachel.vectorization;

public class ValidationSet extends SupervisedInOutSet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ValidationSet(InOutSet<InputSet, OutputSet> inout) {
		this(inout.getInputSet(), inout.getOutputSet());
	}
	
	public ValidationSet(InputSet inputSet, OutputSet outputSet) {
		super(inputSet, outputSet);
	}

}
