package com.hoosteen.schedule;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hoosteen.Tools;
import com.hoosteen.tree.Node;
import com.hoosteen.tree.Tree;

public class Schedule extends Node{
	
	public Schedule(){
		super(true);
	}
	
	/**
	 * Merges this schedule with a given schedule
	 * Essentially appends the content of the given schedule to the current one. 
	 * @param s - Schedule to merge with
	 */
	public void merge(Schedule s){
		for(Node n : s){
			addNode(n);
		}
	}
	
	/**
	 * Adds a given gen ed subcat to the schedule
	 * @param ges - GenEdSubcat to add to Schedule
	 */
	public void addGenEdSubcat(GenEdSubcat ges){
		String url = URLMaker.getGenEdURL(ges);
		try {
			Document doc = (Document) Jsoup.connect(url).get();
			readDocument(doc);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void removeEmptySections(){
		ArrayList<Node> removeList = new ArrayList<Node>();
		
		for(Node course : this){
			for(Node sec : course){
				Section section = (Section)sec;
				if(section.getOpenSeats() < 1){
					removeList.add(section);
				}
			}
		}
		
		for(Node rem : removeList){
			Tree.remove(rem);
		}
	}
	
	/**
	 * Removes any duplicate courses from the list
	 */
	public void removeDuplicateCourses(){
		
		ArrayList<Node> removeList = new ArrayList<Node>();
		
		for(int i = 0; i < this.size(); i++){
			for(int ii = i + 1; ii < this.size(); ii++){
				
				Course course1 = (Course)(getNode(i));
				Course course2 = (Course)(getNode(ii));
				
				
				if(course1 != course2 && course1.equalsCourse(course2)){
					removeList.add(course2);
				}
			}
		}
		
		for(Node rem : removeList){
			removeNode(rem);
		}
	}
	

	
	/**
	 * Removes all courses within the schedule which have a gen-ed number of less than the input parameter
	 * @param lowerThan - Courses with a gen ed count lower than this will get removed
	 */
	public void removeCoursesWithLowerGenEdSubcats(int lowerThan){
		
		ArrayList<Course> removeList = new ArrayList<Course>();
		
		for(Node cour : this){
			Course course = (Course) cour;
			if(course.getNumOfSubCats() < lowerThan){
				removeList.add(course);
			}
		}
		
		for(Course rem : removeList){
			removeNode(rem);
		}
	}
	
	public void refreshCourses(){
		for(Node children : this){
			Course course = (Course) children;
			course.refresh();
		}
	}
	
	/**
	 * Reads a document, and scans for each course. 
	 * Assigns a random color to the new course
	 * @param doc
	 */
	private void readDocument(Document doc){
		readDocument(doc, null);
	}
	
	/**
	 * Reads a document, and scans for each course. 
	 * Assigns the color give, or if color is null, assigns a random color
	 * @param doc
	 * @param color
	 */
	private void readDocument(Document doc, Color color){		
		Elements courseElements = doc.select(".course");
		
		Runnable r = new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(8);
		
		for(int i = 0; i < courseElements.size(); i++){
			if(color == null){
				color = Tools.getRandomColor();
			}
			
			pool.submit(new NodeWorker(courseElements.get(i), color));		
		}
		
		try {
			pool.shutdown();
			pool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	class NodeWorker implements Runnable{
		
		Element e;
		Color c;
		
		public NodeWorker(Element e, Color c){
			this.e = e;
			this.c = c;
		}

		@Override
		public void run() {
			addNode(new Course(e,c));
		}
		
	}
	
	/**
	 * Adds a course, with a given ID, to the schedule, with the give color 
	 * @param course - Course ID String to add
	 * @param color - Color to add the course as
	 */
	public void addCourseById(String course, Color color){
		String url = URLMaker.getCourseURL(course);
		try {
			Document doc = (Document) Jsoup.connect(url).get();
			readDocument(doc, color);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes sections (on any day) that fall between the two given times
	 * @param start - Beginning Time
	 * @param end - Ending Time
	 */
	public void removeSectionsBetween(Time start, Time end){
		
		ClassTime classTime = new ClassTime(start, end);
		
		ArrayList<Section> removeList = new ArrayList<Section>();
		
		for(Node course : this){
			
			for(Node sec : course){
				Section section = (Section) sec;
				if(section.getLecture().conflicts(classTime)){
					removeList.add(section);
					continue;
				}
				
				for(ClassTime dis : section.getDiscussionTimes()){
					if(dis.conflicts(classTime)){
						removeList.add(section);
						break;
					}
				}				
			}
		}
		
		
		for(Section rem : removeList){
			Tree.remove(rem);
		}
	}
	
	/**
	 * Removes any classTimes in this schedule which conflict with the given classTime
	 * @param classTime - Class time to check conflicting against
	 */
	public void removeConflictingClasstimes(ClassTime classTime) {
		
		//You need to remove from tree after reading through tree 
		//In order to avoid ConcurrentModificationExceptions
		ArrayList<Node> removeList = new ArrayList<Node>();
		
		//Go through tree
		for(Node course : this){
			for(Node sec : course){
				Section section = (Section) sec;
				
				//Remove if:
				//The two nodes conflict AND
				//The two nodes are for separate courses
				
				ClassTime lecture = section.getLecture();
				if(classTime.conflicts(lecture)&& lecture.getSection().getCourse() != classTime.getSection().getCourse()){
					removeList.add(section);
					continue;
				}	
				
				for(ClassTime dis : section.getDiscussionTimes()){
					if(classTime.conflicts(dis)){
						removeList.add(section);
						break;
					}
				}
			}
		}
		
		//Remove all nodes in remove list
		for(Node rem : removeList){
			Tree.remove(rem);
		}
	}
}