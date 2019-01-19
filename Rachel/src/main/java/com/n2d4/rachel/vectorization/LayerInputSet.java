package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.vectorization.derivatives.LayerInputDerivative;

public final class LayerInputSet extends LayerSet implements InputValueSet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private LayerInputSet(ValueSet set, boolean _PRIVATE_) {
		super(addLeadingOnes(set));
	}

	public LayerInputSet(InputSet set) {
		this(set, true);
	}

	public LayerInputSet(LayerOutputSet set) {
		this(set, true);
	}

	public int getInputSize() {
		return super.getSetSize() - 1;
	}
	
	protected static final INDArray addLeadingOnes(ValueSet set) {
		INDArray ones = Nd4j.ones(set.getSetCount(), 1);
		return Nd4j.concat(1, ones, VectorizedData.getINDArray(set));
	}

	@Override
	public int getInputCount() {
		return getSetSize();
	}
	
	public LayerInputDerivative getLayerInputSetDerivative() {
		int[] shape = getValues().shape();
		shape[1]--;
		return new LayerInputDerivative(Nd4j.ones(shape));
	}
	
	public boolean hasIgnorableDerivative() {
		return true;
	}

}
