package com.n2d4.rachel.learning.regression;

import java.util.Random;

import com.n2d4.rachel.learning.ExperimentState;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.vectorization.LayerWeights;

public class RegressionState extends ExperimentState {
	private static final long serialVersionUID = 1L;
	
	private LayerWeights weights;
	
	public RegressionState(int inputSize, int outputSize, Random random) {
		Requirements.positive(inputSize, "input size");
		Requirements.positive(outputSize, "output size");
		
		this.weights = new LayerWeights(inputSize, outputSize, random);
	}
	
	public LayerWeights getWeights() {
		return weights;
	}
	
}
