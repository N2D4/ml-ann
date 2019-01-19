package com.n2d4.rachel.learning.regression;

import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.ActivationFunction;
import com.n2d4.rachel.learning.OptimizationFunction;
import com.n2d4.rachel.vectorization.DataSet;

public class LinearRegression extends Regression {

	public LinearRegression(DataSet dataSet, double learningRate) {
		this(CostFunction.HALF_SQUARED, dataSet, learningRate);
	}
	
	public LinearRegression(CostFunction costFunction, DataSet dataSet, double learningRate) {
		this(costFunction, OptimizationFunction.GRADIENT_DESCENT(learningRate), dataSet);
	}

	public LinearRegression(CostFunction costFunction, OptimizationFunction optimizationFunction, DataSet dataSet) {
		super(costFunction, ActivationFunction.LINEAR, optimizationFunction, dataSet);
	}

}
