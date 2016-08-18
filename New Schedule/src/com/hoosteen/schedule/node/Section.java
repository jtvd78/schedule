package com.hoosteen.schedule.node;

import java.awt.Color;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hoosteen.Tools;
import com.hoosteen.tree.Node;


public class Section extends Node{
	
	/**
	 * Number of open seats in the Section
	 */
	private int openSeats;
	
	/**
	 * Total number of seats in the Section
	 */
	private int totalSeats;	
	
	/**
	 * Size of the waitlist in the Section
	 */
	private int waitlistSize;
	
	/**
	 * The Lecture's ClassTime
	 */
	private ClassTime lectureTime;
	
	/**
	 * An array of the discussion ClassTimes
	 */
	private ClassTime[] discussionTimes;
	
	/**
	 * The ID of the Section
	 */
	private String sectionID;
	
	/**
	 * The Section's professor
	 */
	private String professor;
	
	/**
	 * The parent Course
	 */
	private Course course;
	
	/**
	 * Creates a Course with an HTML Element and the parent course
	 * @param e HTML Element from Schedule of Classes
	 * @param course The parent Course
	 */
	public Section(Element e, Course course){
		this.course = course;
		initSection(e);
		initClassTimes(e);		
	}
	
	/**
	 * Sets up the Section's instance variables (data from the HTML)
	 * @param e The Element from the HTML
	 */
	private void initSection(Element e){
		professor = e.select(".section-instructor").text();
		sectionID = e.select(".section-id-container").text();	
		openSeats = Integer.parseInt(e.select(".open-seats-count").text());
		totalSeats = Integer.parseInt(e.select(".total-seats-count").text());
		waitlistSize = Integer.parseInt(e.select(".waitlist-count").get(0).text());
	}
	
	/**
	 * Gets the number of open seats in the Section
	 * @return The number of open seats
	 */
	public int getOpenSeats(){
		return openSeats;
	}
	
	/**
	 * Gets the Section's lecture
	 * @return The Lecture
	 */
	public ClassTime getLecture(){
		return lectureTime;
	}
	
	/**
	 * Gets all of the discussion ClassTimes
	 * @return An array of ClassTimes
	 */
	public ClassTime[] getDiscussionTimes(){
		return (ClassTime[]) discussionTimes.clone();
	}
	
	/**
	 * Gets the Section's ID
	 * @return The Section's ID
	 */
	public String getSectionID(){
		return sectionID;
	}
	
	/**
	 * A Section's display color is the Course's display color
	 */
	@Override
	public Color getDisplayColor(){
		return getParent().getDisplayColor();
	}

	/**
	 * Refreshes a Section with the given HTML Element
	 * @param sectionElement The Element to refresh the Section with
	 */
	public void refresh(Element sectionElement) {
		initSection(sectionElement);
		updateClassTimes(sectionElement);
	}
	
	
	/**
	 * Initializes all of the ClassTimes within the Section
	 * @param sectionElement The element to init the ClassTime with
	 */
	private void initClassTimes(Element sectionElement){
		//Select the ClassTimes
		Elements times = sectionElement.select(".class-days-container").select(".row");		
		
		//Make sure classes have a "-" in their times to make sure we can read them
		int size = 0;
		for(int i = 0; i < times.size(); i++){
			if(times.get(i).text().contains("-")){
				size++;
			}
		}
		
		//Class does not have standard timing
		if(size == 0){
			return;
		}
		
		//Skips the first "rows" until you find one with a dash
		int skip = 0;
		while(!times.get(skip).text().contains("-")){
			skip++;
		}		
		
		//The lecture is the first ClassTime with a dash (Get from times with index skip)
		lectureTime = new ClassTime(times.get(skip), this, true);
		addNode(lectureTime);		
		
		//Init discussionTimes array
		discussionTimes = new ClassTime[size-1];
		
		//Loop through the rest of the Elements
		for(int i = 1; i < size; i++){
			
			//Skip any Elements with the following text
			if(times.get(i).text().contains("Class time/details on ELMS")){
				skip++;
				continue;
			}
			
			//Save the new discussion in the array
			discussionTimes[i-1] = new ClassTime(times.get(i),this, false);		
			
			//Add the new discussion to this Node
			addNode(discussionTimes[i-1-skip]);
		}
	}
	
	/**
	 * Updates the ClassTimes within the Section
	 * It removes all previous ClassTimes and re-initializes them from the given Element
	 * @param sectionElement The HTML Element to refresh the Section with
	 */
	private void updateClassTimes(Element sectionElement){
		clear();
		initClassTimes(sectionElement);
	}

	/**
	 * Gets the Course's ID
	 * @return The Course's ID
	 */
	public String getCourseID() {
		return course.getCourseID();
	}

	/**
	 * Determines whether the given Section has the same Course as this section
	 * @param section The section to test
	 * @return The result
	 */
	public boolean hasSameCourse(Section section) {
		return section.course == course;
	}
	
	public String toString(){
		return sectionID + " : " + professor + " : " + lectureTime + " - " + "Total: " + totalSeats + " Open: " + openSeats + " WL: " + waitlistSize;
	}
}