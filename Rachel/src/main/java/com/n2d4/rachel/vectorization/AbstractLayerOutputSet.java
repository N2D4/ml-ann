package com.n2d4.rachel.vectorization;

import java.io.Serializable;

import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;

public class AbstractLayerOutputSet<I extends InputSet, O extends OutputSet> implements InOutSet<I, O>, Serializable, Debuggable {

	private static final long serialVersionUID = 1L;
	
	private I inputSet;
	private O outputSet;
	
	
	public AbstractLayerOutputSet(I inputSet, O outputSet) {
		this.inputSet = inputSet;
		this.outputSet = outputSet;
	}
	
	
	
	@Override
	public I getInputSet() {
		return inputSet;
	}

	@Override
	public O getOutputSet() {
		return outputSet;
	}
	
	
	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getInputSet()", getInputSet()).add("getOutputSet()", getOutputSet());
		return builder.toString();
	}
	
}
