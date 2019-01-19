package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.ops.transforms.Transforms;

import com.n2d4.rachel.util.DebugStringBuilder;

public class CostSet extends VectorizedData {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CostSet(INDArray arr) {
		super(arr);
	}

	public CostSet(double... values) {
		super(values);
	}

	public int getSize() {
		return this.getColumnCount();
	}
	
	public double getRMS() {
		return Math.sqrt(Transforms.pow(getValues(), 2).meanNumber().doubleValue());
	}
	
	@Override
	public DebugStringBuilder buildDebugBuilder() {
		return super.buildDebugBuilder().add("getRMS()", getRMS());
	}
	
}
