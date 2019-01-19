package com.n2d4.rachel.vectorization.derivatives;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.LayerInputError;
import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerWeights;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;
import com.n2d4.rachel.vectorization.VectorizedData;

public class Erroneousness extends Derivative<CostSet, UnactivatedLayerOutputSet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Erroneousness(INDArray values) {
		super(values);
	}
	
	
	public ParameterError<LayerWeights> chain(ParameterApplyDerivative<LayerWeights> with) {
		Requirements.nonNull(with, "derivative to chain with");
		return new ParameterError<LayerWeights>(VectorizedData.getINDArray(with).transpose().mmul(getValues()));
	}
	
	public LayerInputError chainInput(ParameterApplyDerivative<LayerInputSet> with) {
		Requirements.nonNull(with, "derivative to chain with");
		return new LayerInputError(getValues().mmul(VectorizedData.getINDArray(with).transpose()));
	}

}
