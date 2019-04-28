package graphics;

import java.io.Serializable;

import javafx.scene.paint.Color;

public class Player implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3808620236824524793L;
	private int id;
	private double x,y;
	private static final int SIZE = 20;

	public Player(int id, double x, double y){
		this.x = x;
		this.y = y;
		this.id = id;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public int getSize() {
		return SIZE;
	}
	
	public int getId() {
		return id;
	}
	
	public String toString() {
		return "Id : " + id + " :[x:" + x + ", y:" + y + "]";
	}
}
