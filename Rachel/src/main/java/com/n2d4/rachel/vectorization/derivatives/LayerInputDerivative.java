package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerOutputSet;

public class LayerInputDerivative extends Derivative<LayerInputSet, LayerOutputSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LayerInputDerivative(INDArray values) {
		super(values);
	}

}
