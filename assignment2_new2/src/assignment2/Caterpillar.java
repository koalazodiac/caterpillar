package assignment2;

import assignment2.food.*;

import java.awt.*;
import java.util.Random;

public class Caterpillar {
	// All the fields have been declared public for testing purposes
	public Segment head;
	public Segment tail;
	public int length;
	public EvolutionStage stage;

	public MyStack<Position> positionsPreviouslyOccupied;
	public int goal;
	public int turnsNeededToDigest;


	public static Random randNumGenerator = new Random(1);


	// Creates a Caterpillar with one Segment. It is up to students to decide how to implement this.
	public Caterpillar(Position p, Color c, int goal) {
		head = new Segment(p,c);
		tail = head;
		this.goal = goal;
		length = 1;
		stage = EvolutionStage.FEEDING_STAGE;
		positionsPreviouslyOccupied = new MyStack<>();

	}

	public EvolutionStage getEvolutionStage() {
		return this.stage;
	}

	public Position getHeadPosition() {
		return this.head.position;
	}

	public int getLength() {
		return this.length;
	}


	// returns the color of the segment in position p. Returns null if such segment does not exist
	public Color getSegmentColor(Position p) {
		Segment current = head;
		for (int i = 0; i < length; i++){
			if (current.position.equals(p)){
				return current.color;
			}
			else{
				current = current.next;
			}
		}
		return null;
	}


	// Methods that need to be added for the game to work
	public Color[] getColors(){
		Color[] cs = new Color[this.length];
		Segment chk = this.head;
		for (int i = 0; i < this.length; i++){
			cs[i] = chk.color;
			chk = chk.next;
		}
		return cs;
	}

	public Position[] getPositions(){
		Position[] ps = new Position[this.length];
		Segment chk = this.head;
		for (int i = 0; i < this.length; i++){
			ps[i] = chk.position;
			chk = chk.next;
		}
		return ps;
	}


	// shift all Segments to the previous Position while maintaining the old color
	// the length of the caterpillar is not affected by this
	public void move(Position p) {
		Position[] positions = this.getPositions();
		if ((getHeadPosition().getX()+1 == p.getX() && getHeadPosition().getY() == p.getY()) || (getHeadPosition().getX()-1 == p.getX() && getHeadPosition().getY() == p.getY()) || (getHeadPosition().getX() == p.getX() && getHeadPosition().getY()+1 == p.getY()) || (getHeadPosition().getX() == p.getX() && getHeadPosition().getY()-1 == p.getY())) {
			for (int i = 0; i < positions.length-1; i++) {
				if (positions[i].equals(p)) {
					stage = EvolutionStage.ENTANGLED;
					return;
				}
			}
			Segment current = head;
			Position temp = p;
			for (int i = 0; i < length; i++){
				if (i == length-1){
					tail = current;
				}
				Position copy = temp;
				temp = current.position;
				current.position = copy;
				current = current.next;
				if (i == length-1){
					positionsPreviouslyOccupied.push(temp);
				}
			}
//			if (stage == EvolutionStage.BUTTERFLY){
//				return;
//			}
			if (turnsNeededToDigest ==0){
				stage = EvolutionStage.FEEDING_STAGE;
			}
			if (turnsNeededToDigest > 0 && stage == EvolutionStage.GROWING_STAGE){
				if (head.position.equals(positionsPreviouslyOccupied.peek())){
					return;
				}
				Position temp2 = positionsPreviouslyOccupied.pop();
                tail.next = new Segment(temp2, GameColors.SEGMENT_COLORS[randNumGenerator.nextInt(5)]);
				tail = tail.next;
				turnsNeededToDigest -= 1;
				length+=1;
				if (length+1 >= goal){
					stage = EvolutionStage.BUTTERFLY;
				}
//				else {
//					stage = EvolutionStage.GROWING_STAGE;
//					length+=1;
//					turnsNeededToDigest -= 1;

//				}
			}
		}
		else {
			throw new IllegalArgumentException();
		}
	}



	// a segment of the fruit's color is added at the end
	public void eat(Fruit f) {
		if (stage != EvolutionStage.FEEDING_STAGE) {
			return;
		}
		Position p = positionsPreviouslyOccupied.pop();
		Color c = f.getColor();
		Segment temp = new Segment(p,c);
		tail.next = temp;
		tail = tail.next;
		length+=1;
		if (length >= goal){
			stage = EvolutionStage.BUTTERFLY;
		}
	}


	// the caterpillar moves one step backwards because of sourness
	public void eat(Pickle p) {
		if (stage != EvolutionStage.FEEDING_STAGE) {
			return;
		}
		Segment current = head;
		if (length == 1){
			current.position = positionsPreviouslyOccupied.pop();
			return;
		}
		Position temp = head.next.position;
		for (int i = 0; i < length; i++){
			if (i == length-1){
				current.position = positionsPreviouslyOccupied.pop();
			}
			else {
				Position copy = temp;
				temp = current.position;
				current.position = copy;
				current = current.next;
			}
		}
	}


	// all the caterpillar's colors shuffle around
	public void eat(Lollipop lolly) {
		if (stage != EvolutionStage.FEEDING_STAGE) {
			return;
		}
		Color[] colors = getColors();
		Segment current = head;
		for (int i = length-1; i>0; i--){
			int j = randNumGenerator.nextInt(i+1);
			Color temp = colors[i];
			colors[i] = colors[j]; //not sure if getColors()'s array values are reference values
			colors[j] = temp;
		}
		for (int i = 0; i< length; i++){
			current.color = colors[i];
			current=current.next;
		}
	}

	// brain freeze!!
	// It reverses and its (new) head turns blue
	public void eat(IceCream gelato) {
		if (stage != EvolutionStage.FEEDING_STAGE) {
			return;
		}
		positionsPreviouslyOccupied.clear();
		Segment current = head;
		Segment prev = null;
		for (int i = 0; i < length; i++){
			if (i == 0){
				tail = current;
			}else if (i == length-1){
				head = current;
			}
			Segment temp = current.next;
			current.next = prev;
			prev = current;
			current = temp;

		}
		head.color = GameColors.BLUE;
	}


	// the caterpillar embodies a slide of Swiss cheese loosing half of its segments.
	public void eat(SwissCheese cheese) {
		if (stage != EvolutionStage.FEEDING_STAGE) {
			return;
		}
		Color[] colors = getColors();
		Position[] positions = getPositions();
		Segment current = head;
		for (int i = 0; i < (length+1)/2; i++){
			if (i == (length+1)/2-1){
				current.next = null;
				tail = current;
			}
			current.color = colors[2*i];
			current = current.next;
		}
		for (int i = length-1; i >= (length+1)/2; i--){
			positionsPreviouslyOccupied.push(positions[i]);
		}

		this.length = (length+1)/2;


	}



	public void eat(Cake cake) {
		if (stage != EvolutionStage.FEEDING_STAGE) {
			return;
		}
		stage = EvolutionStage.GROWING_STAGE;
		int energy = cake.getEnergyProvided();

		for (int i = 0; i < energy; i++){
			if (positionsPreviouslyOccupied.empty()){
				turnsNeededToDigest = energy - i;
				return;
			}
			Position positionadd = positionsPreviouslyOccupied.peek();
			Segment current = head;
			for (int j = 0; j < length-1; j++){
				if (positionadd.equals(current.position)){
					turnsNeededToDigest = energy - i;
					return;
				}
				current = current.next;
			}

			if (positionadd.equals(tail.position)){
				turnsNeededToDigest = energy - i;
				return;
			}
			int random = randNumGenerator.nextInt(5);
			positionadd = positionsPreviouslyOccupied.pop();
			Segment newsegment = new Segment(positionadd, GameColors.SEGMENT_COLORS[random]);
			current.next = newsegment;
			tail = newsegment;
			length++;
			if (length == goal){
				stage = EvolutionStage.BUTTERFLY;
				return;
			}
		}
		stage = EvolutionStage.FEEDING_STAGE;
	}



	// This nested class was declared public for testing purposes
	public class Segment {
		private Position position;
		private Color color;
		private Segment next;

		public Segment(Position p, Color c) {
			this.position = p;
			this.color = c;
		}

	}


	public String toString() {
		Segment s = this.head;
		String snake = "";
		while (s!=null) {
			String coloredPosition = GameColors.colorToANSIColor(s.color) +
					s.position.toString() + GameColors.colorToANSIColor(Color.WHITE);
			snake = coloredPosition + " " + snake;
			s = s.next;
		}
		return snake;
	}



	public static void main(String[] args) {


		Position startingPoint = new Position(3, 2);

		Caterpillar gus = new Caterpillar(startingPoint, GameColors.GREEN, 10);
		System.out.println("1) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(3,1));
		gus.eat(new Fruit(GameColors.RED));
		gus.move(new Position(2,1));
		gus.move(new Position(1,1));
		gus.eat(new Fruit(GameColors.YELLOW));

		System.out.println("\n2) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(1,2));
		gus.eat(new IceCream());

		System.out.println("\n3) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);
		gus.move(new Position(3,1));
		gus.move(new Position(3,2));
		gus.eat(new Fruit(GameColors.ORANGE));


		System.out.println("\n4) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(2,2));
		gus.eat(new SwissCheese());

		System.out.println("\n5) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);

		gus.move(new Position(2, 3));
		gus.eat(new Cake(4));

		System.out.println("\n6) Gus: " + gus);
		System.out.println("Stack of previously occupied positions: " + gus.positionsPreviouslyOccupied);
	}
}
