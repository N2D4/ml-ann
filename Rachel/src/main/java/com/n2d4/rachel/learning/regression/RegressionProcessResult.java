package com.n2d4.rachel.learning.regression;

import com.n2d4.rachel.vectorization.LayerInputSet;
import com.n2d4.rachel.vectorization.LayerOutputSet;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.UnactivatedLayerOutputSet;

public final class RegressionProcessResult {
	
	private LayerInputSet layerInputSet;
	private UnactivatedLayerOutputSet unactivatedOutputSet;
	private LayerOutputSet layerOutputSet;
	private OutputSet outputSet;
	
	
	RegressionProcessResult(LayerInputSet layerInputSet, UnactivatedLayerOutputSet unactivatedOutputSet, LayerOutputSet layerOutputSet, OutputSet outputSet) {
		this.layerInputSet = layerInputSet;
		this.unactivatedOutputSet = unactivatedOutputSet;
		this.layerOutputSet = layerOutputSet;
		this.outputSet = outputSet;
	}
	
	
	public LayerInputSet getLayerInputSet() {
		return layerInputSet;
	}
	
	public LayerOutputSet getLayerOutputSet() {
		return layerOutputSet;
	}
	
	public UnactivatedLayerOutputSet getUnactivatedOutputSet() {
		return unactivatedOutputSet;
	}
	
	public OutputSet getOutputSet() {
		return outputSet;
	}
	
	
}
