public final GUIElement[] elements = new GUIElement[] {
  new Slider("Speed", 1, 20, 5),
  new Slider("Width", 4, 12, 9),
  new Slider("Height", 4, 12, 6),
  new Slider("Trap Chance", 0, 20, 2, 0.05, "%.2f"),
  new Slider("Reward RNG", 0, 20, 0, 0.25, "%.2f"),
  
  new Slider("Learning Rate", 0, 20, 20, 0.05, "%.2f"),
  new Slider("Discount", 0, 20, 20, 0.05, "%.2f"),
  new Slider("Curiosity", 0, 500, 1),
  
  new Slider("To Win", 1, 40, 4, 25, "%.0f"),
  new Slider("To Lose", -1, -40, -4, 25, "%.0f"),
  new Slider("Trap Punish", 1, 20, 8, 25, "%.0f"),
  new Slider("Goal Reward", 1, 20, 8, 25, "%.0f"),
  new Slider("Move Punish", 0, 15, 1),
  
  new StartButton("START! (space)")
};

private int sinceEnd = -1;
private boolean inMenu = true;
private int verticalSelection;

void draw() {
  if (inMenu) drawMenu();
  else drawWorld();
}


public void showMenu() {
  verticalSelection = elements.length - 1;
  inMenu = true;
  frameRate(30);
}


public void drawMenu() {
  background(0);
  int tot = elements.length * 2 + 1;
  float per = 1f/tot * height;
  int cw = round(width * 0.45f);
  for (int i = 0; i < elements.length; i++) {
    int y = round(per * (2 * i + 1));
    boolean selected = i == verticalSelection;
    elements[i].draw(100, y, width - 140, round(per), cw, round(per * 0.8), selected);
    if (selected) {
      noStroke();
      fill(#FFFFFF);
      beginShape();
      vertex(0, y);
      vertex(80, y + per * 0.5);
      vertex(0, y + per);
      endShape(CLOSE);
    }
  }
}


public void drawWorld() {
  if (sinceEnd >= 0) {
    if (sinceEnd-- == 0) {
      world.reset();
      qlearner.reset();
    }
    return;
  }
  
  background(0);
  
  int[][] av = new int[][] {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
  int state = world.getState();
  int a = qlearner.getBestAction(state, 4);
  float r;
  r = world.step(av[a][0], av[a][1]);
  qlearner.registerReward(state, a, 4, r);
  
  if (world.hasWonOrLost()) sinceEnd = 3;
  
  
  float fieldWidth = width / (float) world.getWidth();
  float fieldHeight = height / (float) world.getHeight();
  for (int i = 0; i < world.getWidth(); i++) {
    for (int j = 0; j < world.getHeight(); j++) {
      stroke(#222222);
      strokeWeight(4);
      fill(getColorForReward(world.getReward(i, j)));
      rect(i * fieldWidth, height - j * fieldHeight - fieldHeight, fieldWidth, fieldHeight);
      
      strokeWeight(1);
      float[] avals = qlearner.getActionValues(world.getState(i, j), 4);
      for (int k = 0; k < avals.length; k++) {
        stroke(getColorForReward(avals[k]));
        float xp = (i + 0.5) * fieldWidth;
        float yp = (j + 0.5) * fieldHeight;
        arrow(xp, height - yp, xp + av[k][0] * 0.4 * fieldWidth, height - (yp + av[k][1] * 0.4 * fieldHeight));
      }
    }
  }
  stroke(#000000);
  strokeWeight(2);
  fill(#FF0000);
  ellipse((world.getX() + 0.5) * fieldWidth, height - (world.getY() + 0.5) * fieldHeight, 0.5 * fieldWidth, 0.5 * fieldHeight);
  
  fill(#FFFFFF);
  textSize(14);
  textAlign(LEFT, TOP);
  text("Reward: " + world.getTotalReward(), 2, 2);
}

private int getColorForReward(float reward) {
  colorMode(RGB);
  float r = abs(reward);
  float c = constrain(20 * (3 * min(r, 2) + 1 * min(r, 5) + 0.2 * r), 0, 255);
  return reward < 0 ? color(c, 0, 0) : color(0, c, 0);
}


public void keyPressed() {
  if (inMenu) {
    if (keyCode == UP || keyCode == DOWN) {
      verticalSelection = (verticalSelection + (keyCode == UP ? -1 : 1)) % elements.length;
      if (verticalSelection < 0) verticalSelection += elements.length;
    } else if (keyCode == LEFT || keyCode == RIGHT) {
      elements[verticalSelection].increment(keyCode == LEFT ? -1 : 1);
    } else if (key == ' ') {
      elements[verticalSelection].action();
    }
  }
}










public abstract class GUIElement {
  public abstract void draw(int x, int y, int width, int height, int columnWidth, int textHeight, boolean selected);
  public void action() {}
  public void increment(int by) {}
}



public class Slider extends GUIElement {
  private final String caption;
  private final int min, max;
  private int value;
  private final float valueScale;
  private final String formatString;
  
  public Slider(String caption, int min, int value, int max) {
    this(caption, min, value, max, 1.0f, "%.0f");
  }
  
  public Slider(String caption, int min, int max, int value, float valueScale, String formatString) {
    this.caption = caption;
    this.min = min;
    this.max = max;
    this.value = value;
    this.valueScale = valueScale;
    this.formatString = formatString;
  }
  
  public void draw(int x, int y, int width, int height, int columnWidth, int textHeight, boolean selected) {
    noStroke();
    fill(selected ? #FFFFFF : #AAAAAA);
    textSize(textHeight);
    textAlign(LEFT, CENTER);
    text(caption + ": " + getValue().toFixed(2), x, y + height / 2);
  }
  
  public void increment(int by) {
    value = constrain(value + by, min, max);
  }
  
  public float getValue() {
    return value * valueScale;
  }
}

public class StartButton extends GUIElement {
  private String caption;
  
  public StartButton(String caption) {
    this.caption = caption;
  }
  
  public void draw(int x, int y, int width, int height, int columnWidth, int textHeight, boolean selected) {
    noStroke();
    fill(selected ? #FFFFFF : #AAAAAA);
    rect(x, y, width, height);
    fill(#000000);
    textAlign(CENTER, BOTTOM);
    textSize(textHeight);
    text(caption, x + width/2, y + height);
  }
  
  public void action() {
    inMenu = false;
    frameRate(ival(0));
    startSim(ival(1), ival(2), fval(3), fval(4), fval(8), fval(9), -fval(10), fval(11), -fval(12), fval(5), fval(6), fval(7));
  }
  
  private int ival(int i) {
    return (int) ((Slider)elements[i]).getValue();
  }
  
  private float fval(int i) {
    return ((Slider)elements[i]).getValue();
  }
}