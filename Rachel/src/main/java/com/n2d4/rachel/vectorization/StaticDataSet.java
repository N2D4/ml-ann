package com.n2d4.rachel.vectorization;

import java.io.Serializable;
import java.util.Arrays;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;
import com.n2d4.rachel.util.Requirements;

public class StaticDataSet implements DataSet, Debuggable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private TrainingSet trainingSet;
	private ValidationSet validationSet;
	private TestSet testSet;
	private final int inputSize;
	private final int outputSize;
	private final int dataCount;
	
	public StaticDataSet(InputSet inputSet, OutputSet outputSet, double crossValidationSize, double testSize) {
		Requirements.equal(inputSet.getSetCount(), outputSet.getSetCount(), "input set size");
		
		int setCount = inputSet.getSetCount();
		int cvSetSize = Requirements.nonNegative((int) (setCount * crossValidationSize), "cross validation set size");
		int testSetSize = Requirements.nonNegative((int) (setCount * testSize), "test set size");
		int trainingSetSize = Requirements.positive(setCount - cvSetSize - testSetSize, "training set size");
		
		INDArray iarr = VectorizedData.getINDArray(inputSet).dup();
		INDArray oarr = VectorizedData.getINDArray(outputSet).dup();
		Nd4j.shuffle(Arrays.asList(iarr, oarr), 1);
		
		int from = 0;
		TrainingSet trainingSet = new TrainingSet(stprt(iarr, oarr, from, from += trainingSetSize));
		TestSet testSet = testSetSize > 0 ? new TestSet(stprt(iarr, oarr, from, from += testSetSize)) : new TestSet(aliasSet(trainingSet));
		ValidationSet validationSet = cvSetSize > 0 ? new ValidationSet(stprt(iarr, oarr, from, from += cvSetSize)) : new ValidationSet(aliasSet(trainingSet));
		
		this.trainingSet = trainingSet;
		this.validationSet = validationSet;
		this.testSet = testSet;
		this.inputSize = inputSet.getInputCount();
		this.outputSize = outputSet.getOutputCount();
		this.dataCount = inputSet.getSetCount();
	}
	
	public StaticDataSet(InputSet inputSet, OutputSet outputSet) {
		this(inputSet, outputSet, 0.2d, 0.2d);
	}

	
	private static final InOutSet<InputSet, OutputSet> stprt(INDArray iarr, INDArray oarr, int from, int to) {
		INDArrayIndex ind = NDArrayIndex.interval(from, to);
		INDArrayIndex all = NDArrayIndex.all();
		final InputSet newIn = new InputSet(iarr.get(ind, all));
		final OutputSet newOut = new OutputSet(oarr.get(ind, all));
		
		return new AbstractLayerOutputSet<InputSet, OutputSet>(newIn, newOut);
	}
	
	private static final InOutSet<InputSet, OutputSet> aliasSet(InOutSet<InputSet, OutputSet> orig) {
		return new AbstractLayerOutputSet<InputSet, OutputSet>(new InputSet(VectorizedData.getINDArray(orig.getInputSet())), new OutputSet(VectorizedData.getINDArray(orig.getOutputSet())));
	}
	
	
	
	
	
	
	@Override
	public TrainingSet getTrainingSet() {
		return trainingSet;
	}
	
	@Override
	public ValidationSet getValidationSet() {
		return validationSet;
	}
	
	@Override
	public TestSet getTestSet() {
		return testSet;
	}
	
	@Override
	public int getInputSize() {
		return inputSize;
	}
	
	@Override
	public int getOutputSize() {
		return outputSize;
	}
	
	public int getDataSize() {
		return dataCount;
	}
	
	
	
	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getTrainingSet()", getTrainingSet()).add("getValidationSet()", getValidationSet())
				.add("getTestSet()", getTestSet());
		return builder.toString();
	}
	
}
