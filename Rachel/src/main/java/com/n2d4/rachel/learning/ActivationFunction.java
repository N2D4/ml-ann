package com.n2d4.rachel.learning;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.BooleanIndexing;
import org.nd4j.linalg.indexing.conditions.Conditions;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;
import com.n2d4.rachel.vectorization.VectorizedData;
import com.n2d4.rachel.vectorization.derivatives.ActivationDerivative;

public interface ActivationFunction {
	public LayerOutputSet apply(UnactivatedLayerOutputSet layerSetOutput);
	public ActivationDerivative getDerivative(UnactivatedLayerOutputSet layerSetOutput, LayerOutputSet actualOutput);
	
	
	
	
	
	public static final ActivationFunction LINEAR = new ActivationFunction() {
		@Override public LayerOutputSet apply(UnactivatedLayerOutputSet layerSetOutput) {
			// TODO Requirements
			return new LayerOutputSet(VectorizedData.getINDArray(layerSetOutput));
		}
		
		@Override public ActivationDerivative getDerivative(UnactivatedLayerOutputSet layerSetOutput, LayerOutputSet actualOutput) {
			// TODO Requirements
			return new ActivationDerivative(Nd4j.ones(VectorizedData.getINDArray(layerSetOutput).shape()));
		}
	};
	
	
	
	
	
	
	public static final ActivationFunction SIGMOID = new ActivationFunction() {
		@Override public LayerOutputSet apply(UnactivatedLayerOutputSet layerSetOutput) {
			// TODO Requirements
			return new LayerOutputSet(Transforms.sigmoid(VectorizedData.getINDArray(layerSetOutput)));
		}
		
		@Override public ActivationDerivative getDerivative(UnactivatedLayerOutputSet layerSetOutput, LayerOutputSet actualOutput) {
			// TODO Requirements
			INDArray out = VectorizedData.getINDArray(actualOutput);
			return new ActivationDerivative(out.mul(out.rsub(1)));
		}
	};
	
	
	
	
	
	
	public static final ActivationFunction RELU = new ActivationFunction() {
		@Override public LayerOutputSet apply(UnactivatedLayerOutputSet layerSetOutput) {
			// TODO Requirements
			return new LayerOutputSet(Transforms.max(VectorizedData.getINDArray(layerSetOutput), 0));
		}
		
		@Override public ActivationDerivative getDerivative(UnactivatedLayerOutputSet layerSetOutput, LayerOutputSet actualOutput) {
			// TODO Requirements
			INDArray out = VectorizedData.getINDArray(actualOutput);
			return new ActivationDerivative(Transforms.sign(out));
		}
	};
	
	
	
	
	public static final ActivationFunction ANALYTIC = new ActivationFunction() {
		@Override public LayerOutputSet apply(UnactivatedLayerOutputSet layerSetOutput) {
			// TODO Requirements
			INDArray out = VectorizedData.getINDArray(layerSetOutput);
			INDArray n = Transforms.exp(out).add(1);
			BooleanIndexing.replaceWhere(n, out, Conditions.isInfinite());
			return new LayerOutputSet(Transforms.log(n));
		}
		
		@Override public ActivationDerivative getDerivative(UnactivatedLayerOutputSet layerSetOutput, LayerOutputSet actualOutput) {
			// TODO Requirements
			INDArray out = VectorizedData.getINDArray(actualOutput);
			return new ActivationDerivative(Transforms.sigmoid(out));
		}
	};
}
