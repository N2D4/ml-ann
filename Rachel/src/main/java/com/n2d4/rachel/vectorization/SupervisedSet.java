package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;

public abstract class SupervisedSet extends ValueSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SupervisedSet(INDArray data) {
		super(data);
	}
	
	public SupervisedSet(double[]... data) {
		super(data);
	}

}
