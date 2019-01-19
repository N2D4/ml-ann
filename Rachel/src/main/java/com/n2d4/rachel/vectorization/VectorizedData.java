package com.n2d4.rachel.vectorization;

import java.io.Serializable;

import org.nd4j.linalg.api.buffer.DataBuffer.Type;
import org.nd4j.linalg.api.buffer.util.DataTypeUtil;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;
import com.n2d4.rachel.util.Requirements;

public abstract class VectorizedData implements Debuggable, Serializable {
	private static final long serialVersionUID = 1L;
	
	private final INDArray values;
	
	static {
		DataTypeUtil.setDTypeForContext(Type.DOUBLE);
	}

	protected VectorizedData(int size) {
		Requirements.nonNegative(size, "size");
		
		values = Nd4j.create(size);
	}
	
	protected VectorizedData(INDArray values) {
		Requirements.nonNull(values, "values");
		
		this.values = values;
	}
	
	protected VectorizedData(double[]... values) {
		this.values = Nd4j.create(values);
	}
	
	protected int getRowCount() {
		return getSize(0);
	}
	
	protected int getColumnCount() {
		return getSize(1);
	}
	
	protected int getZSize() {
		return getSize(2);
	}
	
	protected int getSize(int dimension) {
		return values.size(dimension);
	}
	
	protected INDArray getValues() {
		return values;
	}
	
	
	
	public static INDArray getINDArray(VectorizedData data) {
		return data.getValues();
	}

	@Override
	public String toString() {
		return buildDebugBuilder().toString();
	}
	
	protected DebugStringBuilder buildDebugBuilder() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getRowCount()", getRowCount()).add("getColumnCount()", getColumnCount()).add("getValues()",
				getValues());
		return builder;
	}

}
