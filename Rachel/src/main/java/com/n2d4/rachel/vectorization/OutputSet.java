package com.n2d4.rachel.vectorization;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.vectorization.derivatives.OutputDerivative;

public class OutputSet extends SupervisedSet implements OutputValueSet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final OutputMixer mixer = new OutputMixer();

	public OutputSet(double[]... sets) {
		super(sets);
	}

	public OutputSet(double... set) {
		super(set);
	}

	public OutputSet(LayerOutputSet processLayer) {
		super(VectorizedData.getINDArray(processLayer));
	}

	public OutputSet(INDArray data) {
		super(data);
	}
	
	public OutputMixer getMixer() {
		return mixer;
	}

	@Override
	public int getOutputCount() {
		return getSetSize();
	}
	
	public DataDelta<OutputSet> getDifference(OutputSet actualOutput) {
		DataDelta<OutputSet> result = new DataDelta<OutputSet>(actualOutput, this);
		VectorizedData.getINDArray(result).muli(VectorizedData.getINDArray(getMixer())).muli(VectorizedData.getINDArray(actualOutput.getMixer()));
		return result;
	}
	
	public OutputDerivative getLayerOutputSetDerivative() {
		return new OutputDerivative(Nd4j.ones(getValues().shape()));
	}
	
	public LayerOutputSet asLayerOutputSet() {
		return new LayerOutputSet(VectorizedData.getINDArray(this));
	}
	
	public boolean hasIgnorableDerivative() {
		return true;
	}
	
	
	
	public class OutputMixer extends VectorizedData {			// TODO Rework mixers (important!)
		private static final long serialVersionUID = 1L;
		
		public OutputMixer() {
			super(Nd4j.ones(OutputSet.this.getValues().shape()));
		}
		
		public OutputSet mix(OutputSet actualOutput) {
			INDArray values = getValues();
			return new OutputSet(values.mul(OutputSet.this.getValues()).add(actualOutput.getValues().mul(values.rsub(1))));
		}
	}

}
