package com.n2d4.rachel.learning;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.n2d4.rachel.util.math.Nd4jUtils;
import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.LayerInputError;
import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.LayerWeights;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;
import com.n2d4.rachel.vectorization.UnrolledBiasLackingParameters;
import com.n2d4.rachel.vectorization.VectorizedData;
import com.n2d4.rachel.vectorization.derivatives.Erroneousness;
import com.n2d4.rachel.vectorization.derivatives.LayerCostDerivative;
import com.n2d4.rachel.vectorization.derivatives.OutputCostDerivative;
import com.n2d4.rachel.vectorization.derivatives.ParameterError;

public interface CostFunction {
	public CostSet getCost(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput);
	public OutputCostDerivative getOutputDerivative(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput);
	
	public default Erroneousness getErroneousness(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput, UnactivatedLayerOutputSet unactivatedLayerOutput, LayerOutputSet actualLayerOutput, ActivationFunction activationFunction) {
		// Optimize some common combinations of cost and activation functions
		if (this == CostFunction.LOGARITHMIC && actualOutput.hasIgnorableDerivative() && activationFunction == ActivationFunction.SIGMOID)	// extremely common in logistic regression and sometimes in neural networks; divisor of cost function and activation function reduce each other
			return new Erroneousness(VectorizedData.getINDArray(expectedOutput.getDifference(actualOutput)).mul(1d/expectedOutput.getSetCount()));
		
		
		OutputCostDerivative oderiv = getOutputDerivative(weightDecayParameters, expectedOutput, actualOutput);
		LayerCostDerivative lderiv;
		if (actualOutput.hasIgnorableDerivative()) // if the output's derivative can be ignored, then do so
			lderiv = new LayerCostDerivative(VectorizedData.getINDArray(oderiv));
		else
			lderiv = oderiv.chain(actualOutput.getLayerOutputSetDerivative());
		
		if (activationFunction == ActivationFunction.LINEAR) // linear activation function's derivative matrix consists out of only ones, so we can simply skip that step instead
			return new Erroneousness(VectorizedData.getINDArray(lderiv));
		
		return lderiv.chain(activationFunction.getDerivative(unactivatedLayerOutput, actualLayerOutput));
	}
	
	
	public default ParameterError<LayerWeights> getWeightDerivative(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput, LayerInputSet layerInput, LayerWeights weights, UnactivatedLayerOutputSet unactivatedLayerOutput, LayerOutputSet actualLayerOutput, ActivationFunction activationFunction, WeightApplyFunction applyFunction) {
		return getErroneousness(weightDecayParameters, expectedOutput, actualOutput, unactivatedLayerOutput, actualLayerOutput, activationFunction).chain(applyFunction.getWeightDerivative(layerInput, weights));
	}
	
	
	public default LayerInputError getInputDerivative(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput, LayerInputSet layerInput, LayerWeights weights, UnactivatedLayerOutputSet unactivatedLayerOutput, LayerOutputSet actualLayerOutput, ActivationFunction activationFunction, WeightApplyFunction applyFunction) {
		return getErroneousness(weightDecayParameters, expectedOutput, actualOutput, unactivatedLayerOutput, actualLayerOutput, activationFunction).chainInput(applyFunction.getInputDerivative(layerInput, weights));
	}
	
	
	
	
	public static final CostFunction HALF_SQUARED = new CostFunction() {
		@Override public CostSet getCost(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput) {
			// TODO Requirements
			INDArray dif = VectorizedData.getINDArray(expectedOutput.getDifference(actualOutput));
			INDArray J = Transforms.pow(dif, 2).sum(0).mul(1/(2d * dif.rows()));
			return new CostSet(J);
		}
		
		@Override public OutputCostDerivative getOutputDerivative(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput) {
			// TODO Requirements
			INDArray dif = VectorizedData.getINDArray(expectedOutput.getDifference(actualOutput));
			INDArray grad = dif.mul(1d/expectedOutput.getSetCount());
			return new OutputCostDerivative(grad);
		}
	};
	
	
	
	
	public static final CostFunction LINEAR = new CostFunction() {
		@Override public CostSet getCost(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput) {
			// TODO Requirements
			INDArray dif = VectorizedData.getINDArray(expectedOutput.getDifference(actualOutput));
			INDArray J = Transforms.abs(dif).sum(0).mul(1d/dif.rows());
			return new CostSet(J);
		}
		
		@Override public OutputCostDerivative getOutputDerivative(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput) {
			// TODO Requirements
			INDArray dif = VectorizedData.getINDArray(expectedOutput.getDifference(actualOutput));
			INDArray grad = Transforms.sign(dif).mul(1d/expectedOutput.getSetCount());
			return new OutputCostDerivative(grad);
		}
	};
	
	
	

	public static final CostFunction LOGARITHMIC = new CostFunction() {
		@Override public CostSet getCost(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput) {
			// TODO Requirements
			INDArray expected = VectorizedData.getINDArray(expectedOutput.getMixer().mix(actualOutput));
			INDArray actual = VectorizedData.getINDArray(actualOutput);
			INDArray J = expected.mul(Nd4jUtils.zLog(actual)).add(expected.rsub(1).mul(Nd4jUtils.zLog(actual.rsub(1)))).sum(0).mul(-1d/actual.rows());
			return new CostSet(J);
		}
		
		@Override public OutputCostDerivative getOutputDerivative(UnrolledBiasLackingParameters weightDecayParameters, OutputSet expectedOutput, OutputSet actualOutput) {
			// TODO Requirements
			INDArray dif = VectorizedData.getINDArray(expectedOutput.getDifference(actualOutput));
			INDArray actual = VectorizedData.getINDArray(actualOutput);
			INDArray bel = Transforms.max(actual.mul(actual.rsub(1)), Double.MIN_NORMAL);
			INDArray grad = dif.div(bel).mul(1d/expectedOutput.getSetCount());
			return new OutputCostDerivative(grad);
		}
		
	};
	
	
	
	
}
