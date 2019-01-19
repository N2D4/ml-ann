package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.VectorizedData;

public class OutputCostDerivative extends Derivative<CostSet, OutputSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public OutputCostDerivative(INDArray values) {
		super(values);
	}
	
	
	public LayerCostDerivative chain(OutputDerivative with) {
		Requirements.nonNull(with, "derivative to chain with");
		return new LayerCostDerivative(getValues().mul(VectorizedData.getINDArray(with)));
	}

}
