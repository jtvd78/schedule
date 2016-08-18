package com.hoosteen.schedule.node;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.jsoup.nodes.Element;

import com.hoosteen.Tools;
import com.hoosteen.schedule.Time;
import com.hoosteen.tree.Node;

/**
 * ClassTime. Can be a lecture or not, but has a start time, end time, and a set of days on which it occurs on. 
 * @author Justin
 *
 */
public class ClassTime extends Node{
	
	/**
	 * When the ClassTime starts
	 */
	private Time startTime;
	
	/**
	 * When the ClassTime ends
	 */
	private Time endTime;
	
	/**
	 * The days that the ClassTime falls on
	 */
	private Time.Day[] days;
	
	/**
	 * Whether or not the ClassTime is a lecture
	 */
	private boolean lecture;
	
	/**
	 * The parent section of the ClassTime
	 */
	private Section section;
	
	/**
	 * Constructor that makes a ClassTime from an HTML Element e
	 * @param e HTML Element
	 * @param section parentSection
	 * @param lecture boolean if lecture or not
	 */
	public ClassTime(Element e, Section section, boolean lecture){
		
		days = getDays(e.select(".section-days").text());		
		startTime = new Time(e.select(".class-start-time").text());
		endTime = new Time(e.select(".class-end-time").text());
		this.lecture = lecture;
		this.section = section;
		
		//Allows the user to right click on a ClassTime and remove any ClassTimes that conflict with this
		this.addRightClickOption(new AbstractAction("Remove Conflicting Classtimes"){
			public void actionPerformed(ActionEvent e) {
				((Schedule)(getTopNode())).removeConflictingClasstimes(ClassTime.this);
				//No repaint here. Need to add it somehow
			}
		});
	}
	
	/**
	 * Creates a ClassTime starting and ending at the given times. 
	 * The classtime is not a lecture,
	 * and takes place on M, Tu, W, Th, and F
	 * @param startTime - Time that the ClassTime starts at
	 * @param endTime - Time that the ClassTime ends at
	 */
	public ClassTime(Time startTime, Time endTime){
		this.startTime = startTime;
		this.endTime = endTime;
		this.days = new Time.Day[]{Time.Day.M, Time.Day.Tu,Time.Day.W,Time.Day.Th,Time.Day.F};
		this.section = null;
		this.lecture = false;
	}
	
	/**
	 * Gets the course ID of the parent course
	 * @return The course ID
	 */
	public String getCourseID(){
		return section.getCourseID();
	}
	
	/**
	 * Gets the section ID of the parent section
	 * @return the section ID
	 */
	public String getSectionID(){
		return section.getSectionID();
	}
	
	/**
	 * Whether or not this ClassTime is a lecture
	 * @return If the ClassTime is a lecture
	 */
	public boolean isLecture(){
		return lecture;
	}
	
	/**
	 * Determines whether the input ClassTime has the same course as this ClassTime
	 * @param classTime The ClassTime to test against
	 * @return Whether or the ClassTimes have the same course
	 */
	public boolean hasSameCourse(ClassTime classTime) {
		return classTime.section.hasSameCourse(section);
	}
	
	/**
	 * Lists the days of the week that this ClassTime happens
	 * @return An array of the days this ClassTime occurs on
	 */
	public Time.Day[] getDayList(){
		return (Time.Day[])days.clone();
	}
	
	/**
	 * When the ClassTime starts
	 * @return The starting time
	 */
	public Time getStartTime(){
		return startTime;
	}
	
	/**
	 * When the ClassTime ends
	 * @return The ending time
	 */
	public Time getEndTime(){
		return endTime;
	}
	
	
	/**
	 * Gets the display color of the ClassTime
	 * @return The ClassTime's display color
	 */
	@Override
	public Color getDisplayColor(){
		return getParent().getDisplayColor();
	}
	 
	/**
	 * Determines whether a specified ClassTime conflicts with this ClassTime
	 * @param test - The ClassTime to test against
	 * @return The result
	 */
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
	
	/**
	 * Returns a description of the ClassTime
	 */
	public String getDescription(){
		return (lecture ? "Lecture: " : "Discussion: ") + Tools.arrToString(days, "") + "\nStart Time: " +
				startTime.toString() + "\nEndTime: " +
				endTime.toString();
	}

	public String toString(){
		return (lecture ? "Lecture " : "Discussion ") + "(" + startTime + " - " + endTime + " : " + Tools.arrToString(days,"") + ")";
	}
	
	/**
	 * Gets the days that a ClassTime occurs based on the a String which came from the Schedule of 
	 * Classes webpage
	 * @param daysString The string pulled from the Schedule of Classes page
	 * @return An array of Days which the ClassTime occurs on
	 */
	public static Time.Day[] getDays(String daysString){
		ArrayList<Time.Day> daysList = new ArrayList<Time.Day>();
		for(Time.Day d : Time.Day.values()){
			if(daysString.contains(d.toString())){
				daysList.add(d);
			}
		}
		
		Time.Day[] dayArr = new Time.Day[daysList.size()];
		for(int i = 0; i < dayArr.length; i++){
			dayArr[i] = daysList.get(i);
		}
		
		return dayArr;
	}
}