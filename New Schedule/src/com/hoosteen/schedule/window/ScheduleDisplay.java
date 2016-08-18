package com.hoosteen.schedule.window;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

import com.hoosteen.graphics.GraphicsWrapper;
import com.hoosteen.graphics.Rect;
import com.hoosteen.schedule.Time;
import com.hoosteen.schedule.node.ClassTime;
import com.hoosteen.schedule.node.Schedule;
import com.hoosteen.tree.Node;
import com.hoosteen.tree.TreeComp;

/**
 * Displays a Schedule
 * @author justin
 *
 */
public class ScheduleDisplay extends JComponent{
	
	/**
	 * Current location of the mouse on the ScheduleDisplay
	 */
	Point mouse;
	
	/**
	 * Schedule to be displayed in the ScheduleDisply
	 */
	Schedule schedule;
	
	/**
	 * Whether or not to display the current time
	 * Set to disabled when the mouse leaves the component
	 */
	boolean drawCurrentTime = true;
	
	/**
	 * TreeComp on the right side of the screen
	 */
	TreeComp treeComp;
	
	/**
	 * Width of component
	 */
	int width;
	
	/**
	 * Height of component
	 */
	int height;
	
	/**
	 * Width of each day
	 */
	double dayWidth;
	
	/**
	 * Minutes per every y-pixel
	 */
	double minPerPixelY;
	
	//Settings
	
	/**
	 * Transparency of tranparent classTimes
	 */
	int hoverTransparency = 255/3; //Out of 255
	
	/**
	 * Time at the top of the ScheduleDisplay
	 */
	double startTime = 6; //0.0-24.0 - 6.5 would be 6:30 
	
	/**
	 * Time at the bottom of the ScheduleDisplay
	 */
	double endTime = 9 + 12;

	/**
	 * Color to draw the time string
	 */
	Color timeStringColor = Color.RED;
	
	/**
	 * Color of background grid
	 */
	Color backgroundGridColor = new Color(70,70,70);
	
	
	/**
	 * Creates a ScheduleDisplay with a Schedule and a TreeComp
	 * @param schedule Schedule to display
	 * @param treeComp TreeComp at the right of the screen
	 */
	public ScheduleDisplay(Schedule schedule, TreeComp treeComp){
		
		this.treeComp = treeComp;
		this.schedule = schedule;
		
		//Init mouse point
		mouse = new Point(0,0);
		
		//Add input listeners
		Listener l = new Listener();
		addMouseMotionListener(l);
		addMouseListener(l);
	}	
	
	/**
	 * Updates select instance variables
	 */
	private void update(){
		width = getWidth();
		height = getHeight();
		
		dayWidth = width/5.0;
		minPerPixelY = (endTime - startTime)*60.0/height;			
	}
	
	
	/**
	 * Draws the schedule on the Graphics object g
	 */
	public void paintComponent(Graphics oldG){
		ScheduleGraphics g = new ScheduleGraphics(oldG);
		
		//Updates some stuff
		update();	
		
		//Draw background
		g.drawBackground();
		
		//Loop through courses. Draw each ClassTime
		for(Node course : schedule){
			for(Node section : course){
				for(Node classTime : section){
					if(!classTime.isHidden() || isTransparent(classTime)){
						g.drawClassTime((ClassTime)classTime);
					}
				}
			}
		}
		
		//Current Time marker
		if(drawCurrentTime){
			g.drawCurrentTime();
		}
	}
	
	/**
	 * A node is transparent if its parent is transparent, or if it is currently selected. 
	 * @return boolean, transparent or not. 
	 */
	public boolean isTransparent(Node n){
		
		//The top node is never transparent
		//I wouldn't say never, but for the purposes of this class, it won't be
		//I will come back and change this if I need to. 
		if(n.getLevel() == 0){
			return false;
		}
		
		return n.isHidden() && (n == treeComp.getSelectedNode()) || isTransparent(n.getParent());
	}
	
	/**
	 * Extension of my graphics wrapper. 
	 * Adds methods for performing ScheduleDisplay options. 
	 * @author Justin
	 *
	 */
	class ScheduleGraphics extends GraphicsWrapper{
		
		public ScheduleGraphics(Graphics g) {
			super(g);
		}
		
		/**
		 * Draws black background
		 * Draws verical line for each day
		 * Draws a horizontal line for each hour
		 */
		void drawBackground(){
			
			//Background
			g.setColor(Color.BLACK);		
			g.fillRect(0, 0, width, height);			
			
			//Vertical Lines
			g.setColor(backgroundGridColor);
			for(int i = 0; i < 5; i++){
				g.drawLine(i*getWidth()/5,0,i*getWidth()/5,getHeight());
			}
			
			//Horizontal Lines
			int startHour = (int) Math.floor(startTime);
			for(int h = startHour; h < (int) Math.ceil(endTime); h++){
				int y = (int)((h-startHour)*60/minPerPixelY);
				g.drawLine(0, y , width, y);
			}
		}
		
		/**
		 * Draws a ClassTimeto this Graphics object with this ClassTime outlined in their color. 
		 * @param ct ClassTime to draw
		 */
		void drawClassTime(ClassTime ct){
			
			//Outline color is red, unless the class is a lecture. Then it is green.
			Color outlineColor = Color.RED;
			if(ct.isLecture()){
				outlineColor = Color.GREEN;
			}			
			
			
			//Loop through each of the ClassTime's days
			for(Time.Day d : ct.getDayList()){
				
				//Timing of ClassTime
				int day = getDayInt(d);
				int ctStartMin = ct.getStartTime().toMinutes();
				int ctEndMin = ct.getEndTime().toMinutes();
				int classLengthMin = ctEndMin-ctStartMin;
				
				//Creates the Rect for this clastime
				int ctX = (int)(day*dayWidth);
				int ctY = (int)((ctStartMin-startTime*60.0)/minPerPixelY);
				int ctWidth = (int)dayWidth;
				int ctHeight = (int)(classLengthMin/minPerPixelY);				
				Rect ctRect = new Rect(ctX, ctY, ctWidth, ctHeight);
				
				//Make ClassTime transparent if need be
				Color ogColor = ct.getDisplayColor();
				if(isTransparent(ct)){
					setColor( new Color(ogColor.getRed(), ogColor.getGreen(), ogColor.getBlue(), hoverTransparency));
					outlineColor = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), hoverTransparency);
				}else{
					setColor(ogColor);
				}			
								
				//Actual rectangle
				fillRect(ctRect);
				
				//Outline
				setColor(outlineColor);
				drawRect(ctRect, 2);
				
				//Draw ClassTime info
				setColor(Color.black);
				String toDraw = ct.getCourseID() + "\n" + ct.getSectionID();
				drawMultiLineCenteredString(toDraw, new Rect(ctX, ctY, ctWidth, ctHeight));
			}
		}
		
		/**
		 * Draws the current time, along with a corresponding horizontal line, at the mouse's Y coordinate.
		 */
		void drawCurrentTime(){
			
			int mouseY = (int) mouse.getY();			
			
			//String to draw
			String timeString = new Time((int)(mouseY*minPerPixelY + startTime * 60.0)).toString();
			
			//Get string width and construct Rect to display behind the String
			int stringWidth = (int) g.getFontMetrics().getStringBounds(timeString, g).getWidth();
			Rect r = new Rect((int)mouse.getX() + 10, (int)( mouse.getY()), stringWidth + 10, 20);

			//Fill the Rect with half transparency
			g.setColor(new Color(0,0,0,128));
			fillRect(r);
			
			//Draw the string and the marker line
			g.setColor(timeStringColor);
			g.drawLine(0, mouseY, width, mouseY);	
			drawString(timeString, r);	
		}
	}
	
	/**
	 * Returns the column number of a given day (starts at 0)
	 * @param d Day to get int of
	 * @return Given a day, returns the day of the week, excluding the weekends. Defaults to -1 if day does not exist. 
	 */
	public int getDayInt(Time.Day d){
		switch(d){
		case M: return 0;
		case Tu: return 1;
		case W: return 2;
		case Th: return 3;
		case F: return 4;
		default: return -1;
		}
	}
	
	/**
	 * Updates x and y mouse positions for use when drawing the component. 
	 * Also repaints component whenever mouse motion is detected. 
	 * @author justin
	 *
	 */
	private class Listener implements MouseMotionListener, MouseListener{

		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouse.setLocation(e.getX(), e.getY());
			repaint();
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			drawCurrentTime = true;
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			drawCurrentTime = false;
			repaint();
		}
		
		public void mouseClicked(MouseEvent arg0) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}