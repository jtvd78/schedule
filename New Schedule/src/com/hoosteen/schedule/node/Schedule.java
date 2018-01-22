package com.hoosteen.schedule.node;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.hoosteen.Tools;
import com.hoosteen.schedule.GenEdSubcat;
import com.hoosteen.schedule.ScheduleStart;
import com.hoosteen.schedule.Time;
import com.hoosteen.schedule.URLMaker;
import com.hoosteen.schedule.settings.ScheduleSettings;
import com.hoosteen.tree.Node;

public class Schedule extends Node{
	
	/**
	 * Creates an Schedule Node. It starts out as expanded
	 */
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
	 * Adds a given GenEdSubcat  to the schedule
	 * @param ges - GenEdSubcat to add to Schedule
	 */
	public void addGenEdSubcat(GenEdSubcat ges){
		
		// Get the GenEd URL
		String url = URLMaker.getGenEdURL(ges);
		try {
			
			//Download the HTMl and parse it
			Document doc = (Document) Jsoup.connect(url).get();
			
			//Read the Document (Adds the containing courses)
			readDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Removes any sections that have no open seats
	 */
	public void removeFullSections(){
		ArrayList<Node> removeList = new ArrayList<Node>();
		
		//Loop through any section
		for(Node course : this){
			for(Node sec : course){
				Section section = (Section)sec;
				
				//Find ones with no open seats
				if(section.getOpenSeats() < 1){
					
					//Save section to be removed
					removeList.add(section);
				}
			}
		}
		
		//Remove all pre-found Sections
		for(Node rem : removeList){
			rem.remove();
		}
	}
	
	/**
	 * Removes any duplicate courses from the list
	 */
	public void removeDuplicateCourses(){
		
		ArrayList<Node> removeList = new ArrayList<Node>();
		
		//Loop through the couses within this Node
		for(int i = 0; i < this.size(); i++){
			for(int ii = i + 1; ii < this.size(); ii++){
				
				//Get the courses to be compared
				Course course1 = (Course)(getNode(i));
				Course course2 = (Course)(getNode(ii));
				
				//Compare the courses. If they are different courses, and have the same ID
				//Save the course to be removed
				if(course1 != course2 && course1.equalsCourse(course2)){
					removeList.add(course2);
				}
			}
		}
		
		//Remove all pre-found Sections
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
		
		ScheduleSettings settings = ScheduleStart.getSettings();
		GenEdSubcat[] enabled = settings.getEnabledGenEds();
		
		
		ArrayList<GenEdSubcat> enabledGenEds = new ArrayList<>(enabled.length);
		for(GenEdSubcat sub : enabled) {
			enabledGenEds.add(sub);
		}
		
		//Loop through every course
		for(Node cour : this){
			Course course = (Course) cour;
			
			int count = 0;
			
			GenEdSubcat[] courseCats = course.getSubCats();
			
			for(int c = 0; c < courseCats.length; c++){
				if(enabledGenEds.contains(courseCats[c])){
					count++;
					if(count == lowerThan){
						break;
					}
				}
			}
			
			if(count < lowerThan || course.getNumOfSubCats() < 2){
				removeList.add(course);
			}
			
			
		}
		
		//Remove all pre-found Sections
		for(Course rem : removeList){
			removeNode(rem);
		}
	}
	
	/**
	 * Refresh all the Courses within the Schedule
	 */
	public void refreshCourses(){
		for(Node children : this){
			Course course = (Course) children;
			course.refresh();
		}
	}
	
	/**
	 * Reads a document, and scans for each course. 
	 * Assigns a random color to the new course
	 * @param doc The Document to read
	 */
	private void readDocument(Document doc){
		readDocument(doc, null);
	}
	
	/**
	 * Reads a document, and scans for each course. 
	 * Assigns the color give, or if color is null, assigns a random color
	 * @param doc The Document to read
	 * @param color The display color to assign to the courses in the document
	 */
	private void readDocument(Document doc, Color color){		
		Elements courseElements = doc.select(".course");
		
		//Create a thread pool to load the courses in parallel
		//The number of threads in the pool is equal to the computers thread count
		int cores = Runtime.getRuntime().availableProcessors();
		ExecutorService pool = Executors.newFixedThreadPool(cores);
		
		//Loop through each course found in the document
		for(int i = 0; i < courseElements.size(); i++){
			
			//If color is null, assign a random color
			if(color == null){
				color = Tools.getRandomColor();
			}
			
			//Submit the job to the thread pool
			pool.submit(new AddCourseWorker(courseElements.get(i), color));		
		}
		
		/**
		 * Shutdown the pool, set 5 minute time out
		 */
		try {
			pool.shutdown();
			pool.awaitTermination(5, TimeUnit.MINUTES);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * A Runnable which adds a course as a Node to the Schedule
	 * @author Justin
	 *
	 */
	class AddCourseWorker implements Runnable{
		
		//Color to assign to the course
		Color c;
		
		//Element to parse within the Course
		Element e;		
		
		/**
		 * Creates an AddCourseWorker with an Element e and a display Color c
		 * @param e Element to parse
		 * @param c Display color of the Course
		 */
		public AddCourseWorker(Element e, Color c){
			this.e = e;
			this.c = c;
		}

		@Override
		public void run() {
			
			//Add a new Course as a Node to this Node
			addNode(new Course(e,c));
		}
		
	}
	
	/**
	 * Adds a course, with a given ID, to the schedule, with the give color 
	 * @param course - Course ID String to add
	 * @param color - Color to add the course as
	 */
	public void addCourseById(String course, Color color){
		
		//Get the Course URL
		String url = URLMaker.getCourseURL(course);
		
		//Download the Document, parse it, and read the Courses within
		try {
			Document doc = (Document) Jsoup.connect(url).get();
			readDocument(doc, color);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes sections (on any day) that fall between the two given times
	 * @param start - Beginning Time
	 * @param end - Ending Time
	 */
	public void removeSectionsBetween(Time start, Time end){
		
		//Creates a dummy ClassTime between the start and end time
		ClassTime classTime = new ClassTime(start, end);
		
		ArrayList<Section> removeList = new ArrayList<Section>();
		
		//Loops through the courses and sections to get each ClassTime
		for(Node course : this){
			
			for(Node sec : course){
				Section section = (Section) sec;
				
				//Test the lecture
				if(section.getLecture().conflicts(classTime)){
					
					//Remove the lecture's section
					removeList.add(section);
					continue;
				}
				
				//Loop through the discussions
				for(ClassTime dis : section.getDiscussionTimes()){
					
					//Test the discussion
					if(dis.conflicts(classTime)){
						
						//Remove the discussion's section
						removeList.add(section);
						break;
					}
				}				
			}
		}
		
		//Remove all pre-found Sections
		for(Section rem : removeList){
			rem.remove();
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
				if(classTime.conflicts(lecture) && !lecture.hasSameCourse(classTime)){
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
			rem.remove();
		}
	}
}