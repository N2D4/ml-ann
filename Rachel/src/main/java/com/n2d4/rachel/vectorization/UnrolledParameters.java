package com.n2d4.rachel.vectorization;

import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.derivatives.UnrolledParameterErrors;

public class UnrolledParameters extends UnrolledData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public UnrolledParameters(VectorizedData... from) {
		super(from);
	}
	
	
	public void updateFrom(UnrolledParameterErrors derivative, double learningRate) {
		Requirements.equal(derivative.getDataLength(), getDataLength(), "derivative data length");
		
		getValues().subi(VectorizedData.getINDArray(derivative).mul(learningRate));
	}
	
}
