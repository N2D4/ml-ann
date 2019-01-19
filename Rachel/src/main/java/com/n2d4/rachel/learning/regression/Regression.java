package com.n2d4.rachel.learning.regression;

import com.n2d4.rachel.learning.CostFunction;

import java.util.Random;

import com.n2d4.rachel.learning.ActivationFunction;
import com.n2d4.rachel.learning.OptimizationFunction;
import com.n2d4.rachel.learning.SupervisedLearner;
import com.n2d4.rachel.learning.WeightApplyFunction;
import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.DataSet;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.LayerWeights;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.SupervisedInOutSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;
import com.n2d4.rachel.vectorization.UnrolledBiasLackingParameters;
import com.n2d4.rachel.vectorization.UnrolledParameters;
import com.n2d4.rachel.vectorization.derivatives.ParameterError;
import com.n2d4.rachel.vectorization.derivatives.UnrolledParameterErrors;

public abstract class Regression extends SupervisedLearner<RegressionState> {
	
	public Regression(CostFunction costFunction, ActivationFunction hypothesis, OptimizationFunction optimizationFunction, DataSet dataSet) {
		this(costFunction, hypothesis, optimizationFunction, WeightApplyFunction.MATRIX_MULT, dataSet);
	}
	
	public Regression(CostFunction costFunction, ActivationFunction hypothesis, OptimizationFunction optimizationFunction, WeightApplyFunction weightApplyFunction, DataSet dataSet) {
		super(costFunction, hypothesis, optimizationFunction, weightApplyFunction, dataSet);
	}
	
	
	public LayerWeights getWeights() {
		return getState().getWeights();
	}

	public RegressionState getNewState(long seed) {
		return new RegressionState(getInputSize(), getOutputSize(), new Random(seed));
	}
	
	
	@Override
	public void onTrain(SupervisedInOutSet trainingSet) {
		getWeights().checkInOutSet(trainingSet);
		
		InputSet input = trainingSet.getInputSet();
		RegressionProcessResult res = processFull(input);
		
		UnrolledParameters params = getUnrolledParameters();
		UnrolledBiasLackingParameters noBias = getUnrolledBiasLackingParameters();
		
		UnrolledParameterErrors deriv = new UnrolledParameterErrors(getWeightError(trainingSet, res, noBias));
		
		getOptimizationFunction().step(getState().getOptimizationData(), params, deriv);
		params.reshapeOriginals();
	}
	
	
	
	@Override
	protected CostSet getCost(CostFunction function, SupervisedInOutSet inoutSet, OutputSet actualOutput) {
		return getCost(function, inoutSet, actualOutput, getUnrolledBiasLackingParameters());
	}
	
	protected CostSet getCost(CostFunction function, SupervisedInOutSet inoutSet, RegressionProcessResult actualResult, UnrolledBiasLackingParameters biasLackingParams) {
		return getCost(function, inoutSet, actualResult.getOutputSet(), biasLackingParams);
	}
	
	protected CostSet getCost(CostFunction function, SupervisedInOutSet inoutSet, OutputSet actualOutput, UnrolledBiasLackingParameters biasLackingParams) {
		Requirements.nonNull(function, "cost function");
		Requirements.nonNull(inoutSet, "in/out set");
		Requirements.nonNull(actualOutput, "actual output");
		
		return function.getCost(biasLackingParams, inoutSet.getOutputSet(), actualOutput);
	}
	
	protected ParameterError<LayerWeights> getWeightError(SupervisedInOutSet inoutSet, RegressionProcessResult actualResult, UnrolledBiasLackingParameters biasLackingParams) {
		return getCostFunction().getWeightDerivative(biasLackingParams, inoutSet.getOutputSet(), actualResult.getOutputSet(), actualResult.getLayerInputSet(), getWeights(), actualResult.getUnactivatedOutputSet(), actualResult.getLayerOutputSet(), getActivationFunction(), getWeightApplyFunction());
	}
	
	
	
	
	protected UnrolledParameters getUnrolledParameters() {
		return new UnrolledParameters(getWeights());
	}
	
	protected UnrolledBiasLackingParameters getUnrolledBiasLackingParameters() {
		return new UnrolledBiasLackingParameters(getWeights().withoutBias());
	}
	

	
	public RegressionProcessResult processFull(InputSet input) {
		LayerInputSet layerInputSet = new LayerInputSet(input);
		UnactivatedLayerOutputSet unactivatedOutputSet = getWeightApplyFunction().getOutput(layerInputSet, getWeights());
		LayerOutputSet layerOutputSet = getActivationFunction().apply(unactivatedOutputSet);
		OutputSet outputSet = new OutputSet(layerOutputSet);
		
		return new RegressionProcessResult(layerInputSet, unactivatedOutputSet, layerOutputSet, outputSet);
	}
	
	@Override
	public OutputSet process(InputSet input) {
		return processFull(input).getOutputSet();
	}
	


	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getIterationCount()", getIterationCount()).add("getWeights()", getWeights())
				.add("getCostFunction()", getCostFunction());
		return builder.toString();
	}
	
}
