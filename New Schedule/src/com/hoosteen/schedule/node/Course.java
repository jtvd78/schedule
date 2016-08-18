package com.hoosteen.schedule.node;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hoosteen.tree.Node;
import com.hoosteen.Tools;
import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.schedule.URLMaker;

public class Course extends Node {
	
	/*
	 * The number of credits in a Course
	 */
	int credits;
	
	/**
	 * The display color of a course
	 */
	Color displayColor;
	
	/**
	 * The String ID of a course
	 */
	String courseID;
	
	/**
	 * The name of the course
	 */
	String courseName;
	
	/**
	 * The course description
	 */
	String description;
	
	/**
	 * The number of GenEds that the course will fulfill
	 */
	int numOfSubCats;
	
	/**
	 * The array of all of the GenEds that the course falls within
	 */
	GenEdSubcat[] subCats;	
	
	/**
	 * Creates a course with an Element from the Schedule of Classes, and a display color
	 * @param e The HTML Element
	 * @param color The display color
	 */
	public Course(Element e, Color displayColor){	
		
		this.displayColor = displayColor;
		
		//Get the data
		initCourse(e);
		initSections();
		
		//Show the description when right clicked
		this.addRightClickOption(new AbstractAction("Show Description"){
			public void actionPerformed(ActionEvent e) {
				Tools.displayText(getDescription(), toString());
			}	
		});		
	}
	
	/**
	 * Sets up the course's instance variables (data from the HTML)
	 * @param e The Element from the HTML
	 */
	private void initCourse(Element e) {
		courseID = e.attr("id");
		subCats = getSubCats(e);
		courseName = e.select(".course-title").text();
		description = e.select(".approved-course-text").text();
		credits = Integer.parseInt(e.select(".course-min-credits").text());	
	}	

	/**
	 * @return Description of the current course.
	 */
	public String getDescription(){
		return courseID + ": " + courseName + "\nGen Ed Subcategories: " +
				Tools.arrToString(subCats, ",") + "\nCredits: " +
				credits + "\nDescription: " + description;
	}	
	
	/**
	 * A course is not hidden when any of its children are not hidden
	 */
	@Override
	public boolean isHidden(){
		for(Node n : this){
			if(!n.isHidden()){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Gets the course name
	 * @return The name of the course
	 */
	public String getName(){
		return courseName;
	}
	
	@Override
	public Color getDisplayColor(){
		return displayColor;
	}
	
	/**
	 * Determines whether two courses are the same course
	 * @param course The course to test
	 * @return The result
	 */
	public boolean equalsCourse(Course course){		
		//Two courses are equal if they have the same ID
		if(courseID.equals(course.getCourseID())){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * @return String representing courseID
	 */
	public String getCourseID(){
		return courseID;
	}
	
	/**
	 * Refreshes the Course and its contents (Sections)
	 */
	public void refresh() {
		try {
			Document doc = Jsoup.connect(URLMaker.getCourseURL(courseID)).get();
			Elements courseElements = doc.select(".course");
			
			for(int i = 0; i < courseElements.size(); i++){
				
				Element e = courseElements.get(i);
				
				if(e.attr("id").equals(courseID)){
					initCourse(e);
					break;
				}				
				
			}	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateSections();
	}
	
	/**
	 * Updates the sections within the Course
	 */
	private void updateSections(){
		try {
			
			//Get the course HTML and get each Section
			Document times = Jsoup.connect(URLMaker.getCourseTimeURL(courseID)).get();
			Elements sectionElements = times.select(".section");
			
			//Loop through each node (Section) within this Course
			for(Node n : this){
				Section sect = (Section)n;			
				
				//Loop through the sections and find the correct one
				for(int i = 0; i < sectionElements.size(); i++){
					Element sectionElement = sectionElements.get(i);					
					
					//Makes sure that a section's times include standard times.
					//Its gotta be a valid Section (If it has a dash its valid)
					if(sectionElement.select(".section-day-time-group").text().contains("-")){
						String elementSectionID = sectionElement.select(".section-id-container").text();
						
						//Match the Element section with the Section to update
						if(sect.getSectionID().equals(elementSectionID)){
							
							//Refresh the correct section
							sect.refresh(sectionElement);
						}
					}
				}
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}		
	}
	
	/**
	 * Initializes the Sections within the Course
	 */
	private void initSections(){		
		try {
			//Get the course HTML and get each Section
			Document times = Jsoup.connect(URLMaker.getCourseTimeURL(courseID)).get();
			Elements sectionElements = times.select(".section");			
			
			//Loop through the sections in the Element
			for(int i = 0; i < sectionElements.size(); i++){
				
				//Makes sure that a section's times include standard times.
				//Makes sure that a section's times include standard times.
				if(sectionElements.get(i).select(".section-day-time-group").text().contains("-")){
					
					//Add the new Section to this Course as a Node
					addNode(new Section(sectionElements.get(i), this));
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * Loads the Course's GenEds from the HTML Element
	 * Also loads the number of subcategories
	 * @param e The HTML Element containing the GenEd data
	 * @return The array of SubCats that the Course falls within
	 */
	private GenEdSubcat[] getSubCats(Element e){
		
		//Select each gen ed
		Elements cats = e.select(".course-subcategory");
		
		//Create the containing array
		GenEdSubcat[] out = new GenEdSubcat[cats.size()]; 
		
		//Loop through each GenEd Element and initialize the GenEd
		for(int i = 0; i < cats.size(); i++){
			for(GenEdSubcat ges : GenEdSubcat.values()){
				if(cats.get(i).text().contains(ges.toString())){
					out[i] = ges;
				}
			}
		}
		
		//Determines the number of SubCats that the course has
		//Does so by adding 1 to the number of commas in the GenEd string
		String str = e.select(".gen-ed-codes-group").text();		
		numOfSubCats = str.length() - str.replace(",", "").length() + 1;
		
		return out;
	}
	
	/**
	 * Returns the amount of Subcategories that the Course will fulfill. 
	 * @return
	 */
	public int getNumOfSubCats(){
		return numOfSubCats;
	}
	
	/**
	 * Gets the list of GenEds that the course falls within
	 * @return An array of GenEdSubcats
	 */
	public GenEdSubcat[] getSubCats(){
		return subCats.clone();
	}
	
	public String toString(){
		return courseID + ": " + Tools.arrToString(subCats, ",") + " : " + courseName;
	}
}