package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.VectorizedData;

public class LayerCostDerivative extends Derivative<CostSet, LayerOutputSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public LayerCostDerivative(INDArray values) {
		super(values);
	}
	
	
	public Erroneousness chain(ActivationDerivative with) {
		Requirements.nonNull(with, "derivative to chain with");
		return new Erroneousness(getValues().mul(VectorizedData.getINDArray(with)));
	}

}
