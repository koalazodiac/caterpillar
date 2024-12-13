package assignment2.game;
import assignment2.Caterpillar;
import assignment2.Position;
import assignment2.food.*;

import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Arrays;

public class Map {
    private final int DEFAULT_WIDTH = 12, DEFAULT_HEIGHT = 12, MAX_ENERGY = 10;

    private int widthInPixels, heightInPixels, sizePerBox, width, height, foodCount, startingFood;

    private static Random rng = new Random(1);

    private boolean drawButterfly = false;

    private FoodItem[][] board;

    // box size decides map size
    public Map(int w, int h, int food, int size) {
        sizePerBox = size;
        heightInPixels = h;
        widthInPixels = w;
        width = widthInPixels / size;
        height = heightInPixels / size;

        startingFood = food;
        board = new FoodItem[width][height];
    }

    // map size decides box size
    public Map(int w, int h, int food) {
        heightInPixels = h;
        widthInPixels = w;
        width = DEFAULT_WIDTH;
        height = DEFAULT_HEIGHT;
        sizePerBox = Math.min(widthInPixels / width, heightInPixels / height);	//if not proportional then go with width by default

        startingFood = food;
        board = new FoodItem[width][height];
//        System.out.println("Width is " + DEFAULT_WIDTH + ", Height is " + DEFAULT_HEIGHT + ", Size is " + sizePerBox);
    }

    private boolean addFoodItem(ArrayList<Position> positions){
        if (foodCount+positions.size() >= width*height) return false;
        
        int x = -1, y = -1;
        do {
            x = rng.nextInt(width);
            y = rng.nextInt(height);
        } while (positions.contains(new Position(x, y)));

        this.setItemAtMapLocation(x, y, FoodEnum.getFoodEnum(rng.nextInt(6)));
        
        foodCount++;
        return true;
    }

    public int addFoodItems(int count, Caterpillar c){
        int ncount = count;
        ArrayList<Position> positions = new ArrayList<Position>();
        if (c != null) positions.addAll(Arrays.asList(c.getPositions()));
        while ((ncount-- > 0) && addFoodItem(positions)){
        };
        ncount++;
        return count-ncount;
    }

    public boolean build(){
        return addFoodItems(startingFood, null) == startingFood;
    }

    public void reset(){
        board = new FoodItem[width][height];
    }


    // ask canvas to draw the map
    public void draw(GameCanvas canvas) {
        canvas.registerMap(this);
    }

    public FoodItem[][] getBoard() {
        return board;
    }

    public int getBoxSize() {
        return sizePerBox;
    }

    public int[] getMapLocation(int x, int y) {
        int nx = (int) Math.ceil((x) / sizePerBox);
        int ny = (int) Math.ceil((y) / sizePerBox);
        //System.out.println(nx + " " + ny + " " + sizePerBox + " " + x + " " + y);
        return new int[]{nx, ny};
    }

    public FoodItem getItemAtMapLocation(int x, int y) {
        return board[x][y];
    }

    public void cleanItemAtMapLocation(int x, int y){
        board[x][y] = null;
    }

    public void setItemAtMapLocation(int x, int y, FoodEnum f) {
        switch(f){
            case IceCream:
                board[x][y] = new IceCream();
                break;
            case Cake:
                board[x][y] = new Cake(1 + rng.nextInt(MAX_ENERGY+1));
                break;
            case SwissCheese:
                board[x][y] = new SwissCheese();
                break;
            case Fruit:
                board[x][y] = new Fruit(GameColors.SEGMENT_COLORS[rng.nextInt(GameColors.SEGMENT_COLORS.length)]);
                break;
            case Pickle:
                board[x][y] = new Pickle();
                break;
            case Lollipop:
                board[x][y] = new Lollipop();
                break;
        }
    }

    public void drawButterfly(boolean b){
        drawButterfly = b;
    }

    public boolean shouldDrawButterfly(){
        return drawButterfly;
    }
}
