package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.OutputSet;

public class OutputDerivative extends Derivative<OutputSet, LayerOutputSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public OutputDerivative(INDArray values) {
		super(values);
	}

}
