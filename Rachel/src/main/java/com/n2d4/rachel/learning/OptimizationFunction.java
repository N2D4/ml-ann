package com.n2d4.rachel.learning;

import com.n2d4.rachel.vectorization.UnrolledParameters;
import com.n2d4.rachel.vectorization.derivatives.UnrolledParameterErrors;

public interface OptimizationFunction {
	
	public OptimizationFunctionData step(OptimizationFunctionData data, UnrolledParameters parameters, UnrolledParameterErrors costDerivative);
	
	
	
	
	public static OptimizationFunction GRADIENT_DESCENT(double learningRate) {
		return new OptimizationFunction() {
			@Override public OptimizationFunctionData step(OptimizationFunctionData data, UnrolledParameters parameters, UnrolledParameterErrors costDerivative) {
				// TODO Requirements
				parameters.updateFrom(costDerivative, learningRate);
				return null;
			}
		};
	}
	
}
