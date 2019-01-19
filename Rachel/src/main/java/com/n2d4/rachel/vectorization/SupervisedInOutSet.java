package com.n2d4.rachel.vectorization;

import java.io.Serializable;
import java.util.Arrays;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;

public class SupervisedInOutSet implements Debuggable, InOutSet<InputSet, OutputSet>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final InputSet inputSet;
	private final OutputSet outputSet;
	private final int size;

	public SupervisedInOutSet(InputSet inputSet, OutputSet outputSet) {
		Requirements.nonNull(inputSet, "input set");
		Requirements.nonNull(outputSet, "output set");
		Requirements.equal(inputSet.getSetCount(), outputSet.getSetCount(), "input set size");
		
		this.inputSet = inputSet;
		this.outputSet = outputSet;
		this.size = inputSet.getSetCount();
	}
	
	
	public InputSet getInputSet() {
		return inputSet;
	}
	
	public OutputSet getOutputSet() {
		return outputSet;
	}
	
	public int getSize() {
		return size;
	}
	
	public int getInputSize() {
		return inputSet.getInputCount();
	}
	
	public int getOutputSize() {
		return outputSet.getOutputCount();
	}
	
	public SupervisedInOutSet getRandomizedBatch(int size) {		// TODO Optimize
		Requirements.largerOrEqual(size, 0, "size");
		Requirements.smallerOrEqual(size, getSize(), "size");
		
		if (size == getSize()) {
			return this;
		}
		
		INDArray inarr = VectorizedData.getINDArray(inputSet).dup();
		INDArray outarr = VectorizedData.getINDArray(outputSet).dup();
		Nd4j.shuffle(Arrays.asList(inarr, outarr), 1);
		inarr = inarr.get(NDArrayIndex.interval(0, size), NDArrayIndex.interval(0, inarr.columns()));
		outarr = outarr.get(NDArrayIndex.interval(0, size), NDArrayIndex.interval(0, outarr.columns()));
		
		return new SupervisedInOutSet(new InputSet(inarr), new OutputSet(outarr));
	}


	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getInputSet()", getInputSet()).add("getOutputSet()", getOutputSet());
		return builder.toString();
	}

}
