package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;

public class ActivationDerivative extends Derivative<LayerOutputSet, UnactivatedLayerOutputSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ActivationDerivative(INDArray values) {
		super(values);
	}

}
