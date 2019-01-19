package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;

public class InputSet extends SupervisedSet implements InputValueSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InputSet(double[]... sets) {
		super(sets);
	}

	public InputSet(double... set) {
		super(set);
	}

	public InputSet(INDArray data) {
		super(data);
	}

	@Override
	public int getInputCount() {
		return getSetSize();
	}

}
