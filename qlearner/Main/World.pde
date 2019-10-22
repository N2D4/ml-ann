class World {
  private float[][] rewards;
  private final int width, height;
  private final float trapChance;
  private final float stepReward, goalReward, trapReward;
  private int x, y;
  private float totalReward;
  private final float rewardToWin, rewardToLose;
  private final float rewardRNG;
  
  public World(int width, int height, float trapChance, float rewardRNG, float rewardToWin, float rewardToLose, float trapReward, float goalReward, float stepReward) {
    this.width = width;
    this.height = height;
    rewards = new float[width][height];
    for (int i = 0; i < width; i++) {
      for (int j = 0; j < height; j++) {
        rewards[i][j] = random(1) < trapChance ? trapReward : 0;
      }
    }
    rewards[(int) random(width)][(int) (height/2 + random(height/2))] = goalReward;
    rewards[x][y] = 0;
    this.trapChance = trapChance;
    this.stepReward = stepReward;
    this.goalReward = goalReward;
    this.trapReward = trapReward;
    this.rewardToWin = rewardToWin;
    this.rewardToLose = rewardToLose;
    this.rewardRNG = rewardRNG;
  }
  
  public void reset() {
    x = 0;
    y = 0;
    totalReward = 0;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  public int getState() {
    return getState(getX(), getY());
  }
  
  public int getState(int x, int y) {
    return x * getHeight() + y;
  }
  
  public int getStateCount() {
    return getWidth() * getHeight();
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public float getReward(int x, int y) {
    return rewards[x][y];
  }
  
  public float getTotalReward() {
    return totalReward;
  }
  
  public float getRewardToWin() {
    return rewardToWin;
  }
  
  public float getRewardToLose() {
    return rewardToLose;
  }
  
  public boolean hasWonOrLost() {
    return getTotalReward() >= getRewardToWin() || getTotalReward() <= getRewardToLose();
  }
  
  public float step(int xp, int yp) {
    x = constrain(x + xp, 0, getWidth() - 1);
    y = constrain(y + yp, 0, getHeight() - 1);
    float k = stepReward + getReward(x, y);
    k += k * rewardRNG * randomGaussian();
    
    totalReward += k;
    return k;
  }
  
}