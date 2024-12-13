package assignment2.game;

import assignment2.EvolutionStage;
import assignment2.food.FoodEnum;
import assignment2.food.FoodItem;
import assignment2.food.IceCream;
import assignment2.Caterpillar;
import assignment2.Position;

import java.awt.*;
import java.awt.event.*;
import java.util.Random;

import javax.swing.*;

public class Game extends JFrame {

    private static final int RES_X = 1000, RES_Y = 800;
    
    private static final int STARTING_FOOD = 10, FOOD_ADD_INTERVAL = 10, GOAL = 10;

    private Position delta = new Position(-2, -2);

    private static Random rng = new Random(250);

    private boolean inSelection = false;
    private FoodEnum selectedFood;

    private int canvasX;
    private int canvasY;
    private int canvasWidth;
    private int canvasHeight;
    private int boardWidth;
    private int boardHeight;

    private static GameCanvas boardCanvas;
    private static Label msgPanel;
    private static TextArea logPanel;
    private static Timer tick;
    
    private static Button CakeBtn, FruitBtn, IceCreamBtn, LollipopBtn, PickleBtn, SwissCheeseBtn;
    private static Button startBtn, endBtn, ClearMapBtn;

    private long frameCount;
    private final int timePerFrame = 500;	// 1 second per frame, increase/decrease to slow down/speed up the game,
    private final int butterflyDelay = 5;   // number of frame refreshes before the butterfly flies away
    private boolean gameOver;

    private Caterpillar caterpillar;
    private static Map map;
    private static StringBuffer logBuffer;
    private static boolean canEat = false;

    private final String moveLeft = "moveLeft", moveRight = "moveRight", moveUp = "moveUp", moveDown = "moveDown";

    private class MoveLeft extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            delta = new Position(-1, 0);
        }
    }
    private class MoveRight extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            delta = new Position(1, 0);
        }
    }

    private class MoveUp extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            delta = new Position(0, -1);
        }
    }

    private class MoveDown extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            delta = new Position(0, 1);
        }
    }

    public Game(){
        initUI();
        initGame();

        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("RIGHT"), moveRight);
        inputMap.put(KeyStroke.getKeyStroke("d"), moveRight);
        inputMap.put(KeyStroke.getKeyStroke("LEFT"), moveLeft);
        inputMap.put(KeyStroke.getKeyStroke("a"), moveLeft);
        inputMap.put(KeyStroke.getKeyStroke("UP"), moveUp);
        inputMap.put(KeyStroke.getKeyStroke("w"), moveUp);
        inputMap.put(KeyStroke.getKeyStroke("DOWN"), moveDown);
        inputMap.put(KeyStroke.getKeyStroke("s"), moveDown);

        actionMap.put(moveLeft, new MoveLeft());
        actionMap.put(moveRight, new MoveRight());
        actionMap.put(moveUp, new MoveUp());
        actionMap.put(moveDown, new MoveDown());
    }

    /*************************** Main ***************************/
    public static void main(String args[]){
        Game game = new Game();
    }


    /****************** Game Logic ******************/

    private void initMap() {
        map = new Map(canvasWidth, canvasHeight, STARTING_FOOD);
        if (map.build()) {
            map.draw(boardCanvas);
            FoodItem[][] board = map.getBoard();
            boardWidth = board.length;
            boardHeight = board[0].length;
        }
        else {
            endGame();
            System.out.println("Cannot initialize map");
        }
    }

    private void initGame() {
        frameCount = 0;
        gameOver = true;
        canEat = false;

        //disable and enable UI
        toggleInput(true);
        startBtn.setEnabled(true);
        endBtn.setEnabled(false);

        initMap();

        logBuffer = new StringBuffer();
    }

    private void startGame() {
        if (!gameOver) return;

        gameOver = false;
        canEat = false;

        map.drawButterfly(false);

        toggleInput(false);
        inSelection = false;

//        System.out.println("Caterpillar is spawned at: "+boardWidth/2+", "+boardHeight/2);
        caterpillar = new Caterpillar(new Position(boardWidth/2, boardHeight/2), GameColors.SEGMENT_COLORS[rng.nextInt(GameColors.SEGMENT_COLORS.length)], GOAL);
        boardCanvas.registerCaterpillar(this.caterpillar);

        tick = new Timer(timePerFrame, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameCount++;

                updateUI();
                simulate();
                render();
            }
        });
        tick.start();
    }

    private void endGame() {
        if (gameOver) return;
        gameOver = true;
        render();

        logMessage("GAME OVER!");
        tick.stop();

        if (map.shouldDrawButterfly()) {
            Timer resetCanvas = new Timer(butterflyDelay * timePerFrame, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    remakeMap();
                    resetCaterpillar();
                    updateUI();
                }
            });
            resetCanvas.setRepeats(false);
            resetCanvas.start();
        }
        else {
            remakeMap();
            resetCaterpillar();
            updateUI();
        }

        frameCount = 0;
        delta = new Position(-2, -2);
        toggleInput(true);
    }

    // game logic
    private void simulate() {
        //TODO: simulate the map based on the rule and update map
        logMessage("\n************* Turn " + frameCount + " *************");


        Position currentPosition = caterpillar.getHeadPosition();
        FoodItem f = map.getItemAtMapLocation(currentPosition.getX(), currentPosition.getY());
        
      
        // The caterpillar can eat only if in a FEEDING_STAGE and it has just moved into the cell
        if (f != null && caterpillar.stage == EvolutionStage.FEEDING_STAGE && canEat){
        	// needed if eating an ice cream
            Position lastVisited = caterpillar.positionsPreviouslyOccupied.peek();
            
            
            logMessage("The caterpillar has eaten a "+f.getClass().getSimpleName()+"!");
            f.accept(caterpillar);
            map.cleanItemAtMapLocation(currentPosition.getX(), currentPosition.getY());
            canEat = false;
            
            // the direction, i.e. delta, will need to be updated if the caterpillar eats an IceCream
            if (f instanceof IceCream) {
            	Position newHead = caterpillar.getHeadPosition();
            	delta = new Position(lastVisited.getX()-newHead.getX(), lastVisited.getY()-newHead.getY());
            }
            
        }
        else logMessage("The caterpillar is hungry!");

        if (Math.abs(delta.getX()+delta.getY()) == 1 && (Math.abs(delta.getX()) == 1 || Math.abs(delta.getY()) == 1)){
            int nx = caterpillar.getHeadPosition().getX()+delta.getX();
            int ny = caterpillar.getHeadPosition().getY()+delta.getY();

            if (Math.min(nx, ny) < 0 || nx >= boardWidth || ny >= boardHeight) {
                logMessage("The caterpillar has hit a wall, and has forgotten how to become a butterfly!");
                endGame();
                return;
            }
            else {
            	caterpillar.move(new Position(nx, ny));
            	canEat = true;
            }
        }

        if (caterpillar.stage == EvolutionStage.ENTANGLED){
            logMessage("The caterpillar has been entangled and cannot become a butterfly!");
            endGame();
            return;
        }
        if (caterpillar.stage == EvolutionStage.BUTTERFLY){
            logMessage("The caterpillar has successfully become a butterfly!");
            map.drawButterfly(true);
            endGame();
            return;
        }

        if (frameCount > 0 && frameCount % FOOD_ADD_INTERVAL == 0){
            map.addFoodItems(1, caterpillar);
        }

        UpdateLog();
    }

    private void render() {
        boardCanvas.repaint();
    }

    private int[] calculateLocation(int x, int y) {
        return map.getMapLocation(x, y);
    }

    private void addFoodItem(int[] boardBox) {
        if (selectedFood == null) return;

        int x = boardBox[0], y = boardBox[1];
//        System.out.println("Spawn FoodItem: "+selectedFood);
        map.setItemAtMapLocation(x, y, selectedFood);
        render(); // display addition
    }

    private void updateUI() {
        if (gameOver) {
            startBtn.setEnabled(true);
            endBtn.setEnabled(false);
        }
        else {
            endBtn.setEnabled(true);
            startBtn.setEnabled(false);
        }
    }

    private void remakeMap() {
        initMap();
        render();
    }

    private void resetMap() {
        map.reset();
        boardCanvas.registerMap(map);
        render();
    }

    private void resetCaterpillar() {
        caterpillar = null;
        boardCanvas.registerCaterpillar(null);
    }

    /**************************** Interface *************************/

    // This method is used to log message to panel
    public static void logMessage(String s) {
        logBuffer.append(s+"\n");
        UpdateLog();
    }

    /************************** UI control *********************/

    public void initUI() {
        msgPanel = new Label();
        msgPanel.setBounds(RES_X / 2 -  125, 30, 250,20);
        msgPanel.setAlignment(1);

        // close window on button click
        addWindowListener(new WindowAdapter(){
            public void windowClosing(WindowEvent e) {
                endGame();
                dispose();
            }
        });

        // create canvas
        canvasX = Math.max(RES_X / 20, 150);
        canvasY = Math.max(RES_Y / 20, 50);
        canvasWidth = (int)(RES_X * 0.70);
        canvasHeight = (int)(RES_Y * 0.70);
        boardCanvas = new GameCanvas(canvasX, canvasY, canvasWidth, canvasHeight);
        boardCanvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //if click on the canvas then set inSelection to false
                //System.out.println(e.getX() + " " + e.getY());
                if (inSelection) {
                    int[] loc = calculateLocation(e.getX(), e.getY());
                    addFoodItem(loc);
                }
            }
        });

        // create button panel
        Panel _buttonPanel = new Panel();
        _buttonPanel.setLayout(new BoxLayout(_buttonPanel, BoxLayout.Y_AXIS));
        _buttonPanel.setBounds(RES_X - 150, 150, 140, 3*RES_Y / 5);
        createButtons(_buttonPanel);

        // create log panel
        logPanel = new TextArea();
        logPanel.setSize(400,400);
        logPanel.setBounds(150, RES_Y - 130, 600, 110);
        logPanel.setVisible(true);
        logPanel.setEditable(false);

        add(logPanel);
        add(boardCanvas);
        add(_buttonPanel);
        add(msgPanel);
        setSize(RES_X, RES_Y);				//frame size 300 width and 300 height
        setLayout(null);					//no layout manager
        setVisible(true);					//now frame will be visible, by default not visible
    }

    public void UpdateMessage(String s) {
        msgPanel.setText(s);
    }

    public static void UpdateLog() {
        logPanel.setText(logBuffer.toString());
    }

    private void createButtons(Panel buttonPanel) {

        startBtn = new Button("Start Game");
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startGame();
            }
        });

        endBtn = new Button("End Game");
        endBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endGame();
            }
        });

        CakeBtn = new Button("Cake");
        CakeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFood = FoodEnum.Cake;
                inSelection = true;
                UpdateMessage("You selected Cake");
            }
        });

        FruitBtn = new Button("Fruit");
        FruitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFood = FoodEnum.Fruit;
                inSelection = true;
                UpdateMessage("You selected Fruit");
            }
        });

        IceCreamBtn = new Button("Ice Cream");
        IceCreamBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFood = FoodEnum.IceCream;
                inSelection = true;
                UpdateMessage("You selected Ice Cream");
            }
        });

        LollipopBtn = new Button("Lollipop");
        LollipopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFood = FoodEnum.Lollipop;
                inSelection = true;
                UpdateMessage("You selected Lollipop");
            }
        });

        PickleBtn = new Button("Pickle");
        PickleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFood = FoodEnum.Pickle;
                inSelection = true;
                UpdateMessage("You selected Pickle");
            }
        });

        SwissCheeseBtn = new Button("Swiss Cheese");
        SwissCheeseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectedFood = FoodEnum.SwissCheese;
                inSelection = true;
                UpdateMessage("You selected Swiss Cheese");
            }
        });

        ClearMapBtn = new Button("Clear Map");
        ClearMapBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetMap();
                UpdateMessage("You cleared the map");
            }
        });

        buttonPanel.add(startBtn);
        buttonPanel.add(endBtn);
        buttonPanel.add(new Label(" "));
        buttonPanel.add(CakeBtn);
        buttonPanel.add(FruitBtn);
        buttonPanel.add(IceCreamBtn);
        buttonPanel.add(LollipopBtn);
        buttonPanel.add(PickleBtn);
        buttonPanel.add(SwissCheeseBtn);
        buttonPanel.add(ClearMapBtn);
    }

    private void toggleInput(boolean b) {
        CakeBtn.setEnabled(b);
        FruitBtn.setEnabled(b);
        IceCreamBtn.setEnabled(b);
        LollipopBtn.setEnabled(b);
        PickleBtn.setEnabled(b);
        SwissCheeseBtn.setEnabled(b);
        ClearMapBtn.setEnabled(b);
    }
}