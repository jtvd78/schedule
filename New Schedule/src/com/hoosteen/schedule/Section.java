package com.hoosteen.schedule;

import java.awt.Color;

import javax.swing.plaf.synth.SynthSeparatorUI;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hoosteen.Tools;
import com.hoosteen.tree.Node;


public class Section extends Node{
	
	private int openSeats;
	private int totalSeats;	
	private int waitlistSize;
	
	private ClassTime lectureTime;
	private ClassTime[] discussionTimes;
	
	private String sectionID;
	private String professor;
	
	private Course course;
	
	public Section(Element e, Course course){
		this.course = course;
		updateSection(e);
		initClassTimes(e);		
	}
	
	private void updateSection(Element e){
		sectionID = e.select(".section-id-container").text();
		professor = e.select(".section-instructor").text();
		
		openSeats = Integer.parseInt(e.select(".open-seats-count").text());
		totalSeats = Integer.parseInt(e.select(".total-seats-count").text());
		waitlistSize = Integer.parseInt(e.select(".waitlist-count").get(0).text());
	}
	
	public int getOpenSeats(){
		return openSeats;
	}
	
	public Course getCourse(){
		return course;
	}
	
	public String toString(){
		return sectionID + " : " + professor + " : " + lectureTime + " - " + "Total: " + totalSeats + " Open: " + openSeats + " WL: " + waitlistSize;
	}
	
	public ClassTime getLecture(){
		return lectureTime;
	}
	
	public ClassTime[] getDiscussionTimes(){
		return (ClassTime[]) discussionTimes.clone();
	}
	
	public String getSectionID(){
		return sectionID;
	}
	
	public Color getColor(){
		return getParent().getColor();
	}

	public void refresh(Element sectionElement) {
		updateSection(sectionElement);
		updateClassTimes(sectionElement);
	}
	
	private void initClassTimes(Element sectionElement){
		//times
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
		
		int skip = 0;
		while(!times.get(skip).text().contains("-")){
			skip++;
		}		
		
		lectureTime = new ClassTime(times.get(skip),this, true);
		addNode(lectureTime);		
		
		discussionTimes = new ClassTime[size-1];
		for(int i = 1; i < size; i++){
			
			if(times.get(i).text().contains("Class time/details on ELMS")){
				skip++;
				continue;
			}
			
			discussionTimes[i-1] = new ClassTime(times.get(i),this, false);		
			addNode(discussionTimes[i-1-skip]);
		}
	}
	
	private void updateClassTimes(Element sectionElement){
		/*
		//times
		Elements times = sectionElement.select(".class-days-container").select(".row");		
				
		
		
		
		for(Node n : this){
			ClassTime ct = (ClassTime)n;
			
			
		}
		*/
	}
}