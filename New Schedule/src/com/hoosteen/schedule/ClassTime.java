package com.hoosteen.schedule;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.jsoup.nodes.Element;

import com.hoosteen.helper.Tools;
import com.hoosteen.tree.Node;
import com.hoosteen.tree.Node.DescriptionAction;


/**
 * ClassTime. Can be a lecture or not, but has a start time, end time, and a set of days on which it occurs on. 
 * @author justi
 *
 */
public class ClassTime extends Node{
	
	private Time startTime;
	private Time endTime;
	private Time.Day[] days;
	private boolean lecture;
	private Section section;
	
	/**
	 * Constructor that makes a ClassTime from an HTML Element e
	 * @param e HTML Element
	 * @param section parentSection
	 * @param lecture boolean if lecture or not
	 */
	public ClassTime(Element e, Section section, boolean lecture){
		days = Time.getDays(e.select(".section-days").text());		
		startTime = new Time(e.select(".class-start-time").text());
		endTime = new Time(e.select(".class-end-time").text());
		this.lecture = lecture;
		this.section = section;
	}
	
	/**
	 * Creates a ClassTime starting and ending at the given times. 
	 * The classtime is not a lecture,
	 * and takes place on M, Tu, W, Th, and F
	 * @param startTime
	 * @param endTime
	 */
	public ClassTime(Time startTime, Time endTime){
		this.startTime = startTime;
		this.endTime = endTime;
		this.days = new Time.Day[]{Time.Day.M, Time.Day.Tu,Time.Day.W,Time.Day.Th,Time.Day.F};
		this.section = null;
		this.lecture = false;
	}
	
	/**
	 * Returns a description of the ClassTime
	 */
	public String getDescription(){
		return (lecture ? "Lecture: " : "Discussion: ") + Tools.arrToString(days, "") + "\nStart Time: " +
				startTime.toString() + "\nEndTime: " +
				endTime.toString();
				
	}
	
	public Section getSection(){
		return section;
	}
	
	public boolean isLecture(){
		return lecture;
	}
	
	public Time.Day[] getDayList(){
		return (Time.Day[])days.clone();
	}
	
	public Time getStartTime(){
		return startTime;
	}
	
	public Time getEndTime(){
		return endTime;
	}
	
	public Color getColor(){
		return parent.getColor();
	}
	
	public Color getOutlineColor(){
		if(lecture){
			return Color.GREEN;
		}
		return Color.RED;
	}
	 
	public boolean conflicts(ClassTime test){
		
		
		//If two classTimes are the same, then they do no conflict, since they are the same classTime;
		if(this == test){
			return false;
		}
		
		
		for(Time.Day day : days){
			for(Time.Day testDay : test.getDayList()){
				if(day == testDay){
					if(test.getEndTime().toMinutes() >= startTime.toMinutes() && test.getEndTime().toMinutes() <= endTime.toMinutes()){
						//Test time ends while this ClassTime is in session
						return true;
					}else if(test.getStartTime().toMinutes() >= startTime.toMinutes() && test.getStartTime().toMinutes() <= endTime.toMinutes()){
						//Test time starts when this ClassTime is in session
						return true;
					}else if(test.getStartTime().toMinutes() <= startTime.toMinutes() && test.getEndTime().toMinutes() >= endTime.toMinutes()){
						//Test time surrounds current ClassTime
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String toString(){
		return (lecture ? "Lecture " : "Discussion ") + "(" + startTime + " - " + endTime + " : " + Tools.arrToString(days,"") + ")";
	}
		
	public void showPopupMenu(Component comp, int x, int y) {
		JPopupMenu jpu = new JPopupMenu();
		jpu.add(new DescriptionAction());
		jpu.add(new ConflictAction());
		jpu.show(comp, x, y);
	}
	
	/**
	 * Action that will display the description of a ClassTime
	 */
	class DescriptionAction extends AbstractAction{

		public DescriptionAction(){
			super("Show Descrition");
		}			
		
		public void actionPerformed(ActionEvent e) {				
			JTextArea jta = new JTextArea(getDescription());
			jta.setLineWrap(true);
			jta.setWrapStyleWord(true);
            JScrollPane jsp = new JScrollPane(jta);
            jsp.setPreferredSize(new Dimension(480, 320));
            JOptionPane.showMessageDialog( null, jsp, ClassTime.this.toString(), JOptionPane.DEFAULT_OPTION);			
		}
	}
	
	/**
	 * Action which will remove any nodes in the current tree that conflicts with the classtime. 
	 * Assumes that n is a classtime. 
	 * @author Justin
	 */
	class ConflictAction extends AbstractAction{
		
		public ConflictAction(){
			super("Remove conflicting classtimes");
		}			
		
		public void actionPerformed(ActionEvent e) {
			((Schedule)(getTree())).removeConflictingClasstimes(ClassTime.this);
			//No repaint here. Need to add it somehow
		}
	}
}
