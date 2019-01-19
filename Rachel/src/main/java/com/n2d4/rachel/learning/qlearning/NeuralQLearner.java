package com.n2d4.rachel.learning.qlearning;

import com.n2d4.rachel.learning.neuralnetwork.NeuralNetwork;

public class NeuralQLearner extends QLearner<double[], Integer> {

	public NeuralQLearner(NeuralQTable table, double learningRate, double discountFactor) {
		super(table, learningRate, discountFactor);
	}
	
	
	public NeuralNetwork getNetwork() {
		return ((NeuralQTable) getTable()).getNetwork();
	}

}
