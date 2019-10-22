// Visibility modifiers are needed because abstract classes and interfaces don't permit default visibility (for a good reason)


private QLearner qlearner;
private World world;


public void setup() {
  size(1200, 800);
  //surface.setResizable(true);
  rectMode(CORNER);
  //pixelDensity(displayDensity());
  
  // First of all, show the menu
  showMenu();
}


public void startSim(int width, int height, float trapChance, float rewardRNG, float rewardToWin, float rewardToLose, float trapReward, float goalReward, float stepReward, float learningRate, float discountFactor, float curiosity) {
  // Initialize the world and the Q-learner
  world = new World(width, height, trapChance, rewardRNG, rewardToWin, rewardToLose, trapReward, goalReward, stepReward);
  qlearner = new QLearner(learningRate, discountFactor, curiosity, world.getStateCount());
}


public int getState() {
  return world.getX() + world.getY() * world.getWidth();
}



/**
/* Stolen from https://processing.org/discourse/beta/num_1219607845.html
*/
public void arrow(float x1, float y1, float x2, float y2) {
  line(x1, y1, x2, y2);
  pushMatrix();
  translate(x2, y2);
  float a = atan2(x1-x2, y2-y1);
  rotate(a);
  line(0, 0, -10, -10);
  line(0, 0, 10, -10);
  popMatrix();
} 