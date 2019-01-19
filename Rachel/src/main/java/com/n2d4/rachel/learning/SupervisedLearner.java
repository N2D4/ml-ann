package com.n2d4.rachel.learning;


import com.n2d4.rachel.util.DebugStringBuilder;
import com.n2d4.rachel.util.Requirements;
import com.n2d4.rachel.util.Util;
import com.n2d4.rachel.util.DebugStringBuilder.Debuggable;
import com.n2d4.rachel.vectorization.CostSet;
import com.n2d4.rachel.vectorization.DataSet;
import com.n2d4.rachel.vectorization.InputSet;
import com.n2d4.rachel.vectorization.OutputSet;
import com.n2d4.rachel.vectorization.SupervisedInOutSet;
import com.n2d4.rachel.vectorization.TrainingSet;

public abstract class SupervisedLearner<StateType extends ExperimentState> implements Debuggable {
	
	private final CostFunction costFunction;
	private final ActivationFunction activationFunction;
	private final OptimizationFunction optimizationFunction;
	private final WeightApplyFunction weightApplyFunction;
	private StateType experimentState;
	private long seed;
	private DataSet dataSet;
	
	public abstract OutputSet process(InputSet input);
	protected abstract void onTrain(SupervisedInOutSet supervisedInOutSet);
	protected abstract CostSet getCost(CostFunction function, SupervisedInOutSet set, OutputSet actualOutput);
	protected abstract StateType getNewState(long seed);
	
	
	public SupervisedLearner(CostFunction costFunction, ActivationFunction activationFunction, OptimizationFunction optimizationFunction, WeightApplyFunction weightApplyFunction, DataSet dataSet) {
		this.costFunction = Requirements.nonNull(costFunction, "cost function");
		this.optimizationFunction = Requirements.nonNull(optimizationFunction, "optimization function");
		this.activationFunction = Requirements.nonNull(activationFunction, "activation function");
		this.weightApplyFunction = Requirements.nonNull(weightApplyFunction, "weight apply function");
		this.dataSet = Requirements.nonNull(dataSet, "data set");
	}
	
	
	public CostFunction getCostFunction() {
		return costFunction;
	}
	
	public ActivationFunction getActivationFunction() {
		return activationFunction;
	}
	
	public OptimizationFunction getOptimizationFunction() {
		return optimizationFunction;
	}
	
	public WeightApplyFunction getWeightApplyFunction() {
		return weightApplyFunction;
	}
	
	public DataSet getDataSet() {
		return dataSet;
	}
	
	public int getInputSize() {
		return getDataSet().getInputSize();
	}
	
	public int getOutputSize() {
		return getDataSet().getOutputSize();
	}
	
	public CostSet getCost(CostFunction function, SupervisedInOutSet set) {
		return getCost(function == null ? getCostFunction() : function, set, process(set.getInputSet()));
	}
	
	public CostSet getCost(SupervisedInOutSet set) {
		return getCost(null, set);
	}
	
	public CostSet getError(CostFunction function) {
		return getCost(function, getDataSet().getTestSet());
	}
	
	public CostSet getError() {
		return getError(null);
	}
	
	public CostSet getValidationError(CostFunction function) {
		return getCost(function, getDataSet().getValidationSet());
	}
	
	public CostSet getValidationError() {
		return getValidationError(null);
	}
	
	public CostSet getTrainingError(CostFunction function) {
		return getCost(function, getDataSet().getTrainingSet());
	}
	
	public CostSet getTrainingError() {
		return getTrainingError(null);
	}
	
	public OutputSet processTestSet() {
		return process(getDataSet().getTestSet().getInputSet());
	}
	
	public StateType getState() {
		if (experimentState == null) this.recreateDefaultState();
		return experimentState;
	}
	
	public OptimizationFunctionData getOptimizationData() {
		return getState().getOptimizationData();
	}
	
	public void restoreState(StateType state) {
		Requirements.nonNull(state, "state");
		this.experimentState = state;
	}
	
	public StateType restoreDefaultState() {
		restoreState(getNewState(seed));
		return getState();
	}
	
	public StateType recreateDefaultState() {
		seed = Util.getRandom().nextLong() ^ System.currentTimeMillis();
		return restoreDefaultState();
	}
	
	
	
	public final void train() {
		train(getDataSet().getTrainingSet());
	}
	
	public final void train(int batchSize) {
		train(getDataSet().getTrainingSet(), batchSize);
	}
	
	public final void train(TrainingSet trainingSet) {
		train(trainingSet, trainingSet.getSize());
	}
	
	public final void train(TrainingSet trainingSet, int batchSize) {
		Requirements.positive(batchSize, "batch size");
		Requirements.smallerOrEqual(batchSize, trainingSet.getSize(), "batch size");

		onTrain(getDataSet().getTrainingSet().getRandomizedBatch(batchSize));
		getState().increaseIterationCount();
	}
	
	

	
	public int getIterationCount() {
		return getState().getIterationCount();
	}
	
	
	
	@Override
	public String toString() {
		DebugStringBuilder builder = new DebugStringBuilder(this);
		builder.add("getIterationCount()", getIterationCount()).add("getCostFunction()", getCostFunction());
		return builder.toString();
	}
	
}
