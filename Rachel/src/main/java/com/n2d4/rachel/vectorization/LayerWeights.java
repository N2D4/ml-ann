package com.n2d4.rachel.vectorization;

import java.util.Random;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import com.n2d4.rachel.learning.ActivationFunction;
import com.n2d4.rachel.learning.WeightApplyFunction;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.util.Util;

public class LayerWeights extends VectorizedData {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LayerWeights(INDArray data) {
		super(data);
	}
	
	public LayerWeights(int inputCount, int outputCount) {
		this(inputCount, outputCount, Util.getRandom());
	}
	
	public LayerWeights(int inputCount, int outputCount, Random random) {
		this(Nd4j.randn(Requirements.positive(inputCount + 1, "input size"), Requirements.positive(outputCount, "output size"), random.nextLong()).muli(1));
	}

	public LayerWeights(double[]... outputs) {
		this(Nd4j.create(Requirements.nonEmpty(outputs, "outputs")).transpose());
	}
	
	
	
	public int getInputSize() {
		return getRowCount() - 1;
	}
	
	public int getOutputSize() {
		return getColumnCount();
	}

	public OutputSet applyOn(InputSet inputSet, WeightApplyFunction weightApplyFunction, ActivationFunction activationFunction) {
		return new OutputSet(activationFunction.apply(weightApplyFunction.getOutput(new LayerInputSet(inputSet), this)));
	}
	
	
	public BiasLackingLayerWeights withoutBias() {
		return new BiasLackingLayerWeights(getValues());
	}

	public void checkInOutSet(InOutSet<?, ?> valueSet) {
		Requirements.equal(valueSet.getInputSet().getInputCount(), this.getInputSize(), "input size");
		Requirements.equal(valueSet.getOutputSet().getOutputCount(), this.getOutputSize(), "output size");
	}
}