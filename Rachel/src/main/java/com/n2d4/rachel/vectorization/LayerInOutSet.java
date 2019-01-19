package com.n2d4.rachel.vectorization;

import java.io.Serializable;

import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;

public class LayerInOutSet implements InOutSet<LayerInputSet, LayerOutputSet>, Serializable, Debuggable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final LayerInputSet inputSet;
	private final LayerOutputSet outputSet;

	public LayerInOutSet(LayerInputSet inputSet, LayerOutputSet outputSet) {
		this.inputSet = inputSet;
		this.outputSet = outputSet;
	}
	
	@Override public LayerInputSet getInputSet() {
		return inputSet;
	}

	@Override public LayerOutputSet getOutputSet() {
		return outputSet;
	}


	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getInputSet()", getInputSet()).add("getOutputSet()", getOutputSet());
		return builder.toString();
	}
}
