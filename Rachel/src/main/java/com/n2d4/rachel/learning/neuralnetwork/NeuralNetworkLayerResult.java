package com.n2d4.rachel.learning.neuralnetwork;

import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;
import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;

public final class NeuralNetworkLayerResult implements Debuggable {

	private final LayerInputSet layerInput;
	private final UnactivatedLayerOutputSet unactivatedLayerOutput;
	private final LayerOutputSet layerOutput;

	NeuralNetworkLayerResult(LayerInputSet layerInput, UnactivatedLayerOutputSet unactivatedLayerOutput, LayerOutputSet layerOutput) {
		this.layerInput = layerInput;
		this.unactivatedLayerOutput = unactivatedLayerOutput;
		this.layerOutput = layerOutput;
	}

	public LayerInputSet getLayerInput() {
		return layerInput;
	}

	public UnactivatedLayerOutputSet getUnactivatedLayerOutput() {
		return unactivatedLayerOutput;
	}

	public LayerOutputSet getLayerOutput() {
		return layerOutput;
	}
	
	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getLayerInput()", getLayerInput()).add("getUnactivatedLayerOutput()", getUnactivatedLayerOutput())
				.add("getLayerOutput()", getLayerOutput());
		return builder.toString();
	}

}
