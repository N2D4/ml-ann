package com.n2d4.rachel.learning;

import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerWeights;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;
import com.n2d4.rachel.vectorization.VectorizedData;
import com.n2d4.rachel.vectorization.derivatives.ParameterApplyDerivative;

public interface WeightApplyFunction {
	public UnactivatedLayerOutputSet getOutput(LayerInputSet input, LayerWeights weights);
	public ParameterApplyDerivative<LayerWeights> getWeightDerivative(LayerInputSet input, LayerWeights weights);
	public ParameterApplyDerivative<LayerInputSet> getInputDerivative(LayerInputSet input, LayerWeights weights);
	
	
	
	
	public static WeightApplyFunction MATRIX_MULT = new WeightApplyFunction() {
		@Override public UnactivatedLayerOutputSet getOutput(LayerInputSet input, LayerWeights weights) {
			// TODO Requirements
			return new UnactivatedLayerOutputSet(VectorizedData.getINDArray(input).mmul(VectorizedData.getINDArray(weights)));
		}

		@Override
		public ParameterApplyDerivative<LayerWeights> getWeightDerivative(LayerInputSet input, LayerWeights weights) {
			// TODO Requirements
			return new ParameterApplyDerivative<LayerWeights>(VectorizedData.getINDArray(input));
		}

		@Override
		public ParameterApplyDerivative<LayerInputSet> getInputDerivative(LayerInputSet input, LayerWeights weights) {
			// TODO Requirements
			return new ParameterApplyDerivative<LayerInputSet>(VectorizedData.getINDArray(weights));
		}
		
	};
}
