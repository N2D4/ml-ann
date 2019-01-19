package com.n2d4.rachel.learning;

import java.io.Serializable;

import com.n2d4.rachel.util.Requirements;

public abstract class ExperimentState implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private int iterations = 0;
	private OptimizationFunctionData optimizationData;
	
	public ExperimentState() {
		
	}
	
	protected ExperimentState(ExperimentState from) {
		Requirements.nonNull(from, "state to clone");
		
		iterations = from.getIterationCount();
		optimizationData = from.getOptimizationData();
		if (optimizationData != null) optimizationData = optimizationData.clone();
	}
	
	public final int getIterationCount() {
		return iterations;
	}
	
	final void increaseIterationCount() {
		iterations++;
	}
	
	
	
	public final OptimizationFunctionData getOptimizationData() {
		return optimizationData;
	}
	
	public final void setOptimizationData(OptimizationFunctionData optimizationData) {
		this.optimizationData = optimizationData;
	}
	
}
