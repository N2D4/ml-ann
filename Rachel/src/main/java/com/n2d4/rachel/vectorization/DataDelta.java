package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;

public class DataDelta<T extends VectorizedData> extends VectorizedData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DataDelta(INDArray values) {
		super(values);
	}

	public DataDelta(T one, T two) {
		// TODO Requirements
		this(VectorizedData.getINDArray(one).sub(VectorizedData.getINDArray(two)));
	}

}
