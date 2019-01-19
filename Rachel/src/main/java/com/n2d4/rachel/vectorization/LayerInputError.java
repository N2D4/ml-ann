package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;

import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.derivatives.LayerCostDerivative;
import com.n2d4.rachel.vectorization.derivatives.LayerInputDerivative;
import com.n2d4.rachel.vectorization.derivatives.ParameterError;

public class LayerInputError extends ParameterError<LayerInputSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public LayerInputError(INDArray values) {
		super(values);
	}
	
	
	public LayerCostDerivative chain(LayerInputDerivative with) {
		Requirements.nonNull(with, "derivative to chain with");
		INDArray nout = VectorizedData.getINDArray(with);
		INDArray vals = getValues();
		return new LayerCostDerivative(vals.get(NDArrayIndex.all(), NDArrayIndex.interval(1, vals.columns())).mul(nout));
	}

}
