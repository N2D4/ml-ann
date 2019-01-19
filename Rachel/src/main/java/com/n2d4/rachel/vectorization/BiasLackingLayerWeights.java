package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;

public class BiasLackingLayerWeights extends VectorizedData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	BiasLackingLayerWeights(INDArray data) {
		super(data.get(NDArrayIndex.interval(1, data.rows()), NDArrayIndex.all()));
	}

	public int getInputSize() {
		return getRowCount();
	}
	
	public int getOutputSize() {
		return getColumnCount();
	}
	
}
