package com.n2d4.rachel.vectorization;

public interface DataSet {
	public TrainingSet getTrainingSet();
	public ValidationSet getValidationSet();
	public TestSet getTestSet();
	public int getInputSize();
	public int getOutputSize();
}
