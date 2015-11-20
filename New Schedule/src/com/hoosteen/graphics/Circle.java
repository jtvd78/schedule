package com.hoosteen.graphics;

import java.awt.Graphics;

/**
 * Its a circle. It has an (X,Y) coordinate, and a radius. 
 * @author Justin
 *
 */
public class Circle {
	
	int x;
	int y;
	int radius;
	
	public Circle(int x, int y, int radius){
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	public boolean contains(int x, int y){
		if(Math.pow((this.x - x), 2) + Math.pow((this.y -y),2) > Math.pow(radius,2)){
			return false;
		}
		return true;
	}	
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getRadius() {
		return radius;
	}

	/**
	 * Draws the circle
	 * @param Graphics object to draw on
	 */
	public void draw(Graphics g) {
		g.fillOval(x - radius, y - radius,radius*2, radius*2);
	}
}