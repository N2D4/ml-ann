package com.n2d4.rachel.learning.regression;

import com.n2d4.rachel.learning.CostFunction;
import com.n2d4.rachel.learning.ActivationFunction;
import com.n2d4.rachel.learning.OptimizationFunction;
import com.n2d4.rachel.vectorization.DataSet;

public class LogisticRegression extends Regression {

	public LogisticRegression(DataSet dataSet, double learningRate) {
		this(CostFunction.LOGARITHMIC, dataSet, learningRate);
	}
	
	public LogisticRegression(CostFunction costFunction, DataSet dataSet, double learningRate) {
		this(costFunction, OptimizationFunction.GRADIENT_DESCENT(learningRate), dataSet);
	}

	public LogisticRegression(CostFunction costFunction, OptimizationFunction optimizationFunction, DataSet dataSet) {
		super(costFunction, ActivationFunction.SIGMOID, optimizationFunction, dataSet);
	}

}
