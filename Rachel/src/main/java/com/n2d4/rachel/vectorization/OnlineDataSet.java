package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.util.Requirements;

public class OnlineDataSet implements ContinuousDataSet {
	
	private final int setCount;
	private final InputSet inputSet;
	private final OutputSet outputSet;
	private int counter = 0;
	
	public OnlineDataSet(int inputSize, int outputSize, int setCount) {
		this.setCount = setCount;
		
		inputSet = new InputSet(Nd4j.randn(getSetCount(), inputSize));
		outputSet = new OutputSet(Nd4j.randn(getSetCount(), outputSize));
	}
	
	@Override public TrainingSet getTrainingSet() {
		return new TrainingSet(inputSet, outputSet);
	}
	
	@Override public ValidationSet getValidationSet() {
		return new ValidationSet(inputSet, outputSet);
	}
	
	@Override public TestSet getTestSet() {
		return new TestSet(inputSet, outputSet);
	}
	
	@Override public int getInputSize() {
		return inputSet.getSetSize();
	}

	@Override public int getOutputSize() {
		return outputSet.getSetSize();
	}
	
	public int getSetCount() {
		return setCount;
	}
	
	public void add(double[] input, double[] output) {
		Requirements.equal(input.length, getInputSize(), "input length");
		Requirements.equal(output.length, getOutputSize(), "output length");
		
		VectorizedData.getINDArray(inputSet).putRow(counter, Nd4j.create(input));
		VectorizedData.getINDArray(outputSet).putRow(counter, Nd4j.create(output));
		counter = ++counter % getSetCount();
	}

	public void add(double[] input, int position, double output) {
		Requirements.equal(input.length, getInputSize(), "input length");
		Requirements.nonNegative(position, "output position");
		Requirements.smallerThan(position, getOutputSize(), "output position");
		
		VectorizedData.getINDArray(inputSet).putRow(counter, Nd4j.create(input));
		double[] doutput = new double[getOutputSize()];
		doutput[position] = output;
		VectorizedData.getINDArray(outputSet).putRow(counter, Nd4j.create(doutput));
		doutput[position] = 1;
		VectorizedData.getINDArray(outputSet.getMixer()).putRow(counter, Nd4j.create(doutput));
		counter = ++counter % getSetCount();
	}
}