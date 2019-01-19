package com.n2d4.rachel.learning.neuralnetwork;

import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.learning.ActivationFunction;
import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.OptimizationFunction;
import com.n2d4.rachel.learning.SupervisedLearner;
import com.n2d4.rachel.learning.WeightApplyFunction;
import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.util.Util;
import com.n2d4.rachel.vectorization.BiasLackingLayerWeights;
import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.DataSet;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerWeights;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.SupervisedInOutSet;
import com.n2d4.rachel.vectorization.TrainingSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;
import com.n2d4.rachel.vectorization.UnrolledBiasLackingParameters;
import com.n2d4.rachel.vectorization.UnrolledParameters;
import com.n2d4.rachel.vectorization.VectorizedData;
import com.n2d4.rachel.vectorization.derivatives.Erroneousness;
import com.n2d4.rachel.vectorization.derivatives.ParameterApplyDerivative;
import com.n2d4.rachel.vectorization.derivatives.ParameterError;
import com.n2d4.rachel.vectorization.derivatives.UnrolledParameterErrors;

public class NeuralNetwork extends SupervisedLearner<NeuralNetworkState> {
	
	public static final CostFunction DEFAULT_COST_FUNCTION = CostFunction.HALF_SQUARED;
	public static final ActivationFunction DEFAULT_ACTIVATION_FUNCTION = ActivationFunction.SIGMOID;
	
	
	private final int[] layerSizes;
	
	public NeuralNetwork(DataSet dataSet, double learningRate, int... hiddenLayerSizes) {
		this(DEFAULT_ACTIVATION_FUNCTION, dataSet, learningRate, hiddenLayerSizes);
	}
	
	public NeuralNetwork(CostFunction costFunction, DataSet dataSet, double learningRate, int... hiddenLayerSizes) {
		this(costFunction, DEFAULT_ACTIVATION_FUNCTION, dataSet, learningRate, hiddenLayerSizes);
	}
	
	public NeuralNetwork(ActivationFunction activationFunction, DataSet dataSet, double learningRate, int... hiddenLayerSizes) {
		this(DEFAULT_COST_FUNCTION, activationFunction, dataSet, learningRate, hiddenLayerSizes);
	}
	
	public NeuralNetwork(CostFunction costFunction, ActivationFunction activationFunction, DataSet dataSet, double learningRate, int... hiddenLayerSizes) {
		this(costFunction, activationFunction, OptimizationFunction.GRADIENT_DESCENT(learningRate), dataSet, hiddenLayerSizes);
	}
	
	public NeuralNetwork(CostFunction costFunction, ActivationFunction activationFunction, OptimizationFunction optimizationFunction, DataSet dataSet, int... hiddenLayerSizes) {
		this(costFunction, activationFunction, optimizationFunction, WeightApplyFunction.MATRIX_MULT, dataSet, hiddenLayerSizes);
	}

	public NeuralNetwork(CostFunction costFunction, ActivationFunction activationFunction, OptimizationFunction optimizationFunction, WeightApplyFunction weightApplyFunction, DataSet dataSet, int... hiddenLayerSizes) {
		super(costFunction, activationFunction, optimizationFunction, weightApplyFunction, dataSet);
		
		Requirements.nonNull(hiddenLayerSizes, "hidden layers");
		layerSizes = new int[hiddenLayerSizes.length + 2];
		layerSizes[0] = dataSet.getInputSize();
		layerSizes[layerSizes.length - 1] = dataSet.getOutputSize();
		for (int i = 1; i < layerSizes.length - 1; i++) {
			layerSizes[i] = hiddenLayerSizes[i - 1];
		}
	}
	
	
	

	
	public LayerWeights[] getWeights() {
		return getState().getWeights();
	}
	
	public LayerWeights getLayerWeights(int layer) {
		// TODO Requirements
		return getState().getLayerWeights(layer);
	}
	
	public int getLayerCount() {
		return getState().getLayerCount();
	}
	
	
	
	protected UnrolledParameters getUnrolledParameters() {
		return new UnrolledParameters(getWeights());
	}
	
	protected UnrolledBiasLackingParameters getUnrolledBiasLackingParameters() {
		BiasLackingLayerWeights[] weights = new BiasLackingLayerWeights[getLayerCount()];
		for (int i = 0; i < weights.length; i++) {
			weights[i] = getLayerWeights(i).withoutBias();
		}
		return new UnrolledBiasLackingParameters(weights);
	}
	
	
	
	
	protected NeuralNetworkLayerResult processLayer(LayerInputSet input, int layer) {
		Requirements.nonNull(input, "input");
		
		UnactivatedLayerOutputSet unactivated = getWeightApplyFunction().getOutput(input, getLayerWeights(layer));
		return new NeuralNetworkLayerResult(input, unactivated, getActivationFunction().apply(unactivated));
	}
	
	protected NeuralNetworkLayerResult[] processLayers(LayerInputSet input, int startingLayer, int outputLayer) {
		Requirements.nonNull(input, "input");
		Requirements.nonNegative(startingLayer, "starting layer");
		Requirements.nonNegative(outputLayer, "output layer");
		
		NeuralNetworkLayerResult[] result = new NeuralNetworkLayerResult[outputLayer - startingLayer];
		for (int i = 0; i < result.length; i++) {
			result[i] = processLayer(input, startingLayer + i);
			input = new LayerInputSet(result[i].getLayerOutput());
		}
		return result;
	}
	
	protected NeuralNetworkLayerResult[] processLayers(LayerInputSet firstInput) {
		return processLayers(firstInput, 0, getLayerCount());
	}
	
	public NeuralNetworkProcessResult processFullOutput(InputSet input) {
		NeuralNetworkLayerResult[] out = processLayers(new LayerInputSet(input));
		return new NeuralNetworkProcessResult(out, new OutputSet(out[out.length - 1].getLayerOutput()));
	}
	
	@Override
	public OutputSet process(InputSet input) {
		Requirements.nonNull(input, "input set");
		Requirements.equal(input.getInputCount(), getInputSize(), "input feature count");
		
		return processFullOutput(input).getOutput();
	}

	@Override
	protected void onTrain(SupervisedInOutSet trainingSet) {
		Requirements.nonNull(trainingSet, "training set");
		
		
		NeuralNetworkProcessResult result = processFullOutput(trainingSet.getInputSet());
		
		Erroneousness[] errors = new Erroneousness[getLayerCount()];
		errors[errors.length - 1] = getCostFunction().getErroneousness(getUnrolledBiasLackingParameters(), trainingSet.getOutputSet(), result.getOutput(), result.getFinalLayer().getUnactivatedLayerOutput(), result.getFinalLayer().getLayerOutput(), getActivationFunction());
		for (int i = errors.length - 2; i >= 0; i--) {
			LayerInputSet layerInput = result.getLayerInputSet(i + 1);
			ParameterApplyDerivative<LayerInputSet> inputError = getWeightApplyFunction().getInputDerivative(layerInput, getLayerWeights(i + 1));
			errors[i] = errors[i + 1].chainInput(inputError).chain(layerInput.getLayerInputSetDerivative()).chain(getActivationFunction().getDerivative(result.getUnactivatedLayerOutputSet(i), result.getLayerOutputSet(i)));
		}
		
		ParameterError<?>[] weightErrors = new ParameterError<?>[errors.length];
		for (int i = 0; i < weightErrors.length; i++) {
			weightErrors[i] = errors[i].chain(getWeightApplyFunction().getWeightDerivative(result.getLayerInputSet(i), getLayerWeights(i)));
		}
		
		UnrolledParameters params = getUnrolledParameters();
		UnrolledParameterErrors unrolledParamErrors = new UnrolledParameterErrors(weightErrors);
		getOptimizationFunction().step(getState().getOptimizationData(), params, unrolledParamErrors);
		params.reshapeOriginals();
	}
	
	
	protected ParameterError<LayerWeights> checkDerivatives(TrainingSet trainingSet, int layer) {
		final double epsilon = 0.0001;
		NeuralNetworkState initial = getState();
		
		int[] shape = VectorizedData.getINDArray(getLayerWeights(layer)).shape();
		ParameterError<LayerWeights> result = new ParameterError<LayerWeights>(Nd4j.create(shape));
		INDArray resarr = VectorizedData.getINDArray(result);
		int[] cur = Util.elementWiseLoopInit(shape.length);
		while (Util.elementWiseIncrement(cur, shape)) {
			double res = 0;
			for (int i = -1; i <= 1; i += 2) {
				restoreState(new NeuralNetworkState(initial));
				INDArray arr = VectorizedData.getINDArray(getState().getLayerWeights(layer));
				arr.putScalar(cur, arr.getDouble(cur) + (epsilon * i));
				res += i * VectorizedData.getINDArray(getCost(trainingSet)).sumNumber().doubleValue();
			}
			resarr.putScalar(cur, res / (2 * epsilon));
		}
		
		restoreState(initial);
		return result;
	}

	@Override
	protected CostSet getCost(CostFunction function, SupervisedInOutSet set, OutputSet actualOutput) {
		return function.getCost(getUnrolledBiasLackingParameters(), set.getOutputSet(), actualOutput);
	}

	@Override
	protected NeuralNetworkState getNewState(long seed) {
		return new NeuralNetworkState(layerSizes, new Random(seed));
	}
	

	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getWeights()", getWeights()).add("getLayerCount()", getLayerCount())
				.add("getCostFunction()", getCostFunction()).add("getActivationFunction()", getActivationFunction())
				.add("getOptimizationFunction()", getOptimizationFunction())
				.add("getWeightApplyFunction()", getWeightApplyFunction()).add("getDataSet()", getDataSet());
		return builder.toString();
	}

}
