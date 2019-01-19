package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.NDArrayIndex;

import com.n2d4.rachel.util.Requirements;

public abstract class UnrolledData extends VectorizedData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final VectorizedData[] refs;
	
	public UnrolledData(VectorizedData... from) {
		super(unrollData(from));
		this.refs = from;
	}
	
	private final static INDArray unrollData(VectorizedData... from) {
		Requirements.positive(from.length, "data source size");
		
		INDArray result = linarr(from[0]);
		for (int i = 1; i < from.length; i++) {
			result = Nd4j.concat(0, result, linarr(from[i]));
		}
		
		return result;
	}
	
	private final static INDArray linarr(VectorizedData from) {
		INDArray result = VectorizedData.getINDArray(from).dup();
		return result.reshape(result.length(), 1);
	}
	
	
	public int getDataLength() {
		return getRowCount();
	}
	
	
	public void reshapeOriginals() {
		int pos = 0;
		for  (VectorizedData data : refs) {
			INDArray arr = VectorizedData.getINDArray(data);
			int[] shape = arr.shape();
			int npos = pos + arr.length();
			INDArray narr = getValues().get(NDArrayIndex.interval(pos, npos)).reshape(shape);
			Nd4j.copy(narr, arr);
			pos = npos;
		}
	}
	
}
