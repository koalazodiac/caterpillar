package assignment2.game;
import assignment2.Caterpillar;
import assignment2.Position;
import assignment2.food.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameCanvas extends Canvas {
    final static String dir = System.getProperty("user.dir")+"/src/assignment2/game/assets/";
    final static List<String> imagesFiles = Arrays.asList("Apple.png", "Banana.png", "Orange.png", "Pear.png", "Plum.png",
            "Cake.png", "IceCream.png", "Lollipop.png", "Pickle.png", "SwissCheese.png", "RedHead.png",
            "YellowHead.png", "OrangeHead.png", "GreenHead.png", "BlueHead.png", "Butterfly.png");

    final int fruitImageOffset = 4, foodImageOffset = 10;
    final int red = 0, yellow = 1, orange = 2, green = 3, blue = 4;
    static List<ImageIcon> IMAGES;

    Map map;

    Caterpillar caterpillar;

    private boolean mapGenerated = false;


    public GameCanvas(int canvasX, int canvasY, int canvasWidth, int canvasHeight) {
        //this.setBackground();

        this.setBounds(canvasX, canvasY, canvasWidth, canvasHeight);

        IMAGES = new ArrayList<>();
        for (String path : imagesFiles) {
            String full_path = dir+path;
            ImageIcon icon = new ImageIcon(full_path);
            if (icon.getIconWidth() == -1) {
                System.out.println("Failed to load image: " + full_path);
            } else {
                IMAGES.add(icon);
            }
        }
    }

    public void registerMap(Map m) {
        this.map = m;
    }

    public void registerCaterpillar(Caterpillar c){
        this.caterpillar = c;
    }

    // paint is called automatically every turn
    // update map based on map object status
    @Override
    public void paint(Graphics g) {
        // TODO Auto-generated method stub
        super.paint(g);

        if (map == null) return;

        int size = map.getBoxSize();
        FoodItem[][] board = map.getBoard();

        if (map.shouldDrawButterfly()){
            int width = board.length*size, height = board[0].length*size;
            g.drawImage(IMAGES.get(IMAGES.size()-1).getImage(), 1, 1, width, height, null);
            return;
        }

        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                int x = size * i + 1;
                int y = size * j + 1;

                if (board[i][j] != null) {
                    if (board[i][j] instanceof Cake) {
                        g.setColor(Color.black);
                        g.drawRect(x, y, size, size);
                        g.setColor(Color.pink);
                        g.fillRect(x, y, size, size);

                        g.drawImage(IMAGES.get(fruitImageOffset+FoodEnum.Cake.ordinal()).getImage(), x, y, size, size, null);
                    }
                    else if (board[i][j] instanceof Fruit) {
                        g.setColor(Color.black);
                        g.drawRect(x, y, size, size);
                        g.setColor(Color.red);
                        g.fillRect(x, y, size, size);

                        ImageIcon fruitImage;
                        Color c = ((Fruit) board[i][j]).getColor();
                        if (c.equals(GameColors.RED)) fruitImage = IMAGES.get(red);
                        else if (c.equals(GameColors.YELLOW)) fruitImage = IMAGES.get(yellow);
                        else if (c.equals(GameColors.ORANGE)) fruitImage = IMAGES.get(orange);
                        else if (c.equals(GameColors.GREEN)) fruitImage = IMAGES.get(green);
                        else if (c.equals(GameColors.BLUE)) fruitImage = IMAGES.get(blue);
                        else {
                            System.out.println("No such color?");
                            continue;
                        }

                        g.drawImage(fruitImage.getImage(), x, y, size, size, null);
                    }
                    else if (board[i][j] instanceof IceCream) {
                        g.setColor(Color.black);
                        g.drawRect(x, y, size, size);
                        g.setColor(Color.gray);
                        g.fillRect(x, y, size, size);

                        g.drawImage(IMAGES.get(fruitImageOffset+FoodEnum.IceCream.ordinal()).getImage(), x, y, size, size, null);
                    }
                    else if (board[i][j] instanceof Lollipop) {
                        g.setColor(Color.black);
                        g.drawRect(x, y, size, size);
                        g.setColor(Color.white);
                        g.fillRect(x, y, size, size);

                        g.drawImage(IMAGES.get(fruitImageOffset+FoodEnum.Lollipop.ordinal()).getImage(), x, y, size, size, null);
                    }
                    else if (board[i][j] instanceof Pickle) {
                        g.setColor(Color.black);
                        g.drawRect(x, y, size, size);
                        g.setColor(Color.green);
                        g.fillRect(x, y, size, size);

                        g.drawImage(IMAGES.get(fruitImageOffset+FoodEnum.Pickle.ordinal()).getImage(), x, y, size, size, null);
                    }
                    else if (board[i][j] instanceof SwissCheese) {
                        g.setColor(Color.black);
                        g.drawRect(x, y, size, size);
                        g.setColor(Color.yellow);
                        g.fillRect(x, y, size, size);

                        g.drawImage(IMAGES.get(fruitImageOffset+FoodEnum.SwissCheese.ordinal()).getImage(), x, y, size, size, null);
                    }
                }
                else {
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, size, size);
                }
            }
        }

        if (caterpillar != null) {
            int len = caterpillar.length;
            Color[] caterpillarColors = caterpillar.getColors();
            Position[] caterpillarPosition = caterpillar.getPositions();

            int headX = size * caterpillarPosition[0].getX() + 1;
            int headY = size * caterpillarPosition[0].getY() + 1;

            ImageIcon caterpillarHead = null;
            if (caterpillarColors[0].equals(GameColors.RED)) caterpillarHead = IMAGES.get(foodImageOffset+red);
            else if (caterpillarColors[0].equals(GameColors.YELLOW)) caterpillarHead = IMAGES.get(foodImageOffset+yellow);
            else if (caterpillarColors[0].equals(GameColors.ORANGE)) caterpillarHead = IMAGES.get(foodImageOffset+orange);
            else if (caterpillarColors[0].equals(GameColors.GREEN)) caterpillarHead = IMAGES.get(foodImageOffset+green);
            else if (caterpillarColors[0].equals(GameColors.BLUE)) caterpillarHead = IMAGES.get(foodImageOffset+blue);
            else {
                System.out.println("No such color?");
                g.setColor(Color.black);
                g.fillRect(headX, headY, size, size);
            }

            if (caterpillarHead != null) g.drawImage(caterpillarHead.getImage(), headX, headY, size, size, null);

            for (int i = 1; i < len; i++) {
                int x = size * caterpillarPosition[i].getX() + 1;
                int y = size * caterpillarPosition[i].getY() + 1;

                g.setColor(caterpillarColors[i]);
                g.fillRect(x, y, size, size);
            }
        }
        else {
//            System.out.println("Caterpillar is null");
        }
    }


}
