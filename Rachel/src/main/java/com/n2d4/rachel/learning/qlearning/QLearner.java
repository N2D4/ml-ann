package com.n2d4.rachel.learning.qlearning;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class QLearner<State, Action> {
	private QTable<State, Action> table;
	private double learningRate, discountFactor;
	private Action a_prev;
	private State s_prev;
	private double r_prev;
	private double q_prev;

	public QLearner(QTable<State, Action> table, double learningRate, double discountFactor) {
		this.table = table;
		this.learningRate = learningRate;
		this.discountFactor = discountFactor;
		this.resetRound();
	}

	public Action getBestAction(State state) {
		Map<Action, Double> qacts = getActionValues(state); 

		List<Action> bestAL = null;
		double bestF = 0;
		for (Entry<Action, Double> entry : qacts.entrySet()) {
			Double value = entry.getValue();
			boolean b = false;
			if (bestAL == null || value > bestF) {
				bestAL = new ArrayList<Action>();
				b = true;
			}
			if (b || value >= bestF) {
				bestAL.add(entry.getKey());
				bestF = value;
			}
		}

		Action bestA = bestAL.get(ThreadLocalRandom.current().nextInt(bestAL.size()));

		return bestA;
	}

	public double getExpectedValue(State state, Action bestAction) {
		return getActionValues(state).get(bestAction);
	}

	public Map<Action, Double> getActionValues(State state) {
		return table.recall(state);
	}
	
	public double getActionValue(State state, Action action) {
		return table.recall(state, action);
	}
	
	public void setActionValue(State state, Action action, double value) {
		table.store(state, action, value);
	}

	public void registerReward(State state, Action action, double reward) {
		if (a_prev != null) {
			double expectedValue = getExpectedValue(state, action);
			setActionValue(s_prev, a_prev, getNewActionValue(expectedValue));
		}

		s_prev = state;
		a_prev = action;
		q_prev = getActionValue(state, action);
		r_prev = reward;
	}

	public void resetRound() {
		if (a_prev != null) setActionValue(s_prev, a_prev, getNewActionValue());
		s_prev = null;
		a_prev = null;
		r_prev = q_prev = -1;
	}
	

	
	protected QTable<State, Action> getTable() {
		return table;
	}
	
	
	private double getNewActionValue() {
		return getNewActionValue(0);
	}
	
	private double getNewActionValue(double expectedValue) {
		return (1-learningRate) * q_prev + learningRate * (r_prev + discountFactor * expectedValue);
	}


}