package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.VectorizedData;

public class ParameterError<T extends VectorizedData> extends Derivative<CostSet, T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ParameterError(INDArray values) {
		super(values);
	}

}
