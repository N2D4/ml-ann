package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;

public abstract class LayerSet extends ValueSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected LayerSet(INDArray data) {
		super(data);
	}

}
