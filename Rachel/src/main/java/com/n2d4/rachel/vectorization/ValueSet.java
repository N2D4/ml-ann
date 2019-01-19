package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;

import com.n2d4.rachel.util.Requirements;

public abstract class ValueSet extends VectorizedData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected ValueSet(INDArray data) {
		super(data.dup());
	}
	
	protected ValueSet(double[]... data) {
		super(data);
	}
	
	
	
	public int getSetSize() {
		return getColumnCount();
	}
	
	public int getSetCount() {
		return getRowCount();
	}
	
	public double[] getSet(int id) {
		Requirements.nonNegative(id, "id");
		Requirements.smallerThan(id, getSetCount(), "id");
		
		return getValues().getRow(id).data().asDouble();
	}

}
