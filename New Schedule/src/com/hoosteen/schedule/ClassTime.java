package com.hoosteen.schedule;

import java.awt.Color;

import org.jsoup.nodes.Element;

import com.hoosteen.helper.Tools;
import com.hoosteen.tree.Node;
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
}