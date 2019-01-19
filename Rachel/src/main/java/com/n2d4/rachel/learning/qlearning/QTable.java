package com.n2d4.rachel.learning.qlearning;

import java.util.Map;
import java.util.Map.Entry;

public interface QTable<State, Action> {
	public default void store(State state, Map<Action, Double> values) {
		for (Entry<Action, Double> entry : values.entrySet()) {
			store(state, entry.getKey(), entry.getValue());
		}
	}
	public void store(State state, Action action, double value);
	
	
	
	
	public Map<Action, Double> recall(State state);
	public default double recall(State state, Action action) {
		return recall(state).get(action);
	}
}
