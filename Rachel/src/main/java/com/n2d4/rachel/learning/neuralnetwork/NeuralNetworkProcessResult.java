package com.n2d4.rachel.learning.neuralnetwork;

import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;
import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;

public final class NeuralNetworkProcessResult implements Debuggable {
	
	private final NeuralNetworkLayerResult[] layerResults;
	private final OutputSet output;

	NeuralNetworkProcessResult(NeuralNetworkLayerResult[] layerResults, OutputSet output) {
		this.layerResults = layerResults;
		this.output = output;
	}

	public OutputSet getOutput() {
		return output;
	}

	public NeuralNetworkLayerResult[] getLayerResults() {
		return layerResults;
	}

	public NeuralNetworkLayerResult getLayerResult(int layer) {
		return getLayerResults()[layer];
	}
	
	public int getLayerCount() {
		return getLayerResults().length;
	}
	
	public LayerInputSet getLayerInputSet(int layer) {
		return getLayerResult(layer).getLayerInput();
	}
	
	public LayerOutputSet getLayerOutputSet(int layer) {
		return getLayerResult(layer).getLayerOutput();
	}
	
	public UnactivatedLayerOutputSet getUnactivatedLayerOutputSet(int layer) {
		return getLayerResult(layer).getUnactivatedLayerOutput();
	}
	
	public NeuralNetworkLayerResult getFinalLayer() {
		return getLayerResult(getLayerCount() - 1);
	}

	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getOutput()", getOutput()).add("getLayerResults()", getLayerResults()).add("getLayerCount()",
				getLayerCount());
		return builder.toString();
	}
	
	

}
