package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;
import com.n2d4.rachel.vectorization.VectorizedData;

public class ParameterApplyDerivative<T extends VectorizedData> extends Derivative<UnactivatedLayerOutputSet, T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParameterApplyDerivative(INDArray values) {
		super(values);
	}

}
