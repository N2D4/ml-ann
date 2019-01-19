package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.vectorization.DataDelta;
import com.n2d4.rachel.vectorization.VectorizedData;

public class Derivative<T extends VectorizedData, R extends VectorizedData> extends DataDelta<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Derivative(INDArray values) {
		super(values);
	}
	
}
