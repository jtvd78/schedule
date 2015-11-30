package com.hoosteen.window;

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
import com.hoosteen.schedule.ClassTime;
import com.hoosteen.schedule.Schedule;
import com.hoosteen.schedule.Time;
import com.hoosteen.tree.Node;

/**
 * Displays a Schedule
 * @author Justin
 *
 */
public class ScheduleDisplay extends JComponent{
	
	//Random Instance Variables
	Point mouse;
	Schedule schedule;
	boolean drawCurrentTime = true;
	
	//Helping Variables
	int width;
	int height;
	double minPerPixelX, minPerPixelY;
	double pixelPerDayY;
	
	//Settings
	int hoverTransparency = 50; //Out of 255
	double startTime = 6; //0.0-24.0 - 6.5 would be 6:30 
	double endTime = 9 + 12;
	
	/**
	 * Constructor
	 * @param Schedule to display
	 */
	public ScheduleDisplay(Schedule schedule){
		this.schedule = schedule;
		
		//Init mouse
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
		
		minPerPixelX = (endTime - startTime)*60.0/width;
		minPerPixelY = (endTime - startTime)*60.0/height;
		pixelPerDayY = ((double)width)/5;	
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
		
		//Loop through courses. Draw 
		for(Node course : schedule){
			for(Node section : course){
				for(Node classTime : section){
					if(!classTime.isHidden() || classTime.isTransparent()){
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
	 * Extension of my graphics wrapper. 
	 * Adds methods for performing ScheduleDisplay options. 
	 * @author justi
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
			g.setColor(new Color(70,70,70));
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
		 * Draws the current time, along with a corresponding horizontal line, at the mouse's Y coordinate.
		 */
		void drawCurrentTime(){
			int mouseY = (int) mouse.getY();
			
			g.setColor(Color.red);
			g.drawLine(0, mouseY, width, mouseY);		
			Time time = new Time((int)(mouseY*minPerPixelY + startTime * 60.0));
			
			drawString(time.toString(), new Rect((int)mouse.getX() + 10, (int)( mouse.getY()), 10, 20));	
		}
		
		/**
		 * Draws a ClassTime
		 * @param to this Graphics object
		 * @param with this ClassTime
		 * @param outlined in thic color. 
		 */
		void drawClassTime(ClassTime ct){
			
			//Outline color is red, unless the class is a lecture. Then it is green.
			Color outline = ct.getOutlineColor();
			
			for(Time.Day d : ct.getDayList()){
				
				//Timing of classtime
				int day = getDayInt(d);
				int ctStartMin = ct.getStartTime().toMinutes();
				int ctEndMin = ct.getEndTime().toMinutes();
				int classLengthMin = ctEndMin-ctStartMin;
				
				//X and Y coordinates, and Width and Height of box to be drawn for classtime
				int ctX = (int)(day*pixelPerDayY);
				int ctY = (int)((ctStartMin-startTime*60.0)/minPerPixelY);
				int ctWidth = (int)pixelPerDayY;
				int ctHeight = (int)(classLengthMin/minPerPixelY);
				
				//Make classtime transparent
				Color ogColor = ct.getColor();
				if(ct.isTransparent() && ct.isHidden()){
					setColor(new Color(ogColor.getRed(),ogColor.getGreen(), ogColor.getBlue(), hoverTransparency));
					outline = new Color(outline.getRed(), outline.getGreen(), outline.getBlue(), hoverTransparency);
				}else{
					setColor(ogColor);
				}			
								
				//Actual rectangle
				fillRect(ctX, ctY,ctWidth, ctHeight);
				
				//Outline
				setColor(outline);
				drawRect(ctX, ctY, ctWidth, ctHeight, 2);
				
				//Draw ClassTime info
				setColor(Color.black);
				String toDraw = ct.getSection().getCourse().getCourseID() + "\n" + ct.getSection().getSectionID();
				drawMultiLineCenteredString(toDraw, new Rect(ctX, ctY, ctWidth, ctHeight));
			}
		}
	}
	
	/**
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
	 * @author Justin
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