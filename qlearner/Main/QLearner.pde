class QLearner {
  private float[][] qtable;
  private float learningRate, discountFactor;
  private float curiosity;
  private int a_prev = -1;
  private int tota_prev = -1;
  private int s_prev = -1;
  private float r_prev = -1;
  private float q_prev = -1;
  
  public QLearner(float learningRate, float discountFactor, float curiosity, int stateCount) {
    qtable = new float[stateCount][];
    this.learningRate = learningRate;
    this.discountFactor = discountFactor;
    this.curiosity = curiosity;
    this.reset();
  }
  
  public int getBestAction(int state, int actions) {
    float qacts[] = getActionValues(state, actions); 
    
    ArrayList<Integer> bestAL = null;
    float bestF = 0;
    for (int i = 0; i < qacts.length; i++) {
      boolean b = false;
      if (bestAL == null || qacts[i] > bestF) {
          bestAL = new ArrayList<Integer>();
          b = true;
      }
      if (b || qacts[i] >= bestF) {
        bestAL.add(i);
        bestF = qacts[i];
      }
    }
    
    int bestA = bestAL.get((int) random(bestAL.size()));
    
    return bestA;
  }
  
  public float getExpectedValue(int state, int actions, int bestAction) {
    return getActionValues(state, actions)[bestAction];
  }
  
  public float[] getActionValues(int state, int actions) {
    float qacts[] = qtable[state];
    if (qacts == null || qacts.length != actions) {
      qacts = new float[actions];
      qtable[state] = qacts;
      for (int i = 0; i < qacts.length; i++) {
        qacts[i] = curiosity;
      }
    }
    return qacts;
  }
  
  public void registerReward(int state, int action, int totalActions, float reward) {
    if (a_prev >= 0) {
      float expectedValue = getExpectedValue(state, totalActions, action);
      getActionValues(s_prev, tota_prev)[a_prev] = (1-learningRate) * q_prev + learningRate * (r_prev + discountFactor * expectedValue);
    }
    
    s_prev = state;
    a_prev = action;
    tota_prev = totalActions;
    q_prev = qtable[state][a_prev];
    r_prev = reward;
  }
  
  public void reset() {
    if (a_prev >= 0) getActionValues(s_prev, tota_prev)[a_prev] = (1-learningRate) * q_prev + learningRate * (r_prev);
    a_prev = tota_prev = s_prev = -1;
    r_prev = q_prev = -1;
  }
  
  
}