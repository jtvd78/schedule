package com.hoosteen.schedule;

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

public class Course extends Node {
	
	int credits;
	Color color;
	String courseID;
	String courseName;
	String description;
	GenEdSubcat[] subCats;
	
	int numOfSubCats;
	
	public Course(Element e, Color color){	
		
		this.color = color;
		updateCourse(e);
		initSections();
		
	}
	
	private void updateCourse(Element e) {
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
	
	public String getName(){
		return courseName;
	}
	
	public Color getColor(){
		return color;
	}
	
	public boolean equalsCourse(Course course){		
		//Two courses are equal if they have the same ID
		if(courseID.equals(course.getCourseID())){
			return true;
		}else{
			return false;
		}
	}
	
	private GenEdSubcat[] getSubCats(Element e){
		Elements cats = e.select(".course-subcategory");
		GenEdSubcat[] out = new GenEdSubcat[cats.size()]; 
		for(int i = 0; i < cats.size(); i++){
			for(GenEdSubcat ges : GenEdSubcat.values()){
				if(cats.get(i).text().contains(ges.toString())){
					out[i] = ges;
				}
			}
		}
		
		String str = e.select(".gen-ed-codes-group").text();
		numOfSubCats = str.length() - str.replace(",", "").length() + 1;
				
		
		return out;
	}
	
	public String toString(){
		return courseID + ": " + Tools.arrToString(subCats, ",") + " : " + courseName;
	}
	
	public int getNumOfSubCats(){
		return numOfSubCats;
	}
	
	public GenEdSubcat[] getSubCats(){
		return subCats.clone();
	}
	
	/**
	 * @return String representing courseID
	 */
	public String getCourseID(){
		return courseID;
	}
	


	public void refresh() {
		try {
			Document doc = Jsoup.connect(URLMaker.getCourseURL(courseID)).get();
			Elements courseElements = doc.select(".course");
			
			for(int i = 0; i < courseElements.size(); i++){	
				updateCourse(courseElements.get(i));				
			}	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		updateSections();
	}

	
	
	private void updateSections(){
		try {
			Document times = Jsoup.connect(URLMaker.getCourseTimeURL(courseID)).get();
			Elements sectionElements = times.select(".section");
			
			for(Node n : this){
				Section sect = (Section)n;
				
				String id = sect.getSectionID();				
				
				//Makes sure that a section's times include standard times.
				for(int i = 0; i < sectionElements.size(); i++){
					Element sectionElement = sectionElements.get(i);					
					
					if(sectionElement.select(".section-day-time-group").text().contains("-")){
						String elementSectionID = sectionElement.select(".section-id-container").text();
						if(id.equals(elementSectionID)){
							sect.refresh(sectionElement);
						}
					}
				}
			}
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}		
	}
	
	private void initSections(){		
		try {
			Document times = Jsoup.connect(URLMaker.getCourseTimeURL(courseID)).get();
			Elements sectionElements = times.select(".section");			
			
			//Makes sure that a section's times include standard times.
			for(int i = 0; i < sectionElements.size(); i++){
				if(sectionElements.get(i).select(".section-day-time-group").text().contains("-")){
					addNode(new Section(sectionElements.get(i), this));
				}
			}
			
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void addPopupMenuOptions(JPopupMenu popupMenu) {	
		
		super.addPopupMenuOptions(popupMenu);
		
		popupMenu.add(new AbstractAction("Show Description"){
			public void actionPerformed(ActionEvent e) {
				Tools.displayText(getDescription(), toString());
			}	
		});
	}
}