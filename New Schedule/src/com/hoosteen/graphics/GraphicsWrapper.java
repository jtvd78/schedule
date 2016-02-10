package com.hoosteen.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;


/**
 * Makes it easy to perform special operations with a Graphics object. Including drawing rectangles, cirles, and strings
 * @author Justin
 *
 */
public class GraphicsWrapper {
	
	/**
	 * Graphics object on which to draw on
	 */
	protected Graphics g;
	private FontMetrics fm;
	
	
	/**
	 * Creates a new Graphics Wrapper, with a graphics object g
	 * @param g - Graphics object to wrap around
	 */
	public GraphicsWrapper(Graphics g){
		this.g = g;
		fm = g.getFontMetrics();
	}
	
	/**
	 * Draws a string on the graphics object, centered vertically within the rectangle. 
	 * @param s - String to draw
	 * @param r - Rectangle to draw within
	 */
	public void drawString(String s, Rect r){
		
		int y = r.getHeight()/2 + r.getY() - (fm.getAscent() + fm.getDescent())/2 + fm.getAscent();
		g.drawString(s, r.getX() + 5, y);
	}
	
	/**
	 * Draws a string, centered within:
	 * @param s - string to draw
	 * @param r - rect to center within
	 */
	public void drawCenteredString(String s, Rect r){		
		drawCenteredString(s, r.getX() + r.getWidth() / 2, r.getY() + r.getHeight()/2);
	}
	
	/**
	 * Draws several lines of text within a rect
	 * @param s String to draw
	 * @param r Rectangle to draw within
	 */
	public void drawMultiLineCenteredString(String s, Rect r){		
		int centerX = r.getX() + r.getWidth() /2;
		int centerY = r.getY() + r.getHeight() /2;

		int lineHeight = fm.getAscent();
		
		String[] lines = s.split("\n");
		double ctr = 0;
		for(String line : lines){
			
			double adj = ctr - (((double)lines.length)-1)/2.0;
			int lineOffset = (int)(adj*lineHeight);
			
			drawCenteredString(line, centerX, centerY +  lineOffset);
			ctr++;
		}
	}
	
	/**
	 * Draws a string centered on the point (centerX, centerY)
	 * @param s	String to draw
	 * @param centerX - X Coordinate to center string on 
	 * @param centerY - Y Coordinate to center string on 
	 */
	public void drawCenteredString(String s, int centerX, int centerY){		
		Rectangle2D bounds = fm.getStringBounds(s, g);
		
		int x = (int)(centerX - bounds.getWidth()/2);
		int y = (int)(centerY - (fm.getAscent() + fm.getDescent())/2 + fm.getAscent());
		
		g.drawString(s, x, y);
	}
	
	/**
	 * Sets the color to draw
	 * @param c - Color to draw
	 */
	public void setColor(Color c){
		g.setColor(c);
	}
	
	
	/**
	 * Draws a line between the input points
	 * Wrapper for drawLine method in graphics
	 * @param x1 - X Coordinate of first point
	 * @param y1 - Y Coordinate of first point
	 * @param x2 - X Coordinate of second point
	 * @param y2 - Y Coordinate of second point
	 */
	public void drawLine(int x1, int y1, int x2, int y2){
		g.drawLine(x1, y1, x2, y2);
	}
	
	/**
	 * Fills a rectangle
	 * @param r - Rect to fill
	 */
	public void fillRect(Rect r){
		r.fill(g);
	}
	
	/**
	 * Draws a rectangle
	 * @param r - Rect to draw
	 */
	public void drawRect(Rect r){
		r.draw(g);
	}
	
	/**
	 * Draws a circle
	 * @param c - Circle to draw
	 */
	public void drawCircle(Circle c){
		c.draw(g);		
	}
	
	/**
	 * Draws a rectangle with a given outline width
	 * @param r - Rectangle to draw
	 * @param weight - Width of outline
	 */
	public void drawRect(Rect r, int weight){
		r.draw(g, weight);
	}
}