package com.n2d4.rachel.learning.qlearning;

import java.util.HashMap;
import java.util.Map;

import com.n2d4.rachel.learning.neuralnetwork.NeuralNetwork;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.OnlineDataSet;

public class NeuralQTable implements QTable<double[], Integer> {
	
	protected final int trainEvery;
	private final NeuralNetwork network;
	private final OnlineDataSet dataSet;
	
	private int iterations = 0;

	public NeuralQTable(int inputSize, int outputCount, int setSize, int trainEvery, double learningRate, int... hiddenLayerSizes) {
		this.dataSet = new OnlineDataSet(inputSize, outputCount, setSize);
		this.network = new NeuralNetwork(dataSet, learningRate, hiddenLayerSizes);
		this.trainEvery = trainEvery;
	}

	@Override
	public void store(double[] state, Integer action, double value) {
		dataSet.add(state, action, value);
		if (++iterations >= dataSet.getSetCount() && iterations % trainEvery == 0) {
			network.train();
		}
	}

	@Override
	public Map<Integer, Double> recall(double[] state) {
		double[] result = network.process(new InputSet(state)).getSet(0);
		Map<Integer, Double> resultMap = new HashMap<Integer, Double>();
		for (int i = 0; i < result.length; i++) {
			resultMap.put(i, result[i]);
		}
		return resultMap;
	}
	
	
	public NeuralNetwork getNetwork() {
		return network;
	}

}
