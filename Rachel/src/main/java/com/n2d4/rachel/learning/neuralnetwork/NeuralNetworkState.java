package com.n2d4.rachel.learning.neuralnetwork;

import java.util.Random;

import com.n2d4.rachel.learning.ExperimentState;
import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.LayerWeights;
import com.n2d4.rachel.vectorization.VectorizedData;

public class NeuralNetworkState extends ExperimentState implements Debuggable {
	private static final long serialVersionUID = 1L;
	
	private final LayerWeights[] layerWeights;
	
	public NeuralNetworkState(NeuralNetworkState from) {
		super(Requirements.nonNull(from, "state to clone"));
		
		LayerWeights[] toCopy = from.getWeights();
		layerWeights = new LayerWeights[toCopy.length];
		for (int i = 0; i < layerWeights.length; i++) {
			layerWeights[i] = new LayerWeights(VectorizedData.getINDArray(toCopy[i]).dup());
		}
	}

	public NeuralNetworkState(int[] layerSizes, Random random) {
		Requirements.nonNull(layerSizes, "layer sizes");		// TODO: Merge requirements when .nonEmpty is fixed for primitive values
		Requirements.positive(layerSizes.length, "layer sizes length");
		
		layerWeights = new LayerWeights[layerSizes.length - 1];
		for (int i = 0; i < layerWeights.length; i++) {
			layerWeights[i] = new LayerWeights(layerSizes[i], layerSizes[i + 1], random);
		}
	}
	
	
	public LayerWeights[] getWeights() {
		return layerWeights;
	}
	
	public LayerWeights getLayerWeights(int layer) {
		// TODO Requirements
		return getWeights()[layer];
	}
	
	public int getLayerCount() {
		return getWeights().length;
	}

	public int getLayerInputSize(int layer) {
		// TODO Requirements
		return getLayerWeights(layer).getInputSize();
	}

	public int getLayerOutputSize(int layer) {
		// TODO Requirements
		return getLayerWeights(layer).getOutputSize();
	}

	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getWeights()", getWeights()).add("getLayerCount()", getLayerCount())
				.add("getIterationCount()", getIterationCount()).add("getOptimizationData()", getOptimizationData());
		return builder.toString();
	}
	
	
}
