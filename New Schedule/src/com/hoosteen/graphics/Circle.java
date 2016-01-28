package com.hoosteen.graphics;

import java.awt.Graphics;

/**
 * Its a circle. It has an (X,Y) coordinate, and a radius. 
 * @author justin
 *
 */
public class Circle {
	
	int x;
	int y;
	int radius;
	
	/**
	 * 
	 * @param x - X Coordinate of Center
	 * @param y - Y Coordinate of Center
	 * @param radius - Radius of Circle
	 */
	public Circle(int x, int y, int radius){
		this.x = x;
		this.y = y;
		this.radius = radius;
	}
	
	/**
	 * Determines whether a point (x, y) is contained witin this circle
	 * @param x - X coordinate of point
	 * @param y - Y coordinate of point
	 * @return whether or not the circle contains the point
	 */
	public boolean contains(int x, int y){
		if(Math.pow((this.x - x), 2) + Math.pow((this.y -y),2) > Math.pow(radius,2)){
			return false;
		}
		return true;
	}	
	
	/**
	 * @return X value of center
	 */
	public int getX() {
		return x;
	}

	/**
	 * @return Y value of center
	 */
	public int getY() {
		return y;
	}

	/**
	 * @return Radius of circle
	 */
	public int getRadius() {
		return radius;
	}

	/**
	 * Draws the circle
	 * @param g - Graphics object to draw on
	 */
	public void draw(Graphics g) {
		g.fillOval(x - radius, y - radius,radius*2, radius*2);
	}
}