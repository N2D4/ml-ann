package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;

abstract class SkeletalLayerOutputSet extends LayerSet implements OutputValueSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SkeletalLayerOutputSet(INDArray data) {
		super(data);
	}

	@Override
	public int getOutputCount() {
		return getSetSize();
	}

}
